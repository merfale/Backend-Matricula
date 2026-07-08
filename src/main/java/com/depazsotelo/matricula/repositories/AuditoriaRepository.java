package com.depazsotelo.matricula.repositories;

import com.depazsotelo.matricula.models.Auditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Integer> {
    List<Auditoria> findByModulo(String modulo);
    List<Auditoria> findByUsuarioIdUsuario(Integer idUsuario);
    List<Auditoria> findByFechaHoraBetween(LocalDateTime desde, LocalDateTime hasta);

    // Vista compacta de la pestaña Trazabilidad: solo los últimos 10 registros
    List<Auditoria> findTop10ByOrderByFechaHoraDesc();

    // Búsqueda paginada con filtros opcionales para el modal "Auditoría completa"
    @Query("SELECT a FROM Auditoria a WHERE " +
            "(:usuario IS NULL OR a.usuario.usuario = :usuario) AND " +
            "(:operacion IS NULL OR a.operacion = :operacion) AND " +
            "(:modulo IS NULL OR a.modulo = :modulo) AND " +
            "(:tabla IS NULL OR a.tablaAfectada = :tabla) AND " +
            "(:desde IS NULL OR a.fechaHora >= :desde) AND " +
            "(:hasta IS NULL OR a.fechaHora <= :hasta)")
    Page<Auditoria> buscar(@Param("usuario") String usuario,
                           @Param("operacion") String operacion,
                           @Param("modulo") String modulo,
                           @Param("tabla") String tabla,
                           @Param("desde") LocalDateTime desde,
                           @Param("hasta") LocalDateTime hasta,
                           Pageable pageable);

    @Query("SELECT DISTINCT a.modulo FROM Auditoria a ORDER BY a.modulo")
    List<String> findDistinctModulos();

    @Query("SELECT DISTINCT a.operacion FROM Auditoria a ORDER BY a.operacion")
    List<String> findDistinctOperaciones();

    @Query("SELECT DISTINCT a.tablaAfectada FROM Auditoria a ORDER BY a.tablaAfectada")
    List<String> findDistinctTablas();
}