package com.depazsotelo.matricula.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PagoRequest {

    @NotNull(message = "Debe indicar la cuota a pagar")
    private Integer codCuota;
}