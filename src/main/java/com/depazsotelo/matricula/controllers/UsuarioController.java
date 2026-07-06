package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.CrearUsuarioRequest;
import com.depazsotelo.matricula.models.Rol;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.RolRepository;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
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

    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // MEJORA: crear usuario + asignar rol, solo el Superusuario debería llegar aquí (ver punto 6)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CrearUsuarioRequest request, Authentication authentication) {
        Rol rol = rolRepository.findById(request.getIdRol())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = new Usuario();
        usuario.setUsuario(request.getUsuario());
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // hash + salting vía BCrypt
        usuario.setRol(rol);
        usuario.setEstado(true);

        // MEJORA: registrar quién creó al usuario (campo usuarioCreacion del modelo)
        usuarioRepository.findByUsuario(authentication.getName())
                .ifPresent(usuario::setUsuarioCreacion);

        return ResponseEntity.ok(usuarioRepository.save(usuario));
    }

    // MEJORA: eliminación lógica — el Superusuario nunca se puede eliminar (regla del spec)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLogico(@PathVariable Integer id) {
        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if ("SUPERUSUARIO".equalsIgnoreCase(usuario.getRol().getNombreRol())) {
            return ResponseEntity.badRequest().body("El Superusuario no puede eliminarse.");
        }

        usuario.setEstado(false);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return usuarioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // MEJORA: edita usuario/rol; el cambio de password sigue yendo por su propio endpoint (CambiarPasswordRequest)
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody CrearUsuarioRequest request) {
        return usuarioRepository.findById(id)
                .map(existente -> {
                    if ("SUPERUSUARIO".equalsIgnoreCase(existente.getRol().getNombreRol())
                            && !existente.getRol().getIdRol().equals(request.getIdRol())) {
                        return ResponseEntity.badRequest().body("No se puede cambiar el rol del Superusuario.");
                    }
                    Rol rol = rolRepository.findById(request.getIdRol())
                            .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
                    existente.setUsuario(request.getUsuario());
                    existente.setRol(rol);
                    return ResponseEntity.ok(usuarioRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}