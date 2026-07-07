package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.CrearUsuarioRequest;
import com.depazsotelo.matricula.models.Rol;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.RolRepository;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService; // MEJORA: auditoría real

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }


    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearUsuarioRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsuario());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // hash + salting vía BCrypt
        usuario.setRol(rol);
        usuario.setEstado(true);

        // registrar quién creó al usuario (campo usuarioCreacion del modelo)
        usuarioRepository.findByUsuario(authentication.getName())
                .ifPresent(usuario::setUsuarioCreacion);

        Usuario guardado = usuarioRepository.save(usuario);

        // MEJORA: auditoría de creación (nunca guardamos el password en claro en el log)
        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Seguridad",
                "usuario",
                "INSERT",
                guardado.getIdUsuario(),
                (String) null,
                "{\"usuario\":\"" + guardado.getUsuario() + "\",\"rol\":\"" + rol.getNombreRol() + "\"}",
                httpRequest
        );

        return ResponseEntity.ok(guardado);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if ("SUPERUSUARIO".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            return ResponseEntity.badRequest().body("El Superusuario no puede eliminarse.");
        }

        usuario.setEstado(false);
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                auditoriaService.usuarioDesdeAuth(authentication),
                "Seguridad",
                "usuario",
                "DELETE",
                usuario.getIdUsuario(),
                "{\"estado\":true}",
                "{\"estado\":false}",
                request
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody CrearUsuarioRequest request,
                                    Authentication authentication, HttpServletRequest httpRequest) {
        return usuarioRepository.findById(id)
                .map(existente -> {
                    if ("SUPERUSUARIO".equalsIgnoreCase(existente.getRol().getNombreRol())
                            && !existente.getRol().getIdRol().equals(request.getIdRol())) {
                        return ResponseEntity.badRequest().body("No se puede cambiar el rol del Superusuario.");
                    }
                    Rol rolAnterior = existente.getRol();
                    String usuarioAnteriorNombre = existente.getUsuario();

                    Rol rol = rolRepository.findById(request.getIdRol())
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                    existente.setUsuario(request.getUsuario());
                    existente.setRol(rol);
                    Usuario guardado = usuarioRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Seguridad",
                            "usuario",
                            "UPDATE",
                            guardado.getIdUsuario(),
                            "{\"usuario\":\"" + usuarioAnteriorNombre + "\",\"rol\":\"" + rolAnterior.getNombreRol() + "\"}",
                            "{\"usuario\":\"" + guardado.getUsuario() + "\",\"rol\":\"" + rol.getNombreRol() + "\"}",
                            httpRequest
                    );

                    return ResponseEntity.ok(guardado);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
