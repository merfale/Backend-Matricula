package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.ConceptoRequest;
import com.depazsotelo.matricula.models.Concepto;
import com.depazsotelo.matricula.repositories.AnioAcademicoRepository;
import com.depazsotelo.matricula.repositories.ConceptoRepository;
import com.depazsotelo.matricula.repositories.TipoConceptoRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.depazsotelo.matricula.services.ConceptoService;
import java.util.List;

@RestController
@RequestMapping("/api/conceptos")
@RequiredArgsConstructor
public class ConceptoController {

    private final ConceptoRepository conceptoRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final TipoConceptoRepository tipoConceptoRepository;
    private final AuditoriaService auditoriaService;
    private final ConceptoService conceptoService;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Conceptos', 'ver')")
    @GetMapping
    public List<Concepto> listar() {
        return conceptoRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Conceptos', 'ver')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return conceptoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Conceptos', 'crear')")
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody ConceptoRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        try {
            Concepto concepto = new Concepto();
            aplicarDatos(concepto, request);
            concepto.setEstado(true);
            Concepto guardado = conceptoRepository.save(concepto);

            auditoriaService.registrar(
                    auditoriaService.usuarioDesdeAuth(authentication),
                    "Conceptos", "concepto", "INSERT", guardado.getCodConcepto(),
                    (Object) null, guardado, httpRequest
            );

            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Conceptos', 'editar')")
    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@Valid @PathVariable Integer id, @RequestBody ConceptoRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        return conceptoRepository.findById(id)
                .map(existente -> {
                    try {
                        String antesJson = String.format(
                                "{\"nombreConcepto\":\"%s\",\"monto\":%s,\"version\":%d}",
                                existente.getNombreConcepto(), existente.getMonto(), existente.getVersion());

                        aplicarDatos(existente, request);
                        Concepto guardado = conceptoRepository.save(existente);

                        String despuesJson = String.format(
                                "{\"nombreConcepto\":\"%s\",\"monto\":%s,\"version\":%d}",
                                guardado.getNombreConcepto(), guardado.getMonto(), guardado.getVersion());

                        auditoriaService.registrar(
                                auditoriaService.usuarioDesdeAuth(authentication),
                                "Conceptos", "concepto", "UPDATE", guardado.getCodConcepto(),
                                antesJson,
                                despuesJson,
                                httpRequest
                        );

                        return ResponseEntity.ok(guardado);
                    } catch (OptimisticLockingFailureException e) {
                        return ResponseEntity.status(409).body("El concepto fue modificado por otro usuario, recarga los datos.");
                    } catch (Exception e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Conceptos', 'eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        return conceptoRepository.findById(id)
                .map(existente -> {
                    existente.setEstado(false);
                    Concepto guardado = conceptoRepository.save(existente);

                    auditoriaService.registrar(
                            auditoriaService.usuarioDesdeAuth(authentication),
                            "Conceptos", "concepto", "DELETE", guardado.getCodConcepto(),
                            "{\"estado\":true}", "{\"estado\":false}", request
                    );

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

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Conceptos', 'crear')")
    @PostMapping("/clonar")
    public ResponseEntity<?> clonar(@RequestParam Integer codAnioOrigen, @RequestParam Integer codAnioDestino,
                                    Authentication authentication, HttpServletRequest httpRequest) {
        try {
            List<Concepto> clonados = conceptoService.clonarConceptos(codAnioOrigen, codAnioDestino);

            auditoriaService.registrar(
                    auditoriaService.usuarioDesdeAuth(authentication),
                    "Conceptos", "concepto", "CLONAR", codAnioDestino,
                    "{\"anioOrigen\":" + codAnioOrigen + "}",
                    "{\"anioDestino\":" + codAnioDestino + ",\"cantidadClonada\":" + clonados.size() + "}",
                    httpRequest
            );

            return ResponseEntity.ok(clonados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
