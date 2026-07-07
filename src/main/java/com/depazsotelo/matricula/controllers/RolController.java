package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Rol;
import com.depazsotelo.matricula.repositories.RolRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolRepository rolRepository;
    private final AuditoriaService auditoriaService; // MEJORA: auditoría real

    @GetMapping
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Rol rol, Authentication authentication, HttpServletRequest request) {
        rol.setEstado(true);
        Rol guardado = rolRepository.save(rol);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Seguridad", "rol", "INSERT", guardado.getIdRol(),
                (Object) null, guardado, request
        );

        return ResponseEntity.ok(guardado);
    }

    // MEJORA: eliminación lógica, no física (como pide el spec)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        Rol rol = rolRepository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
        rol.setEstado(false);
        Rol guardado = rolRepository.save(rol);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Seguridad", "rol", "DELETE", guardado.getIdRol(),
                "{\"estado\":true}", "{\"estado\":false}", request
        );

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody Rol request,
                                    Authentication authentication, HttpServletRequest httpRequest) {
        return rolRepository.findById(id)
                .map(existente -> {
                    String nombreAnterior = existente.getNombreRol();
                    existente.setNombreRol(request.getNombreRol());
                    Rol guardado = rolRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Seguridad", "rol", "UPDATE", guardado.getIdRol(),
                            "{\"nombreRol\":\"" + nombreAnterior + "\"}",
                            "{\"nombreRol\":\"" + guardado.getNombreRol() + "\"}",
                            httpRequest
                    );

                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
