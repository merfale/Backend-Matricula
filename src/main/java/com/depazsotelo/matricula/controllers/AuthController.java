package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.JwtResponse;
import com.depazsotelo.matricula.dtos.LoginRequest;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import com.depazsotelo.matricula.security.JwtUtils;
import com.depazsotelo.matricula.security.TotpService;
import com.depazsotelo.matricula.services.AuditoriaService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final TotpService totpService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            return ResponseEntity.status(401).body("Usuario o contraseña incorrectos");
        }

        Usuario usuario = usuarioRepository.findByUsuario(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String jwt = jwtUtils.generarToken(usuario);

        auditoriaService.registrar(
                usuario, "Seguridad", "usuario", "LOGIN", usuario.getIdUsuario(),
                (String) null, (String) null, request
        );

        return ResponseEntity.ok(new JwtResponse(jwt));
    }


    @PostMapping("/generar-2fa")
    public ResponseEntity<?> generar2FA(Authentication authentication, HttpServletRequest request) {

        if (authentication == null) {
            return ResponseEntity.status(401).body("Debe iniciar sesión antes de activar el 2FA");
        }

        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        GoogleAuthenticatorKey secretKey = totpService.generarSecreto();


        usuario.setSecret2FA(secretKey.getKey());
        usuarioRepository.save(usuario);


        String qrUrl = totpService.getOtpAuthUrl(secretKey.getKey(), usuario.getUsuario());

        auditoriaService.registrar(
                usuario,
                "Seguridad",
                "usuario",
                "UPDATE",
                usuario.getIdUsuario(),
                (String) null,
                "{\"accion\":\"activacion_2FA\"}",
                request
        );

        Map<String, String> response = new HashMap<>();
        response.put("secret", secretKey.getKey());
        response.put("qrUrl", qrUrl);
        return ResponseEntity.ok(response);
    }
}