package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Auditoria;
import com.depazsotelo.matricula.repositories.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Auditoria', 'ver')")
    @GetMapping
    public List<Auditoria> listar() {
        return auditoriaRepository.findAll();
    }

    // Vista compacta de la pestaña Trazabilidad: solo los últimos 10 registros
    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Auditoria', 'ver')")
    @GetMapping("/recientes")
    public List<Auditoria> recientes() {
        return auditoriaRepository.findTop10ByOrderByFechaHoraDesc();
    }

    // Opciones reales (no hardcodeadas) para llenar los combos de filtro del modal "Auditoría completa"
    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Auditoria', 'ver')")
    @GetMapping("/filtros")
    public Map<String, Object> filtros() {
        Map<String, Object> filtros = new LinkedHashMap<>();
        filtros.put("modulos", auditoriaRepository.findDistinctModulos());
        filtros.put("operaciones", auditoriaRepository.findDistinctOperaciones());
        filtros.put("tablas", auditoriaRepository.findDistinctTablas());
        return filtros;
    }

    // Búsqueda paginada con filtros, para el modal "Auditoría completa"
    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Auditoria', 'ver')")
    @GetMapping("/buscar")
    public Page<Auditoria> buscar(
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) String operacion,
            @RequestParam(required = false) String modulo,
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        LocalDateTime desdeDT = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaDT = hasta != null ? hasta.atTime(LocalTime.MAX) : null;
        Pageable pageable = PageRequest.of(page, size, Sort.by("fechaHora").descending());

        return auditoriaRepository.buscar(usuario, operacion, modulo, tabla, desdeDT, hastaDT, pageable);
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Auditoria', 'ver')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return auditoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
