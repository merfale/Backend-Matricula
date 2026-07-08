package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.AnioAcademico;
import com.depazsotelo.matricula.models.Cuota;
import com.depazsotelo.matricula.repositories.CuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final CuotaRepository cuotaRepository;

    @Transactional(rollbackFor = Exception.class)
    public Cuota registrarPago(Integer codCuota) throws Exception {

        Cuota cuota = cuotaRepository.findById(codCuota)
                .orElseThrow(() -> new Exception("Error: La cuota especificada no existe."));


        if (!"PENDIENTE".equalsIgnoreCase(cuota.getEstado())) {
            throw new Exception("Operación rechazada: La cuota ya está pagada o anulada.");
        }


        boolean tieneDeudasAnteriores = cuotaRepository.existsByMatriculaCodMatriculaAndConceptoOrdenPagoLessThanAndEstado(
                cuota.getMatricula().getCodMatricula(),
                cuota.getConcepto().getOrdenPago(),
                "PENDIENTE"
        );

        if (tieneDeudasAnteriores) {
            throw new Exception("No se puede pagar esta cuota. Existen cuotas anteriores pendientes de pago.");
        }

        cuota.setEstado("PAGADO");
        cuota.setFechaPago(LocalDateTime.now());
        cuota.setRecibo(generarRecibo(cuota.getMatricula().getAnioAcademico()));

        return cuotaRepository.save(cuota);
    }

    private String generarRecibo(AnioAcademico anioAcademico) {
        int correlativoActual = anioAcademico.getUltimoCorrelativoRecibo() == null
                ? 0
                : anioAcademico.getUltimoCorrelativoRecibo();

        int siguienteCorrelativo = correlativoActual + 1;
        anioAcademico.setUltimoCorrelativoRecibo(siguienteCorrelativo);

        return "BOL-" + anioAcademico.getAnio() + "-" + String.format("%04d", siguienteCorrelativo);
    }

    public List<Cuota> listarCuotas(Integer codAlumno, Integer codAnioAcademico) {
        if (codAlumno != null && codAnioAcademico != null) {
            return cuotaRepository.findByMatricula_Alumno_CodAlumnoAndMatricula_AnioAcademico_CodAnioAcademico(codAlumno, codAnioAcademico);
        }
        if (codAlumno != null) {
            return cuotaRepository.findByMatricula_Alumno_CodAlumno(codAlumno);
        }
        if (codAnioAcademico != null) {
            return cuotaRepository.findByMatricula_AnioAcademico_CodAnioAcademico(codAnioAcademico);
        }
        return cuotaRepository.findAll();
    }
}