package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Cuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Integer> {


    boolean existsByMatriculaCodMatriculaAndConceptoOrdenPagoLessThanAndEstado(
            Integer codMatricula, Short ordenPago, String estado
    );
    List<Cuota> findByEstado(String estado);
    List<Cuota> findByMatriculaCodMatricula(Integer codMatricula);

    List<Cuota> findByMatricula_Alumno_CodAlumno(Integer codAlumno);
    List<Cuota> findByMatricula_AnioAcademico_CodAnioAcademico(Integer codAnioAcademico);
    List<Cuota> findByMatricula_Alumno_CodAlumnoAndMatricula_AnioAcademico_CodAnioAcademico(
            Integer codAlumno, Integer codAnioAcademico
    );
}