package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findByModulo(String modulo);
    List<Auditoria> findByUsuarioIdUsuario(Integer idUsuario);
    List<Auditoria> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);
}