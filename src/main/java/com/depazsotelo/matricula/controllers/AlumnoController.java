package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.AlumnoRequest;
import com.depazsotelo.matricula.models.Alumno;
import com.depazsotelo.matricula.models.TipoDocumento;
import com.depazsotelo.matricula.repositories.AlumnoRepository;
import com.depazsotelo.matricula.repositories.TipoDocumentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoRepository alumnoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAlumno(@RequestBody AlumnoRequest request) {

        try {
            // 1. Validar que el DNI no exista
            if (alumnoRepository.findByNumeroDocumento(request.getNumeroDocumento()).isPresent()) {
                return ResponseEntity.badRequest().body("Error: El Documento ya está registrado.");
            }

            // 2. Buscar el Tipo de Documento en la BD
            TipoDocumento tipoDoc = tipoDocumentoRepository.findById(request.getCodTipoDocumento())
                    .orElseThrow(() -> new RuntimeException("Error: Tipo de documento no encontrado."));

            // 3. Armar el objeto Alumno
            Alumno nuevoAlumno = new Alumno();
            nuevoAlumno.setTipoDocumento(tipoDoc);
            nuevoAlumno.setNombres(request.getNombres());
            nuevoAlumno.setApellidoPaterno(request.getApellidoPaterno());
            nuevoAlumno.setApellidoMaterno(request.getApellidoMaterno());

            // Cifrado automático gracias al AesEncryptor
            nuevoAlumno.setNumeroDocumento(request.getNumeroDocumento());
            nuevoAlumno.setFechaNacimiento(request.getFechaNacimiento());

            nuevoAlumno.setEstado(true);

            // 4. Guardar en BD
            Alumno guardado = alumnoRepository.save(nuevoAlumno);

            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }
    // MEJORA: valida el checkbox "Eliminar" de la funcionalidad "Alumnos" para el rol del usuario logueado
    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Alumnos', 'eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        // ... tu lógica de eliminación lógica existente
        try {
            // 1. Buscar el alumno, si no existe devolvemos 404
            Alumno alumno = alumnoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Error: Alumno no encontrado."));

            // 2. Eliminación LÓGICA (no física) — el spec exige esto para mantener el historial
            alumno.setEstado(false);
            alumnoRepository.save(alumno);

            return ResponseEntity.ok().build();

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @GetMapping
    public List<Alumno> listar() {
        return alumnoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Integer id) {
        return alumnoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody AlumnoRequest request) {
        return alumnoRepository.findById(id)
                .map(existente -> {
                    try {
                        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(request.getCodTipoDocumento())
                                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado."));
                        existente.setTipoDocumento(tipoDoc);
                        existente.setNumeroDocumento(request.getNumeroDocumento()); // se re-cifra solo
                        existente.setNombres(request.getNombres());
                        existente.setApellidoPaterno(request.getApellidoPaterno());
                        existente.setApellidoMaterno(request.getApellidoMaterno());
                        existente.setFechaNacimiento(request.getFechaNacimiento());
                        return ResponseEntity.ok(alumnoRepository.save(existente));
                    } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}