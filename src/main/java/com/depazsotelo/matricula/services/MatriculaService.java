package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.*;
import com.depazsotelo.matricula.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlumnoRepository alumnoRepository;
    private final AulaRepository aulaRepository;
    private final ConceptoRepository conceptoRepository;
    private final CuotaRepository cuotaRepository;
    private final AuditoriaService auditoriaService;

    @Transactional(rollbackFor = Exception.class)
    public Matricula registrarMatriculaTransaccional(Integer codAlumno, Integer codAula, Usuario usuarioRegistro,
                                                     HttpServletRequest request) throws Exception {

        // 1. Buscar Alumno y Aula
        Alumno alumno = alumnoRepository.findById(codAlumno)
                .orElseThrow(() -> new Exception("Alumno no encontrado"));
        Aula aula = aulaRepository.findById(codAula)
                .orElseThrow(() -> new Exception("Aula no encontrada"));
        AnioAcademico anio = aula.getAnioAcademico();

        // 2. Validar que no esté matriculado ese mismo año
        if (matriculaRepository.existsByAlumnoCodAlumnoAndAnioAcademicoCodAnioAcademico(codAlumno, anio.getCodAnioAcademico())) {
            throw new Exception("El alumno ya se encuentra matriculado en el año " + anio.getAnio());
        }

        // 3. Validar que el Aula tenga vacantes disponibles (contra capacidadMaxima, campo ya existente)
        long matriculasActivas = matriculaRepository.countByAulaCodAulaAndEstado(codAula, "activa");
        if (matriculasActivas >= aula.getCapacidadMaxima()) {
            throw new Exception("El aula no tiene vacantes disponibles (capacidad máxima: "
                    + aula.getCapacidadMaxima() + ", ocupadas: " + matriculasActivas + ")");
        }

        // 4. Validar que existan Conceptos ACTIVOS para el año académico
        List<Concepto> conceptos = conceptoRepository
                .findByAnioAcademicoCodAnioAcademicoAndEstadoTrueOrderByOrdenPagoAsc(anio.getCodAnioAcademico());

        if (conceptos.isEmpty()) {
            throw new Exception("No existen conceptos de pago activos para el año académico " + anio.getAnio()
                    + ". No se puede matricular sin un tarifario configurado.");
        }

        // 5. Registrar Matrícula
        Matricula matricula = new Matricula();
        matricula.setAlumno(alumno);
        matricula.setAula(aula);
        matricula.setAnioAcademico(anio);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstado("activa");
        matricula.setUsuarioRegistro(usuarioRegistro);
        matricula = matriculaRepository.save(matricula);

        // 6. Generar Cuotas automáticamente basándose en los conceptos ACTIVOS del año
        for (Concepto concepto : conceptos) {
            Cuota cuota = new Cuota();
            cuota.setMatricula(matricula);
            cuota.setConcepto(concepto);
            cuota.setMontoCobrado(concepto.getMonto());
            cuota.setEstado("PENDIENTE");
            cuotaRepository.save(cuota);
        }

        // 7. Registrar Auditoría
        auditoriaService.registrar(
                usuarioRegistro, "Matrícula", "matricula", "MATRICULA", matricula.getCodMatricula(),
                (Object) null, matricula, request
        );

        // 8. ¡Commit exitoso!
        return matricula;
    }
}