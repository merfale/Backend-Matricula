package com.depazsotelo.matricula.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConceptoRequest {

    @NotNull(message = "Debe seleccionar el año académico")
    private Integer codAnioAcademico;

    @NotNull(message = "Debe seleccionar el tipo de concepto")
    private Integer codTipoConcepto;

    @NotBlank(message = "El nombre del concepto es obligatorio")
    @Size(max = 80, message = "El nombre del concepto no puede superar los 80 caracteres")
    private String nombreConcepto;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal monto;

    @NotNull(message = "El orden de pago es obligatorio")
    @Positive(message = "El orden de pago debe ser mayor a 0")
    private Short ordenPago;

    @NotNull(message = "Debe indicar si el concepto es obligatorio")
    private Boolean obligatorio;

    private Integer version;
}