package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.PagoRequest;
import com.depazsotelo.matricula.models.Cuota;
import com.depazsotelo.matricula.models.Usuario;
import com.depazsotelo.matricula.repositories.PagoRepository;
import com.depazsotelo.matricula.services.AuditoriaService;
import com.depazsotelo.matricula.services.PagoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final PagoRepository pagoRepository;
    private final AuditoriaService auditoriaService;

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Pagos', 'editar')")
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@Valid @RequestBody PagoRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        try {
            Usuario usuario = auditoriaService.usuarioDesdeAuth(authentication);

            Cuota cuotaPagada = pagoService.registrarPago(request.getCodCuota(), request.getMetodoPago(), usuario);

            auditoriaService.registrar(
                    usuario,
                    "Pagos", "cuota", "PAGO", cuotaPagada.getCodCuota(),
                    "{\"estado\":\"PENDIENTE\"}",
                    "{\"estado\":\"PAGADO\",\"recibo\":\"" + cuotaPagada.getRecibo() + "\"}",
                    httpRequest
            );

            return ResponseEntity.ok(cuotaPagada);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Pagos', 'ver')")
    @GetMapping("/lista")
    public ResponseEntity<?> listarCuotas(
            @RequestParam(required = false) Integer codAlumno,
            @RequestParam(required = false) Integer codAnioAcademico) {
        return ResponseEntity.ok(pagoService.listarCuotas(codAlumno, codAnioAcademico));
    }

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Pagos', 'ver')")
    @GetMapping("/historial/{codCuota}")
    public ResponseEntity<?> historialPago(@PathVariable Integer codCuota) {
        return ResponseEntity.ok(pagoRepository.findByCuotaCodCuota(codCuota));
    }
}