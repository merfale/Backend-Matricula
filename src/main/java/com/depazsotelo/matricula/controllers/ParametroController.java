package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.security.JwtUtils;
import com.depazsotelo.matricula.util.ParametrosSistema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/parametros")
@RequiredArgsConstructor
public class ParametroController {

    private final JwtUtils jwtUtils;

    @GetMapping
    public Map<String, Object> listar() {
        Map<String, Object> parametros = new LinkedHashMap<>();
        parametros.put("minLongitudPassword", ParametrosSistema.MIN_LONGITUD_PASSWORD);
        parametros.put("expiracionSesionMinutos", jwtUtils.getExpirationMinutes());
        parametros.put("vacantesPorAulaInfo", "La capacidad máxima se configura por cada aula en el módulo Aulas (campo capacidadMaxima), no es un valor global.");
        return parametros;
    }
}
