package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.ConceptoRequest;
import com.depazsotelo.matricula.models.Concepto;
import com.depazsotelo.matricula.repositories.AnioAcademicoRepository;
import com.depazsotelo.matricula.repositories.ConceptoRepository;
import com.depazsotelo.matricula.repositories.TipoConceptoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/conceptos")
@RequiredArgsConstructor
public class ConceptoController {

    private final ConceptoRepository conceptoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final TipoConceptoRepository tipoConceptoRepository;

    @GetMapping
    public List<Concepto> listar() {
        return conceptoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return conceptoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody ConceptoRequest request) {
        try {
            Concepto concepto = new Concepto();
            aplicarDatos(concepto, request);
            concepto.setEstado(true);
            return ResponseEntity.ok(conceptoRepository.save(concepto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // MEJORA: respeta el @Version del modelo para evitar sobrescribir cambios concurrentes
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody ConceptoRequest request) {
        return conceptoRepository.findById(id)
                .map(existente -> {
                    try {
                        aplicarDatos(existente, request);
                        return ResponseEntity.ok(conceptoRepository.save(existente));
                    } catch (OptimisticLockingFailureException e) {
                        return ResponseEntity.status(409).body("El concepto fue modificado por otro usuario, recarga los datos.");
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        return conceptoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    conceptoRepository.save(existente);
                    return ResponseEntity.ok().build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private void aplicarDatos(Concepto concepto, ConceptoRequest request) throws Exception {
        concepto.setAnioAcademico(anioAcademicoRepository.findById(request.getCodAnioAcademico())
                .orElseThrow(() -> new Exception("Año académico no encontrado")));
        concepto.setTipoConcepto(tipoConceptoRepository.findById(request.getCodTipoConcepto())
                .orElseThrow(() -> new Exception("Tipo de concepto no encontrado")));
        concepto.setNombreConcepto(request.getNombreConcepto());
        concepto.setMonto(request.getMonto());
        concepto.setOrdenPago(request.getOrdenPago());
        concepto.setObligatorio(request.getObligatorio());
    }
}
