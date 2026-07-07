package com.depazsotelo.matricula.controllers;

import com.depazsotelo.matricula.dtos.PagoRequest;
import com.depazsotelo.matricula.models.Cuota;
import com.depazsotelo.matricula.services.AuditoriaService;
import com.depazsotelo.matricula.services.PagoService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final AuditoriaService auditoriaService; // MEJORA: auditoría real (operación "PAGO" del spec)

    @PreAuthorize("@permisoService.tienePermiso(authentication.name, 'Pagos', 'editar')")
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarPago(@RequestBody PagoRequest request, Authentication authentication, HttpServletRequest httpRequest) {
        try {

            Cuota cuotaPagada = pagoService.registrarPago(request.getCodCuota());


            auditoriaService.registrar(
                    auditoriaService.usuarioDesdeAuth(authentication),
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
    public ResponseEntity<?> listarCuotas() {
        return ResponseEntity.ok(pagoService.listarTodasLasCuotas());
    }

}
