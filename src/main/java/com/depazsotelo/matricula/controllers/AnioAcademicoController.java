package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.AnioAcademico;
import com.depazsotelo.matricula.repositories.AnioAcademicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/anios-academicos")
@RequiredArgsConstructor
public class AnioAcademicoController {
    private final AnioAcademicoRepository AnioAcademicoRepository;

    @GetMapping
    public List<AnioAcademico> listar() {
        return AnioAcademicoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return AnioAcademicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody AnioAcademico request) {
        request.setCodAnioAcademico(null); // por si mandan un id por error
        request.setEstado(true);
        return ResponseEntity.ok(AnioAcademicoRepository.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody AnioAcademico request) {
        return AnioAcademicoRepository.findById(id)
                .map(existente -> {
                    existente.setAnio(request.getAnio());
                    return ResponseEntity.ok(AnioAcademicoRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminación LÓGICA (no física), consistente con el resto del proyecto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return AnioAcademicoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    AnioAcademicoRepository.save(existente);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
