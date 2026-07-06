package com.depazsotelo.matricula.dtos;

import lombok.Data;

@Data
public class AulaRequest {
    private Integer codAnioAcademico;
    private Integer codNivel;
    private Integer codGrado;
    private String seccion;
    private Short capacidadMaxima;
}
