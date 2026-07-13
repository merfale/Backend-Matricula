package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.Cuota;
import com.depazsotelo.matricula.models.Deuda;
import com.depazsotelo.matricula.models.Matricula;
import com.depazsotelo.matricula.repositories.CuotaRepository;
import com.depazsotelo.matricula.repositories.DeudaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeudaService {

    private final DeudaRepository deudaRepository;
    private final CuotaRepository cuotaRepository;

    @Transactional(rollbackFor = Exception.class)
    public Deuda recalcularDeuda(Matricula matricula) {
        List<Cuota> cuotas = cuotaRepository.findByMatriculaCodMatricula(matricula.getCodMatricula());

        BigDecimal montoTotal = cuotas.stream()
                .map(Cuota::getMontoCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montoPendiente = cuotas.stream()
                .filter(c -> "PENDIENTE".equalsIgnoreCase(c.getEstado()))
                .map(Cuota::getMontoCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Deuda deuda = deudaRepository.findByMatriculaCodMatricula(matricula.getCodMatricula())
                .orElseGet(Deuda::new);

        deuda.setMatricula(matricula);
        deuda.setMontoTotal(montoTotal);
        deuda.setMontoPendiente(montoPendiente);
        deuda.setEstado(montoPendiente.compareTo(BigDecimal.ZERO) == 0 ? "AL_DIA" : "PENDIENTE");
        deuda.setFechaActualizacion(LocalDateTime.now());

        return deudaRepository.save(deuda);
    }
}