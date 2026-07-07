package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.AnioAcademico;
import com.depazsotelo.matricula.repositories.AnioAcademicoRepository;
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
@RequestMapping("/api/anios-academicos")
@RequiredArgsConstructor
public class AnioAcademicoController {
    private final AnioAcademicoRepository AnioAcademicoRepository;
    private final AuditoriaService auditoriaService;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'AniosAcademicos', 'ver')")
    @GetMapping
    public List<AnioAcademico> listar() {
        return AnioAcademicoRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'AniosAcademicos', 'ver')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return AnioAcademicoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'AniosAcademicos', 'crear')")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody AnioAcademico request, Authentication authentication, HttpServletRequest httpRequest) {
        request.setCodAnioAcademico(null);
        request.setEstado(true);
        AnioAcademico guardado = AnioAcademicoRepository.save(request);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Matrícula", "anio_academico", "INSERT", guardado.getCodAnioAcademico(),
                (Object) null, guardado, httpRequest
        );

        return ResponseEntity.ok(guardado);
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'AniosAcademicos', 'editar')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @PathVariable Integer id, @RequestBody AnioAcademico request, Authentication authentication, HttpServletRequest httpRequest) {
        return AnioAcademicoRepository.findById(id)
                .map(existente -> {
                    String anioAnterior = existente.getAnio();
                    existente.setAnio(request.getAnio());
                    AnioAcademico guardado = AnioAcademicoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Matrícula", "anio_academico", "UPDATE", guardado.getCodAnioAcademico(),
                            "{\"anio\":\"" + anioAnterior + "\"}",
                            "{\"anio\":\"" + guardado.getAnio() + "\"}",
                            httpRequest
                    );

                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'AniosAcademicos', 'eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return AnioAcademicoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    AnioAcademico guardado = AnioAcademicoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Matrícula", "anio_academico", "DELETE", guardado.getCodAnioAcademico(),
                            "{\"estado\":true}", "{\"estado\":false}", request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
