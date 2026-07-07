package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {

    Optional<Alumno> findByNumeroDocumento(String numeroDocumento);
    List<Alumno> findByEstado(Boolean estado);
}