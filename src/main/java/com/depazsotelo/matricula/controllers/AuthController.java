package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.JwtResponse;
import com.depazsotelo.matricula.dtos.LoginRequest;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import com.depazsotelo.matricula.security.JwtUtils;
import com.depazsotelo.matricula.security.TotpService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
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

    private final TotpService totpService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );


        Usuario usuario = usuarioRepository.findByUsuario(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        String jwt = jwtUtils.generarToken(usuario);


        return ResponseEntity.ok(new JwtResponse(jwt));
    }


    @PostMapping("/generar-2fa")
    public ResponseEntity<?> generar2FA(Authentication authentication) {


        String username = authentication.getName();
        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        GoogleAuthenticatorKey secretKey = totpService.generarSecreto();


        usuario.setSecret2FA(secretKey.getKey());
        usuarioRepository.save(usuario);


        String qrUrl = totpService.getOtpAuthUrl(secretKey.getKey(), usuario.getUsuario());


        Map<String, String> response = new HashMap<>();
        response.put("secret", secretKey.getKey());
        response.put("qrUrl", qrUrl);
        return ResponseEntity.ok(response);
    }
}