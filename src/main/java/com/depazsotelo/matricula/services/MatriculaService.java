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
    public Matricula registrarMatriculaTransaccional(Integer codAlumno, Integer codAula, Usuario usuarioRegistro,  HttpServletRequest request) throws Exception {

        Alumno alumno = alumnoRepository.findById(codAlumno)
                .orElseThrow(() -> new Exception("Alumno no encontrado"));
        Aula aula = aulaRepository.findById(codAula)
                .orElseThrow(() -> new Exception("Aula no encontrada"));
        AnioAcademico anio = aula.getAnioAcademico();

        if (matriculaRepository.existsByAlumnoCodAlumnoAndAnioAcademicoCodAnioAcademico(codAlumno, anio.getCodAnioAcademico())) {
            throw new Exception("El alumno ya se encuentra matriculado en el año " + anio.getAnio());
        }

        Matricula matricula = new Matricula();
        matricula.setAlumno(alumno);
        matricula.setAula(aula);
        matricula.setAnioAcademico(anio);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstado("activa");
        matricula.setUsuarioRegistro(usuarioRegistro);
        matricula = matriculaRepository.save(matricula);

        // 4. Generar Cuotas automáticamente basándose en los conceptos del año
        List<Concepto> conceptos = conceptoRepository.findByAnioAcademicoCodAnioAcademicoOrderByOrdenPagoAsc(anio.getCodAnioAcademico());
        for (Concepto concepto : conceptos) {
            Cuota cuota = new Cuota();
            cuota.setMatricula(matricula);
            cuota.setConcepto(concepto);
            cuota.setMontoCobrado(concepto.getMonto());
            cuota.setEstado("PENDIENTE");
            cuotaRepository.save(cuota);
        }

        auditoriaService.registrar(
                usuarioRegistro,
                "Matrícula",
                "matricula",
                "MATRICULA",
                matricula.getCodMatricula(),
                (Object) null,
                matricula,
                request
        );

        return matricula;
    }
}