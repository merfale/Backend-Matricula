package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Grado;
import com.depazsotelo.matricula.repositories.GradoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/grados")
@RequiredArgsConstructor
public class GradoController {
    private final GradoRepository GradoRepository;

    @GetMapping
    public List<Grado> listar() {
        return GradoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return GradoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Grado request) {
        request.setCodGrado(null); // por si mandan un id por error
        request.setEstado(true);
        return ResponseEntity.ok(GradoRepository.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Grado request) {
        return GradoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(request.getNombre());
                    return ResponseEntity.ok(GradoRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminación LÓGICA (no física), consistente con el resto del proyecto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return GradoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    GradoRepository.save(existente);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
