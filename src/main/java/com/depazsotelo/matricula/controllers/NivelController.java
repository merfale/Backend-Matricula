package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Nivel;
import com.depazsotelo.matricula.repositories.NivelRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/niveles")
@RequiredArgsConstructor
public class NivelController {
    private final NivelRepository nivelRepository;
    private final AuditoriaService auditoriaService; // MEJORA: auditoría real

    @GetMapping
    public List<Nivel> listar() {
        return nivelRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return nivelRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Nivel request, Authentication authentication, HttpServletRequest httpRequest) {
        request.setCodNivel(null); // por si mandan un id por error
        request.setEstado(true);
        Nivel guardado = nivelRepository.save(request);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Matrícula", "nivel", "INSERT", guardado.getCodNivel(),
                (Object) null, guardado, httpRequest
        );

        return ResponseEntity.ok(guardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Nivel request,
                                    Authentication authentication, HttpServletRequest httpRequest) {
        return nivelRepository.findById(id)
                .map(existente -> {
                    String nombreAnterior = existente.getNombre();
                    existente.setNombre(request.getNombre());
                    Nivel guardado = nivelRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Matrícula", "nivel", "UPDATE", guardado.getCodNivel(),
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
        return nivelRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    Nivel guardado = nivelRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Matrícula", "nivel", "DELETE", guardado.getCodNivel(),
                            "{\"estado\":true}", "{\"estado\":false}", request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
