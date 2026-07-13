package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.Aula;
import com.depazsotelo.matricula.models.Vacante;
import com.depazsotelo.matricula.repositories.MatriculaRepository;
import com.depazsotelo.matricula.repositories.VacanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class VacanteService {

    private final VacanteRepository vacanteRepository;
    private final MatriculaRepository matriculaRepository;

    private Vacante obtenerOInicializar(Aula aula) {
        return vacanteRepository.findByAulaCodAula(aula.getCodAula())
                .orElseGet(() -> {
                    Vacante v = new Vacante();
                    v.setAula(aula);
                    short ocupadas = (short) matriculaRepository.countByAulaCodAulaAndEstado(aula.getCodAula(), "activa");
                    v.setVacantesOcupadas(ocupadas);
                    v.setVacantesDisponibles((short) (aula.getCapacidadMaxima() - ocupadas));
                    return v;
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public Vacante sincronizarCapacidad(Aula aula) {
        Vacante vacante = obtenerOInicializar(aula);
        vacante.setAula(aula);
        vacante.setVacantesDisponibles((short) (aula.getCapacidadMaxima() - vacante.getVacantesOcupadas()));
        vacante.setFechaActualizacion(LocalDateTime.now());
        return vacanteRepository.save(vacante);
    }

    @Transactional(rollbackFor = Exception.class)
    public Vacante ocupar(Aula aula) throws Exception {
        Vacante vacante = obtenerOInicializar(aula);

        if (vacante.getVacantesDisponibles() <= 0) {
            throw new Exception("El aula no tiene vacantes disponibles (capacidad máxima: "
                    + aula.getCapacidadMaxima() + ", ocupadas: " + vacante.getVacantesOcupadas() + ")");
        }

        vacante.setVacantesOcupadas((short) (vacante.getVacantesOcupadas() + 1));
        vacante.setVacantesDisponibles((short) (vacante.getVacantesDisponibles() - 1));
        vacante.setFechaActualizacion(LocalDateTime.now());

        return vacanteRepository.save(vacante);
    }

    @Transactional(rollbackFor = Exception.class)
    public Vacante liberar(Integer codAula) throws Exception {
        Vacante vacante = vacanteRepository.findByAulaCodAula(codAula)
                .orElseThrow(() -> new Exception("No existe control de vacantes para esta aula"));

        vacante.setVacantesOcupadas((short) Math.max(0, vacante.getVacantesOcupadas() - 1));
        vacante.setVacantesDisponibles((short) (vacante.getVacantesDisponibles() + 1));
        vacante.setFechaActualizacion(LocalDateTime.now());

        return vacanteRepository.save(vacante);
    }
}