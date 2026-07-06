package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Cuota;
import com.depazsotelo.matricula.repositories.CuotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaRepository cuotaRepository;

    @GetMapping
    public List<Cuota> listar() {
        return cuotaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return cuotaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // MEJORA: permite corregir el monto de una cuota (solo si sigue PENDIENTE)
    @PutMapping("/{id}")
    public ResponseEntity<?> editarMonto(@PathVariable Integer id, @RequestBody BigDecimal nuevoMonto) {
        return cuotaRepository.findById(id)
                .map(cuota -> {
                    if (!"PENDIENTE".equalsIgnoreCase(cuota.getEstado())) {
                        return ResponseEntity.badRequest().body("Solo se pueden editar cuotas PENDIENTES.");
                    }
                    cuota.setMontoCobrado(nuevoMonto);
                    return ResponseEntity.ok(cuotaRepository.save(cuota));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // MEJORA: "eliminar" una cuota = anularla, nunca se paga y no se borra el historial
    @DeleteMapping("/{id}")
    public ResponseEntity<?> anular(@PathVariable Integer id) {
        return cuotaRepository.findById(id)
                .map(cuota -> {
                    if ("PAGADO".equalsIgnoreCase(cuota.getEstado())) {
                        return ResponseEntity.badRequest().body("No se puede anular una cuota ya pagada.");
                    }
                    cuota.setEstado("ANULADA");
                    cuotaRepository.save(cuota);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}