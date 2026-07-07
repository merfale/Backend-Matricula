package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.AlumnoRequest;
import com.depazsotelo.matricula.models.Alumno;
import com.depazsotelo.matricula.models.TipoDocumento;
import com.depazsotelo.matricula.repositories.AlumnoRepository;
import com.depazsotelo.matricula.repositories.TipoDocumentoRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoRepository alumnoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final AuditoriaService auditoriaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarAlumno(@RequestBody AlumnoRequest request, Authentication authentication, HttpServletRequest httpRequest) {

        try {
            if (alumnoRepository.findByNumeroDocumento(request.getNumeroDocumento()).isPresent()) {
                return ResponseEntity.badRequest().body("Error: El Documento ya está registrado.");
            }

            TipoDocumento tipoDoc = tipoDocumentoRepository.findById(request.getCodTipoDocumento())
                    .orElseThrow(() -> new RuntimeException("Error: Tipo de documento no encontrado."));

            Alumno nuevoAlumno = new Alumno();
            nuevoAlumno.setTipoDocumento(tipoDoc);
            nuevoAlumno.setNombres(request.getNombres());
            nuevoAlumno.setApellidoPaterno(request.getApellidoPaterno());
            nuevoAlumno.setApellidoMaterno(request.getApellidoMaterno());

            nuevoAlumno.setNumeroDocumento(request.getNumeroDocumento());
            nuevoAlumno.setFechaNacimiento(request.getFechaNacimiento());

            nuevoAlumno.setEstado(true);

            Alumno guardado = alumnoRepository.save(nuevoAlumno);

            auditoriaService.registrar(
                    auditoriaService.usuarioDesdeAuth(authentication),
                    "Alumnos", "alumno", "INSERT", guardado.getCodAlumno(),
                    (String) null,
                    "{\"nombres\":\"" + guardado.getNombres() + " " + guardado.getApellidoPaterno() + " " + guardado.getApellidoMaterno() + "\"}",
                    httpRequest
            );

            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error interno: " + e.getMessage());
        }
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Alumnos', 'eliminar')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id, Authentication authentication, HttpServletRequest request) {
        try {
            Alumno alumno = alumnoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Error: Alumno no encontrado."));

            alumno.setEstado(false);
            alumnoRepository.save(alumno);

            auditoriaService.registrar(
                    auditoriaService.usuarioDesdeAuth(authentication),
                    "Alumnos", "alumno", "DELETE", alumno.getCodAlumno(),
                    "{\"estado\":true}", "{\"estado\":false}", request
            );

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
    public ResponseEntity<?> editar(@PathVariable Integer id, @RequestBody AlumnoRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        return alumnoRepository.findById(id)
                .map(existente -> {
                    try {
                        TipoDocumento tipoDoc = tipoDocumentoRepository.findById(request.getCodTipoDocumento())
                                .orElseThrow(() -> new RuntimeException("Tipo de documento no encontrado."));
                        existente.setTipoDocumento(tipoDoc);
                        existente.setNumeroDocumento(request.getNumeroDocumento());
                        existente.setNombres(request.getNombres());
                        existente.setApellidoPaterno(request.getApellidoPaterno());
                        existente.setApellidoMaterno(request.getApellidoMaterno());
                        existente.setFechaNacimiento(request.getFechaNacimiento());
                        Alumno guardado = alumnoRepository.save(existente);

                        auditoriaService.registrar(
                                auditoriaService.usuarioDesdeAuth(authentication),
                                "Alumnos", "alumno", "UPDATE", guardado.getCodAlumno(),
                                (String) null,
                                "{\"nombres\":\"" + guardado.getNombres() + " " + guardado.getApellidoPaterno() + " " + guardado.getApellidoMaterno() + "\"}",
                                httpRequest
                        );

                        return ResponseEntity.ok(guardado);
                    } catch (RuntimeException e) {
                        return ResponseEntity.badRequest().body(e.getMessage());
                    }
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}