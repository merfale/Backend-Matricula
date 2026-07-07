package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.AplicarPermisosRequest;
import com.depazsotelo.matricula.models.RolFuncionalidad;
import com.depazsotelo.matricula.repositories.RolFuncionalidadRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import com.depazsotelo.matricula.services.PermisoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/permisos")
@RequiredArgsConstructor
public class PermisoController {

    private final PermisoService permisoService;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;
    private final AuditoriaService auditoriaService; // MEJORA: auditoría real

    // trae los permisos actuales de un rol para pintar los checkboxes marcados
    @GetMapping("/rol/{idRol}")
    public List<RolFuncionalidad> obtenerPorRol(@PathVariable Integer idRol) {
        return rolFuncionalidadRepository.findByRolIdRol(idRol);
    }

    // el botón "Aplicar" del spec
    @PostMapping("/aplicar")
    public ResponseEntity<?> aplicar(@RequestBody AplicarPermisosRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        try {
            permisoService.aplicarPermisos(request);

            // MEJORA: auditamos el "Aplicar" completo (afecta a varias filas de rol_funcionalidad a la vez)
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
