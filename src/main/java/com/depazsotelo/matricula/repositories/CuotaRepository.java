package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {


    boolean existsByMatriculaCodMatriculaAndConceptoOrdenPagoLessThanAndEstado(
            Integer codMatricula, Short ordenPago, String estado
    );
}