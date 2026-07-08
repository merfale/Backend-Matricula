package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.AplicarPermisosRequest;
import com.depazsotelo.matricula.models.RolFuncionalidad;
import com.depazsotelo.matricula.repositories.RolFuncionalidadRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import com.depazsotelo.matricula.services.PermisoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/permisos")
@RequiredArgsConstructor
public class PermisoController {

    private final PermisoService permisoService;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;
    private final AuditoriaService auditoriaService; // MEJORA: auditoría real

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Permisos', 'ver')")
    @GetMapping("/rol/{idRol}")
    public List<RolFuncionalidad> obtenerPorRol(@PathVariable Integer idRol) {
        return rolFuncionalidadRepository.findByRolIdRol(idRol);
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Permisos', 'editar')")
    @PostMapping("/aplicar")
    public ResponseEntity<?> aplicar(@Valid @RequestBody AplicarPermisosRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        try {
            permisoService.aplicarPermisos(request);


            auditoriaService.registrar(
                    auditoriaService.usuarioDesdeAuth(authentication),
                    "Permisos",
                    "rol_funcionalidad",
                    "UPDATE",
                    request.getIdRol(),
                    (String) null,
                    request,
                    httpRequest
            );

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
