package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.models.*;
import com.depazsotelo.matricula.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final AlumnoRepository alumnoRepository;
    private final AulaRepository aulaRepository;
    private final AnioAcademicoRepository anioAcademicoRepository;
    private final ConceptoRepository conceptoRepository;
    private final TipoConceptoRepository tipoConceptoRepository;
    private final TipoDocumentoRepository tipoDocumentoRepository;
    private final GradoRepository gradoRepository;
    private final NivelRepository nivelRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final FuncionalidadRepository funcionalidadRepository;
    private final RolFuncionalidadRepository rolFuncionalidadRepository;
    private final MatriculaRepository matriculaRepository;
    private final CuotaRepository cuotaRepository;
    private final AuditoriaRepository auditoriaRepository;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Alumnos', 'imprimir')")
    @GetMapping("/alumnos")
    public List<Alumno> reporteAlumnos(@RequestParam(required = false) Boolean estado,
                                       @RequestParam(required = false) String texto) {
        List<Alumno> base = (estado != null) ? alumnoRepository.findByEstado(estado) : alumnoRepository.findAll();
        if (texto == null || texto.isBlank()) return base;
        String buscado = texto.toLowerCase();
        return base.stream()
                .filter(a -> (a.getNombres() + " " + a.getApellidoPaterno() + " " + a.getApellidoMaterno())
                        .toLowerCase().contains(buscado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Aulas', 'imprimir')")
    @GetMapping("/aulas")
    public List<Aula> reporteAulas(@RequestParam(required = false) Integer codAnioAcademico,
                                   @RequestParam(required = false) Integer codNivel,
                                   @RequestParam(required = false) Integer codGrado,
                                   @RequestParam(required = false) Boolean estado) {
        List<Aula> base = (codAnioAcademico != null)
                ? aulaRepository.findByAnioAcademicoCodAnioAcademico(codAnioAcademico)
                : aulaRepository.findAll();
        return base.stream()
                .filter(a -> codNivel == null || a.getNivel().getCodNivel().equals(codNivel))
                .filter(a -> codGrado == null || a.getGrado().getCodGrado().equals(codGrado))
                .filter(a -> estado == null || a.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'AniosAcademicos', 'imprimir')")
    @GetMapping("/anios-academicos")
    public List<AnioAcademico> reporteAniosAcademicos(@RequestParam(required = false) Boolean estado) {
        return anioAcademicoRepository.findAll().stream()
                .filter(a -> estado == null || a.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Conceptos', 'imprimir')")
    @GetMapping("/conceptos")
    public List<Concepto> reporteConceptos(@RequestParam(required = false) Integer codAnioAcademico,
                                           @RequestParam(required = false) Integer codTipoConcepto,
                                           @RequestParam(required = false) Boolean estado,
                                           @RequestParam(required = false) Boolean obligatorio) {
        List<Concepto> base = (codAnioAcademico != null)
                ? conceptoRepository.findByAnioAcademicoCodAnioAcademicoOrderByOrdenPagoAsc(codAnioAcademico)
                : conceptoRepository.findAll();
        return base.stream()
                .filter(c -> codTipoConcepto == null || c.getTipoConcepto().getCodTipoConcepto().equals(codTipoConcepto))
                .filter(c -> estado == null || c.getEstado().equals(estado))
                .filter(c -> obligatorio == null || c.getObligatorio().equals(obligatorio))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'TiposConcepto', 'imprimir')")
    @GetMapping("/tipos-concepto")
    public List<TipoConcepto> reporteTiposConcepto(@RequestParam(required = false) Boolean estado) {
        return tipoConceptoRepository.findAll().stream()
                .filter(t -> estado == null || t.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'TiposDocumento', 'imprimir')")
    @GetMapping("/tipos-documento")
    public List<TipoDocumento> reporteTiposDocumento(@RequestParam(required = false) Boolean estado) {
        return tipoDocumentoRepository.findAll().stream()
                .filter(t -> estado == null || t.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Grados', 'imprimir')")
    @GetMapping("/grados")
    public List<Grado> reporteGrados(@RequestParam(required = false) Boolean estado) {
        return gradoRepository.findAll().stream()
                .filter(g -> estado == null || g.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Niveles', 'imprimir')")
    @GetMapping("/niveles")
    public List<Nivel> reporteNiveles(@RequestParam(required = false) Boolean estado) {
        return nivelRepository.findAll().stream()
                .filter(n -> estado == null || n.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Usuarios', 'imprimir')")
    @GetMapping("/usuarios")
    public List<Usuario> reporteUsuarios(@RequestParam(required = false) Integer idRol,
                                         @RequestParam(required = false) Boolean estado) {
        List<Usuario> base = (idRol != null) ? usuarioRepository.findByRolIdRol(idRol) : usuarioRepository.findAll();
        return base.stream()
                .filter(u -> estado == null || u.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Roles', 'imprimir')")
    @GetMapping("/roles")
    public List<Rol> reporteRoles(@RequestParam(required = false) Boolean estado) {
        return rolRepository.findAll().stream()
                .filter(r -> estado == null || r.getEstado().equals(estado))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Funcionalidades', 'imprimir')")
    @GetMapping("/funcionalidades")
    public List<Funcionalidad> reporteFuncionalidades(@RequestParam(required = false) Boolean soloRaiz) {
        return (Boolean.TRUE.equals(soloRaiz))
                ? funcionalidadRepository.findByPadreIsNull()
                : funcionalidadRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Permisos', 'imprimir')")
    @GetMapping("/permisos")
    public List<RolFuncionalidad> reportePermisos(@RequestParam Integer idRol) {
        return rolFuncionalidadRepository.findByRolIdRol(idRol);
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Matriculas', 'imprimir')")
    @GetMapping("/matriculas")
    public List<Matricula> reporteMatriculas(@RequestParam(required = false) Integer codAnioAcademico,
                                             @RequestParam(required = false) Integer codAula,
                                             @RequestParam(required = false) String estado,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                             @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<Matricula> base = matriculaRepository.findAll();
        if (codAnioAcademico != null) base = matriculaRepository.findByAnioAcademicoCodAnioAcademico(codAnioAcademico);
        else if (codAula != null) base = matriculaRepository.findByAulaCodAula(codAula);

        return base.stream()
                .filter(m -> estado == null || m.getEstado().equalsIgnoreCase(estado))
                .filter(m -> desde == null || !m.getFechaMatricula().isBefore(desde))
                .filter(m -> hasta == null || !m.getFechaMatricula().isAfter(hasta))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Cuotas', 'imprimir')")
    @GetMapping("/cuotas")
    public List<Cuota> reporteCuotas(@RequestParam(required = false) String estado,
                                     @RequestParam(required = false) Integer codMatricula) {
        if (estado != null) return cuotaRepository.findByEstado(estado.toUpperCase());
        if (codMatricula != null) return cuotaRepository.findByMatriculaCodMatricula(codMatricula);
        return cuotaRepository.findAll();
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Pagos', 'imprimir')")
    @GetMapping("/pagos")
    public List<Cuota> reportePagos(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return cuotaRepository.findByEstado("PAGADO").stream()
                .filter(c -> desde == null || !c.getFechaPago().toLocalDate().isBefore(desde))
                .filter(c -> hasta == null || !c.getFechaPago().toLocalDate().isAfter(hasta))
                .collect(Collectors.toList());
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Auditoria', 'imprimir')")
    @GetMapping("/auditoria")
    public List<Auditoria> reporteAuditoria(@RequestParam(required = false) String modulo,
                                            @RequestParam(required = false) String operacion,
                                            @RequestParam(required = false) Integer idUsuario,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        List<Auditoria> base;
        if (modulo != null) base = auditoriaRepository.findByModulo(modulo);
        else if (idUsuario != null) base = auditoriaRepository.findByUsuarioIdUsuario(idUsuario);
        else if (desde != null && hasta != null) {
            base = auditoriaRepository.findByFechaHoraBetween(desde.atStartOfDay(), hasta.atTime(LocalTime.MAX));
        } else {
            base = auditoriaRepository.findAll();
        }
        return base.stream()
                .filter(a -> operacion == null || a.getOperacion().equalsIgnoreCase(operacion))
                .collect(Collectors.toList());
    }
}