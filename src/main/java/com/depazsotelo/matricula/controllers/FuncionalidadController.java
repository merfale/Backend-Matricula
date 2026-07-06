package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Funcionalidad;
import com.depazsotelo.matricula.repositories.FuncionalidadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/funcionalidades")
@RequiredArgsConstructor
public class FuncionalidadController {

    private final FuncionalidadRepository funcionalidadRepository;

    // MEJORA: solo trae las raíces; el frontend arma el árbol pidiendo los hijos
    // por cada nodo, o puedes mapear a un DTO recursivo si prefieres un solo request.
    @GetMapping("/raiz")
    public List<Funcionalidad> listarRaiz() {
        return funcionalidadRepository.findByPadreIsNull();
    }

    @GetMapping
    public List<Funcionalidad> listarTodas() {
        return funcionalidadRepository.findAll();
    }

    @PostMapping
    public Funcionalidad crear(@RequestBody Funcionalidad funcionalidad) {
        return funcionalidadRepository.save(funcionalidad);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return funcionalidadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Funcionalidad request) {
        return funcionalidadRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(request.getNombre());
                    existente.setIcono(request.getIcono());
                    existente.setPadre(request.getPadre());
                    return ResponseEntity.ok(funcionalidadRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminación física: no hay campo "estado" en este modelo (ver nota arriba)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!funcionalidadRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        funcionalidadRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}