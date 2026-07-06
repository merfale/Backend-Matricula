package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.AulaRequest;
import com.depazsotelo.matricula.models.Aula;
import com.depazsotelo.matricula.repositories.AnioAcademicoRepository;
import com.depazsotelo.matricula.repositories.AulaRepository;
import com.depazsotelo.matricula.repositories.GradoRepository;
import com.depazsotelo.matricula.repositories.NivelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> crear(@RequestBody AulaRequest request) {
        try {
            Aula aula = new Aula();
            aplicarDatos(aula, request);
            aula.setEstado(true);
            return ResponseEntity.ok(aulaRepository.save(aula));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody AulaRequest request) {
        return aulaRepository.findById(id)
                .map(existente -> {
                    try {
                        aplicarDatos(existente, request);
                        return ResponseEntity.ok(aulaRepository.save(existente));
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return aulaRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    aulaRepository.save(existente);
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
