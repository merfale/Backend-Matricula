package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.TipoConcepto;
import com.depazsotelo.matricula.repositories.TipoConceptoRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tipos-concepto")
@RequiredArgsConstructor
public class TipoConceptoController {

    private final TipoConceptoRepository tipoConceptoRepository;
    private final AuditoriaService auditoriaService; // MEJORA: auditoría real

    @GetMapping
    public List<TipoConcepto> listar() {
        return tipoConceptoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return tipoConceptoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TipoConcepto request, Authentication authentication, HttpServletRequest httpRequest) {
        request.setCodTipoConcepto(null);
        request.setEstado(true);
        TipoConcepto guardado = tipoConceptoRepository.save(request);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Conceptos", "tipo_concepto", "INSERT", guardado.getCodTipoConcepto(),
                (Object) null, guardado, httpRequest
        );

        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody TipoConcepto request,
                                    Authentication authentication, HttpServletRequest httpRequest) {
        return tipoConceptoRepository.findById(id)
                .map(existente -> {
                    String nombreAnterior = existente.getNombre();
                    existente.setNombre(request.getNombre());
                    TipoConcepto guardado = tipoConceptoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Conceptos", "tipo_concepto", "UPDATE", guardado.getCodTipoConcepto(),
                            "{\"nombre\":\"" + nombreAnterior + "\"}",
                            "{\"nombre\":\"" + guardado.getNombre() + "\"}",
                            httpRequest
                    );

                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return tipoConceptoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    TipoConcepto guardado = tipoConceptoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Conceptos", "tipo_concepto", "DELETE", guardado.getCodTipoConcepto(),
                            "{\"estado\":true}", "{\"estado\":false}", request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
