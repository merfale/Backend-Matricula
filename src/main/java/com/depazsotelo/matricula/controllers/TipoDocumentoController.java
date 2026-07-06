package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.TipoDocumento;
import com.depazsotelo.matricula.repositories.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoController {

    private final TipoDocumentoRepository tipoDocumentoRepository;

    @GetMapping
    public List<TipoDocumento> listar() {
        return tipoDocumentoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return tipoDocumentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TipoDocumento request) {
        request.setCodTipoDocumento(null); // por si mandan un id por error
        request.setEstado(true);
        return ResponseEntity.ok(tipoDocumentoRepository.save(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody TipoDocumento request) {
        return tipoDocumentoRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(request.getNombre());
                    return ResponseEntity.ok(tipoDocumentoRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Eliminación LÓGICA (no física), consistente con el resto del proyecto
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return tipoDocumentoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    tipoDocumentoRepository.save(existente);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}