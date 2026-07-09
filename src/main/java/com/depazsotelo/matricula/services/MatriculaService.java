package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.*;
import com.depazsotelo.matricula.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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


        Alumno alumno = alumnoRepository.findById(codAlumno)
                .orElseThrow(() -> new Exception("Alumno no encontrado"));
        Aula aula = aulaRepository.findById(codAula)
                .orElseThrow(() -> new Exception("Aula no encontrada"));
        AnioAcademico anio = aula.getAnioAcademico();


        if (matriculaRepository.existsByAlumnoCodAlumnoAndAnioAcademicoCodAnioAcademico(codAlumno, anio.getCodAnioAcademico())) {
            throw new Exception("El alumno ya se encuentra matriculado en el año " + anio.getAnio());
        }

        List<Cuota> cuotasPendientes = cuotaRepository
                .findByMatricula_Alumno_CodAlumnoAndEstado(codAlumno, "PENDIENTE");

        if (!cuotasPendientes.isEmpty()) {
            BigDecimal totalDeuda = cuotasPendientes.stream()
                    .map(Cuota::getMontoCobrado)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String aniosConDeuda = cuotasPendientes.stream()
                    .map(c -> c.getMatricula().getAnioAcademico().getAnio())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));

            throw new Exception("No se puede matricular: el alumno tiene deuda pendiente de S/ "
                    + totalDeuda + " correspondiente al(los) año(s) " + aniosConDeuda
                    + ". Debe regularizar los pagos anteriores antes de matricularse en "
                    + anio.getAnio() + ".");
        }


        long matriculasActivas = matriculaRepository.countByAulaCodAulaAndEstado(codAula, "activa");
        if (matriculasActivas >= aula.getCapacidadMaxima()) {
            throw new Exception("El aula no tiene vacantes disponibles (capacidad máxima: "
                    + aula.getCapacidadMaxima() + ", ocupadas: " + matriculasActivas + ")");
        }


        List<Concepto> conceptos = conceptoRepository
                .findByAnioAcademicoCodAnioAcademicoAndEstadoTrueOrderByOrdenPagoAsc(anio.getCodAnioAcademico());

        if (conceptos.isEmpty()) {
            throw new Exception("No existen conceptos de pago activos para el año académico " + anio.getAnio()
                    + ". No se puede matricular sin un tarifario configurado.");
        }


        Matricula matricula = new Matricula();
        matricula.setAlumno(alumno);
        matricula.setAula(aula);
        matricula.setAnioAcademico(anio);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstado("activa");
        matricula.setUsuarioRegistro(usuarioRegistro);
        matricula = matriculaRepository.save(matricula);


        for (Concepto concepto : conceptos) {
            Cuota cuota = new Cuota();
            cuota.setMatricula(matricula);
            cuota.setConcepto(concepto);
            cuota.setMontoCobrado(concepto.getMonto());
            cuota.setEstado("PENDIENTE");
            cuotaRepository.save(cuota);
        }


        auditoriaService.registrar(
                usuarioRegistro, "Matrícula", "matricula", "MATRICULA", matricula.getCodMatricula(),
                (Object) null, matricula, request
        );


        return matricula;
    }
}