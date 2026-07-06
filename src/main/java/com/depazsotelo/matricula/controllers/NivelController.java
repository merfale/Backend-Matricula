package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Nivel;
import com.depazsotelo.matricula.repositories.NivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/niveles")
@RequiredArgsConstructor
public class NivelController {
    private final NivelRepository NivelRepository;

    @GetMapping
    public List<Nivel> listar() {
        return NivelRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return NivelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Nivel request) {
        request.setCodNivel(null); // por si mandan un id por error
        request.setEstado(true);
        return ResponseEntity.ok(NivelRepository.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Nivel request) {
        return NivelRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(request.getNombre());
                    return ResponseEntity.ok(NivelRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminación LÓGICA (no física), consistente con el resto del proyecto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return NivelRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    NivelRepository.save(existente);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
