package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Integer> {
    List<Aula> findByAnioAcademicoCodAnioAcademico(Integer codAnioAcademico);
}