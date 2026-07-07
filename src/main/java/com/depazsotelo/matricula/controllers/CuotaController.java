package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Cuota;
import com.depazsotelo.matricula.repositories.CuotaRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaRepository cuotaRepository;
    private final AuditoriaService auditoriaService;

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

    @PutMapping("/{id}")
    public ResponseEntity<?> editarMonto(@PathVariable Integer id, @RequestBody BigDecimal nuevoMonto, Authentication authentication, HttpServletRequest request) {
        return cuotaRepository.findById(id)
                .map(cuota -> {
                    if (!"PENDIENTE".equalsIgnoreCase(cuota.getEstado())) {
                        return ResponseEntity.badRequest().body("Solo se pueden editar cuotas PENDIENTES.");
                    }
                    BigDecimal montoAnterior = cuota.getMontoCobrado();
                    cuota.setMontoCobrado(nuevoMonto);
                    Cuota guardada = cuotaRepository.save(cuota);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Pagos", "cuota", "UPDATE", guardada.getCodCuota(),
                            "{\"montoCobrado\":" + montoAnterior + "}",
                            "{\"montoCobrado\":" + guardada.getMontoCobrado() + "}",
                            request
                    );

                    return ResponseEntity.ok(guardada);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anular(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return cuotaRepository.findById(id)
                .map(cuota -> {
                    if ("PAGADO".equalsIgnoreCase(cuota.getEstado())) {
                        return ResponseEntity.badRequest().body("No se puede anular una cuota ya pagada.");
                    }
                    String estadoAnterior = cuota.getEstado();
                    cuota.setEstado("ANULADA");
                    Cuota guardada = cuotaRepository.save(cuota);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Pagos", "cuota", "DELETE", guardada.getCodCuota(),
                            "{\"estado\":\"" + estadoAnterior + "\"}",
                            "{\"estado\":\"ANULADA\"}",
                            request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}