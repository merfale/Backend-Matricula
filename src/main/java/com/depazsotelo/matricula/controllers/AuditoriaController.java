package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Auditoria;
import com.depazsotelo.matricula.repositories.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

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

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Auditoria', 'ver')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return auditoriaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}