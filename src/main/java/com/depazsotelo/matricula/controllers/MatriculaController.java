package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.Matricula;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.MatriculaRepository;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import com.depazsotelo.matricula.security.TotpService;
import com.depazsotelo.matricula.services.AuditoriaService;
import com.depazsotelo.matricula.services.MatriculaService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final TotpService totpService;
    private final AuditoriaService auditoriaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarMatricula(
            @RequestParam Integer codAlumno,
            @RequestParam Integer codAula,
            @RequestParam String codigoTotp,
            Authentication authentication,
            HttpServletRequest request) {

        try {
            String username = authentication.getName();

            Usuario usuarioAuditoria = usuarioRepository.findByUsuario(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado en la BD"));

            if (usuarioAuditoria.getSecret2FA() == null ||
                    !totpService.validarCodigo(usuarioAuditoria.getSecret2FA(), Integer.parseInt(codigoTotp))) {
                return ResponseEntity.status(401).body("Código de autenticación inválido");
            }

            Matricula nuevaMatricula = matriculaService.registrarMatriculaTransaccional(
                    codAlumno,
                    codAula,
                    usuarioAuditoria,
                    request
            );
            return ResponseEntity.ok(nuevaMatricula);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al matricular: " + e.getMessage());
        }
    }

    private final MatriculaRepository matriculaRepository;

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
    public ResponseEntity<?> anular(@PathVariable Integer id , Authentication authentication, HttpServletRequest request) {
        return matriculaRepository.findById(id)
                .map(matricula -> {
                    String estadoAnterior = matricula.getEstado();
                    matricula.setEstado("anulada");
                    matriculaRepository.save(matricula);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Matrícula",
                            "matricula",
                            "DELETE",
                            matricula.getCodMatricula(),
                            "{\"estado\":\"" + estadoAnterior + "\"}",
                            "{\"estado\":\"anulada\"}",
                            request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}