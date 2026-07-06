package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.TipoConcepto;
import com.depazsotelo.matricula.repositories.TipoConceptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-concepto")
@RequiredArgsConstructor
public class TipoConceptoController {

    private final TipoConceptoRepository TipoConceptoRepository;

    @GetMapping
    public List<TipoConcepto> listar() {
        return TipoConceptoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return TipoConceptoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TipoConcepto request) {
        request.setCodTipoConcepto(null); // por si mandan un id por error
        request.setEstado(true);
        return ResponseEntity.ok(TipoConceptoRepository.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody TipoConcepto request) {
        return TipoConceptoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(request.getNombre());
                    return ResponseEntity.ok(TipoConceptoRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminación LÓGICA (no física), consistente con el resto del proyecto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return TipoConceptoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    TipoConceptoRepository.save(existente);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
