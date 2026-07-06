package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.Cuota;
import com.depazsotelo.matricula.repositories.CuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

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
        cuota.setRecibo("BOL-2026-" + String.format("%04d", cuota.getCodCuota())); // Autogenera un recibo

        return cuotaRepository.save(cuota);
    }

    public java.util.List<Cuota> listarTodasLasCuotas() {
        return cuotaRepository.findAll();
    }
}