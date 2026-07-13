package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Integer> {

    Optional<Deuda> findByMatriculaCodMatricula(Integer codMatricula);

    boolean existsByMatriculaCodMatricula(Integer codMatricula);

    List<Deuda> findByEstado(String estado);

    List<Deuda> findByMatricula_Alumno_CodAlumno(Integer codAlumno);

    List<Deuda> findByMatricula_AnioAcademico_CodAnioAcademico(Integer codAnioAcademico);

    List<Deuda> findByMatricula_Alumno_CodAlumnoAndEstado(Integer codAlumno, String estado);

    List<Deuda> findByMatricula_AnioAcademico_CodAnioAcademicoAndEstado(Integer codAnioAcademico, String estado);
}