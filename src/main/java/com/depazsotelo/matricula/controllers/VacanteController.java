package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.repositories.VacanteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacantes")
@RequiredArgsConstructor
public class VacanteController {

    private final VacanteRepository vacanteRepository;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Aulas', 'ver')")
    @GetMapping
    public Object listar(@RequestParam(required = false) Integer codAnioAcademico) {
        if (codAnioAcademico != null) {
            return vacanteRepository.findByAula_AnioAcademico_CodAnioAcademico(codAnioAcademico);
        }
        return vacanteRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Aulas', 'ver')")
    @GetMapping("/{codAula}")
    public Object obtener(@PathVariable Integer codAula) {
        return vacanteRepository.findByAulaCodAula(codAula).orElse(null);
    }
}