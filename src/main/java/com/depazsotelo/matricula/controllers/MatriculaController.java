package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Matricula;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.MatriculaRepository;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import com.depazsotelo.matricula.security.TotpService;
import com.depazsotelo.matricula.services.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;
    private final UsuarioRepository usuarioRepository;
    private final TotpService totpService; // MEJORA: inyectar el nuevo servicio

    // POST: http://localhost:8081/api/matriculas/registrar?codAlumno=1&codAula=1
    @PostMapping("/registrar")
    public ResponseEntity<?> registrarMatricula(
            @RequestParam Integer codAlumno,
            @RequestParam Integer codAula,
            @RequestParam String codigoTotp, // MEJORA: código de 6 dígitos del Authenticator
            Authentication authentication) { // Spring inyecta al usuario del Token aquí

        try {
            // 1. Sacamos el username del token que viajó en la cabecera
            String username = authentication.getName();

            // 2. Buscamos al usuario real en la base de datos para la Auditoría
            Usuario usuarioAuditoria = usuarioRepository.findByUsuario(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la BD"));

            // MEJORA: validar 2FA ANTES de ejecutar la transacción de matrícula
            if (usuarioAuditoria.getSecret2FA() == null ||
                    !totpService.validarCodigo(usuarioAuditoria.getSecret2FA(), Integer.parseInt(codigoTotp))) {
                return ResponseEntity.status(401).body("Código de autenticación inválido");
            }

            // 3. ¡Lanzamos la transacción mágica!
            Matricula nuevaMatricula = matriculaService.registrarMatriculaTransaccional(
                    codAlumno,
                    codAula,
                    usuarioAuditoria
            );

            // 4. Devolvemos la matrícula generada con un 200 OK
            return ResponseEntity.ok(nuevaMatricula);

        } catch (Exception e) {
            // Si algo falla (ej. ya está matriculado), el rollback actúa y devolvemos el error 400
            return ResponseEntity.badRequest().body("Error al matricular: " + e.getMessage());
        }
    }

    // añadir dentro de MatriculaController.java
    private final MatriculaRepository matriculaRepository; // agrégalo al constructor/campos

    @GetMapping
    public List<Matricula> listar() {
        return matriculaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return matriculaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> anular(@PathVariable Integer id) {
        return matriculaRepository.findById(id)
                .map(matricula -> {
                    matricula.setEstado("anulada");
                    matriculaRepository.save(matricula);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}