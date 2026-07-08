package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.CrearUsuarioRequest;
import com.depazsotelo.matricula.models.Rol;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.RolRepository;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import com.depazsotelo.matricula.dtos.CambiarPasswordRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Usuarios', 'ver')")
    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Usuarios', 'crear')")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody CrearUsuarioRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsuario());
        usuario.setDoc(request.getDoc());
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(rol);
        usuario.setEstado(true);

        usuarioRepository.findByUsuario(authentication.getName())
                .ifPresent(usuario::setUsuarioCreacion);

        Usuario guardado = usuarioRepository.save(usuario);

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

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Usuarios', 'eliminar')")
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

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Usuarios', 'ver')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Usuarios', 'editar')")
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
                    existente.setDoc(request.getDoc());
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

    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request,
                                             Authentication authentication,
                                             HttpServletRequest httpRequest) {

        Usuario usuario = usuarioRepository.findByUsuario(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 1. Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPassword())) {
            return ResponseEntity.badRequest().body("La contraseña actual no es correcta.");
        }

        // 2. Validar que la nueva no esté vacía / sea igual a la actual (mínimo de sentido común)
        if (request.getPasswordNueva() == null || request.getPasswordNueva().isBlank()) {
            return ResponseEntity.badRequest().body("La nueva contraseña no puede estar vacía.");
        }
        if (passwordEncoder.matches(request.getPasswordNueva(), usuario.getPassword())) {
            return ResponseEntity.badRequest().body("La nueva contraseña debe ser diferente a la actual.");
        }

        // 3. Actualizar con hash + salting (BCrypt), igual que en creación de usuarios
        usuario.setPassword(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);

        auditoriaService.registrar(
                usuario, "Seguridad", "usuario", "UPDATE", usuario.getIdUsuario(),
                (String) null, "{\"accion\":\"cambio_password\"}", httpRequest
        );

        return ResponseEntity.ok().build();
    }
}
