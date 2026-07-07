package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.AulaRequest;
import com.depazsotelo.matricula.models.Aula;
import com.depazsotelo.matricula.repositories.AnioAcademicoRepository;
import com.depazsotelo.matricula.repositories.AulaRepository;
import com.depazsotelo.matricula.repositories.GradoRepository;
import com.depazsotelo.matricula.repositories.NivelRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/aulas")
@RequiredArgsConstructor
public class AulaController {

    private final AulaRepository aulaRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final NivelRepository nivelRepository;
    private final GradoRepository gradoRepository;
    private final AuditoriaService auditoriaService;

    @GetMapping
    public List<Aula> listar() {
        return aulaRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return aulaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody AulaRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        try {
            Aula aula = new Aula();
            aplicarDatos(aula, request);
            aula.setEstado(true);
            Aula guardada = aulaRepository.save(aula);

            auditoriaService.registrar(
                    auditoriaService.usuarioDesdeAuth(authentication),
                    "Aulas", "aula", "INSERT", guardada.getCodAula(),
                    (Object) null, guardada, httpRequest
            );

            return ResponseEntity.ok(guardada);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody AulaRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        return aulaRepository.findById(id)
                .map(existente -> {
                    try {
                        aplicarDatos(existente, request);
                        Aula guardada = aulaRepository.save(existente);

                        auditoriaService.registrar(
                                auditoriaService.usuarioDesdeAuth(authentication),
                                "Aulas", "aula", "UPDATE", guardada.getCodAula(),
                                (Object) null, guardada, httpRequest
                        );

                        return ResponseEntity.ok(guardada);
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return aulaRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    Aula guardada = aulaRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Aulas", "aula", "DELETE", guardada.getCodAula(),
                            "{\"estado\":true}", "{\"estado\":false}", request
                    );

                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void aplicarDatos(Aula aula, AulaRequest request) throws Exception {
        aula.setAnioAcademico(anioAcademicoRepository.findById(request.getCodAnioAcademico())
                .orElseThrow(() -> new Exception("Año académico no encontrado")));
        aula.setNivel(nivelRepository.findById(request.getCodNivel())
                .orElseThrow(() -> new Exception("Nivel no encontrado")));
        aula.setGrado(gradoRepository.findById(request.getCodGrado())
                .orElseThrow(() -> new Exception("Grado no encontrado")));
        aula.setSeccion(request.getSeccion());
        aula.setCapacidadMaxima(request.getCapacidadMaxima());
    }
}
