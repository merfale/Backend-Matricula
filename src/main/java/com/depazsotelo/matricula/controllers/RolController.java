package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Rol;
import com.depazsotelo.matricula.repositories.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolRepository rolRepository;

    @GetMapping
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Rol rol) {
        rol.setEstado(true);
        return ResponseEntity.ok(rolRepository.save(rol));
    }

    // MEJORA: eliminación lógica, no física (como pide el spec)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Integer id) {
        Rol rol = rolRepository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rol.setEstado(false);
        rolRepository.save(rol);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Rol request) {
        return rolRepository.findById(id)
                .map(existente -> {
                    existente.setNombreRol(request.getNombreRol());
                    return ResponseEntity.ok(rolRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}