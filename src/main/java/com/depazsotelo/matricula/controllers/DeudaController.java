package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.repositories.DeudaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deudas")
@RequiredArgsConstructor
public class DeudaController {

    private final DeudaRepository deudaRepository;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Pagos', 'ver')")
    @GetMapping
    public Object listar(
            @RequestParam(required = false) Integer codAlumno,
            @RequestParam(required = false) Integer codAnioAcademico,
            @RequestParam(required = false) String estado) {

        if (codAlumno != null && estado != null) {
            return deudaRepository.findByMatricula_Alumno_CodAlumnoAndEstado(codAlumno, estado);
        }
        if (codAlumno != null) {
            return deudaRepository.findByMatricula_Alumno_CodAlumno(codAlumno);
        }
        if (codAnioAcademico != null && estado != null) {
            return deudaRepository.findByMatricula_AnioAcademico_CodAnioAcademicoAndEstado(codAnioAcademico, estado);
        }
        if (codAnioAcademico != null) {
            return deudaRepository.findByMatricula_AnioAcademico_CodAnioAcademico(codAnioAcademico);
        }
        if (estado != null) {
            return deudaRepository.findByEstado(estado);
        }
        return deudaRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Pagos', 'ver')")
    @GetMapping("/{codMatricula}")
    public Object obtener(@PathVariable Integer codMatricula) {
        return deudaRepository.findByMatriculaCodMatricula(codMatricula).orElse(null);
    }
}