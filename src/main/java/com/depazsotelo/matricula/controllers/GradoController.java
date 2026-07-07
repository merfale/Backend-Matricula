package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Grado;
import com.depazsotelo.matricula.repositories.GradoRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/grados")
@RequiredArgsConstructor
public class GradoController {
    private final GradoRepository GradoRepository;
    private final AuditoriaService auditoriaService;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Grados', 'ver')")
    @GetMapping
    public List<Grado> listar() {
        return GradoRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Grados', 'ver')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return GradoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Grados', 'crear')")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Grado request, Authentication authentication, HttpServletRequest httpRequest) {
        request.setCodGrado(null);
        request.setEstado(true);
        Grado guardado = GradoRepository.save(request);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Matrícula", "grado", "INSERT", guardado.getCodGrado(),
                (Object) null, guardado, httpRequest
        );

        return ResponseEntity.ok(guardado);
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Grados', 'editar')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @PathVariable Integer id, @RequestBody Grado request, Authentication authentication, HttpServletRequest httpRequest) {
        return GradoRepository.findById(id)
                .map(existente -> {
                    String nombreAnterior = existente.getNombre();
                    existente.setNombre(request.getNombre());
                    Grado guardado = GradoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Matrícula", "grado", "UPDATE", guardado.getCodGrado(),
                            "{\"nombre\":\"" + nombreAnterior + "\"}",
                            "{\"nombre\":\"" + guardado.getNombre() + "\"}",
                            httpRequest
                    );

                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Grados', 'eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return GradoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    Grado guardado = GradoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Matrícula", "grado", "DELETE", guardado.getCodGrado(),
                            "{\"estado\":true}", "{\"estado\":false}", request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
