package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.TipoDocumento;
import com.depazsotelo.matricula.repositories.TipoDocumentoRepository;
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
@RequestMapping("/api/tipos-documento")
@RequiredArgsConstructor
public class TipoDocumentoController {

    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final AuditoriaService auditoriaService;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'TiposDocumento', 'ver')")
    @GetMapping
    public List<TipoDocumento> listar() {
        return tipoDocumentoRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'TiposDocumento', 'ver')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return tipoDocumentoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'TiposDocumento', 'crear')")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody TipoDocumento request, Authentication authentication, HttpServletRequest httpRequest) {
        request.setCodTipoDocumento(null);
        request.setEstado(true);
        TipoDocumento guardado = tipoDocumentoRepository.save(request);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Seguridad", "tipo_documento", "INSERT", guardado.getCodTipoDocumento(),
                (Object) null, guardado, httpRequest
        );

        return ResponseEntity.ok(guardado);
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'TiposDocumento', 'editar')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @PathVariable Integer id, @RequestBody TipoDocumento request,
                                    Authentication authentication, HttpServletRequest httpRequest) {
        return tipoDocumentoRepository.findById(id)
                .map(existente -> {
                    String nombreAnterior = existente.getNombre();
                    existente.setNombre(request.getNombre());
                    TipoDocumento guardado = tipoDocumentoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Seguridad", "tipo_documento", "UPDATE", guardado.getCodTipoDocumento(),
                            "{\"nombre\":\"" + nombreAnterior + "\"}",
                            "{\"nombre\":\"" + guardado.getNombre() + "\"}",
                            httpRequest
                    );

                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'TiposDocumento', 'eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return tipoDocumentoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    TipoDocumento guardado = tipoDocumentoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Seguridad", "tipo_documento", "DELETE", guardado.getCodTipoDocumento(),
                            "{\"estado\":true}", "{\"estado\":false}", request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
