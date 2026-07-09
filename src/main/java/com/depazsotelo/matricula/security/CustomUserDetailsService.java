package com.depazsotelo.matricula.security;

import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        boolean cuentaNoBloqueada = usuario.getBloqueadoHasta() == null
                || !usuario.getBloqueadoHasta().isAfter(LocalDateTime.now());

        return new User(
                usuario.getUsuario(),
                usuario.getPassword(),
                Boolean.TRUE.equals(usuario.getEstado()),
                true,
                true,
                cuentaNoBloqueada,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombreRol()))
        );
    }
}