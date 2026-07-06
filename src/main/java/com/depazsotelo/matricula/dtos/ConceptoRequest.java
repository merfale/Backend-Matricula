package com.depazsotelo.matricula.dtos;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ConceptoRequest {
    private Integer codAnioAcademico;
    private Integer codTipoConcepto;
    private String nombreConcepto;
    private BigDecimal monto;
    private Short ordenPago;
    private Boolean obligatorio;
    private Integer version;
}