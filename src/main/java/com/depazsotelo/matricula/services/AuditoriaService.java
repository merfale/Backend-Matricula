package com.depazsotelo.matricula.services;

import com.depazsotelo.matricula.models.Auditoria;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.AuditoriaRepository;
import com.depazsotelo.matricula.repositories.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

// MEJORA: centraliza el registro de auditoría para que TODOS los módulos
// (Matrícula, Pago, Login, etc.) lo usen igual, en vez de que cada servicio
// arme su propio objeto Auditoria a mano.
@Service
@RequiredArgsConstructor
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Usuario usuarioDesdeAuth(Authentication authentication) {
        if (authentication == null) return null;
        return usuarioRepository.findByUsuario(authentication.getName()).orElse(null);
    }

    public void registrar(Usuario usuario, String modulo, String tablaAfectada,
                          String operacion, Integer codigoRegistro,
                          Object valorAnterior, Object valorNuevo,
                          HttpServletRequest request) {
        registrar(usuario, modulo, tablaAfectada, operacion, codigoRegistro,
                serializar(valorAnterior), serializar(valorNuevo), request);
    }

    public void registrar(Usuario usuario, String modulo, String tablaAfectada,
                          String operacion, Integer codigoRegistro,
                          String valorAnterior, String valorNuevo,
                          HttpServletRequest request) {

        Auditoria auditoria = new Auditoria();
        auditoria.setUsuario(usuario);
        auditoria.setModulo(modulo);
        auditoria.setTablaAfectada(tablaAfectada);
        auditoria.setOperacion(operacion);
        auditoria.setCodigoRegistro(codigoRegistro);
        auditoria.setValorAnterior(valorAnterior);
        auditoria.setValorNuevo(valorNuevo);
        String ip = request.getHeader("X-Forwarded-For");
        auditoria.setIpOrigen(ip != null ? ip.split(",")[0].trim() : request.getRemoteAddr());
        auditoria.setNavegador(request.getHeader("User-Agent"));

        auditoriaRepository.save(auditoria);
    }

    private String serializar(Object obj) {
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return String.valueOf(obj);
        }
    }
}