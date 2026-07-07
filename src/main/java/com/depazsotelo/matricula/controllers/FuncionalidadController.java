package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Funcionalidad;
import com.depazsotelo.matricula.repositories.FuncionalidadRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/funcionalidades")
@RequiredArgsConstructor
public class FuncionalidadController {

    private final FuncionalidadRepository funcionalidadRepository;
    private final AuditoriaService auditoriaService;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Funcionalidades', 'ver')")
    @GetMapping("/raiz")
    public List<Funcionalidad> listarRaiz() {
        return funcionalidadRepository.findByPadreIsNull();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Funcionalidades', 'ver')")
    @GetMapping
    public List<Funcionalidad> listarTodas() {
        return funcionalidadRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Funcionalidades', 'crear')")
    @PostMapping
    public Funcionalidad crear(@RequestBody Funcionalidad funcionalidad, Authentication authentication, HttpServletRequest request) {
        Funcionalidad guardada = funcionalidadRepository.save(funcionalidad);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Seguridad", "funcionalidad", "INSERT", guardada.getIdFuncionalidad(),
                (String) null,
                "{\"nombre\":\"" + guardada.getNombre() + "\"}",
                request
        );

        return guardada;
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Funcionalidades', 'editar')")
    @PutMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return funcionalidadRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Funcionalidades', 'eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Funcionalidad request, Authentication authentication, HttpServletRequest httpRequest) {
        return funcionalidadRepository.findById(id)
                .map(existente -> {
                    String nombreAnterior = existente.getNombre();
                    existente.setNombre(request.getNombre());
                    existente.setIcono(request.getIcono());
                    existente.setPadre(request.getPadre());
                    Funcionalidad guardada = funcionalidadRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Seguridad", "funcionalidad", "UPDATE", guardada.getIdFuncionalidad(),
                            "{\"nombre\":\"" + nombreAnterior + "\"}",
                            "{\"nombre\":\"" + guardada.getNombre() + "\"}",
                            httpRequest
                    );

                    return ResponseEntity.ok(guardada);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return funcionalidadRepository.findById(id)
                .map(existente -> {
                    String nombreEliminado = existente.getNombre();
                    funcionalidadRepository.deleteById(id);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Seguridad", "funcionalidad", "DELETE", id,
                            "{\"nombre\":\"" + nombreEliminado + "\"}",
                            (String) null,
                            request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}