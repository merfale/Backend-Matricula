package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Integer> {

    boolean existsByAlumnoCodAlumnoAndAnioAcademicoCodAnioAcademico(Integer codAlumno, Integer codAnioAcademico);

    long countByAulaCodAulaAndEstado(Integer codAula, String estado);
}