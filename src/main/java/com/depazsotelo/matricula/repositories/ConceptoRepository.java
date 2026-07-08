package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Concepto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ConceptoRepository extends JpaRepository<Concepto, Integer> {

    List<Concepto> findByAnioAcademicoCodAnioAcademicoOrderByOrdenPagoAsc(Integer codAnioAcademico);
    List<Concepto> findByAnioAcademicoCodAnioAcademicoAndEstadoTrueOrderByOrdenPagoAsc(Integer codAnioAcademico);
}