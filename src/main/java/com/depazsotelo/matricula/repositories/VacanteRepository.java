package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Vacante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacanteRepository extends JpaRepository<Vacante, Integer> {

    Optional<Vacante> findByAulaCodAula(Integer codAula);

    List<Vacante> findByAula_AnioAcademico_CodAnioAcademico(Integer codAnioAcademico);

    List<Vacante> findByAula_Nivel_CodNivel(Integer codNivel);

    List<Vacante> findByAula_Grado_CodGrado(Integer codGrado);

    List<Vacante> findByVacantesDisponiblesGreaterThan(Short cantidad);

    List<Vacante> findByAula_AnioAcademico_CodAnioAcademicoAndVacantesDisponiblesGreaterThan(
            Integer codAnioAcademico, Short cantidad
    );
}