package com.depazsotelo.matricula.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AulaRequest {

    @NotNull(message = "Debe seleccionar el año académico")
    private Integer codAnioAcademico;

    @NotNull(message = "Debe seleccionar el nivel")
    private Integer codNivel;

    @NotNull(message = "Debe seleccionar el grado")
    private Integer codGrado;

    @NotBlank(message = "La sección es obligatoria")
    @Pattern(regexp = "^[A-Za-z]{1,2}$", message = "La sección solo puede tener 1 o 2 letras (Ej: A, B1)")
    private String seccion;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Positive(message = "La capacidad máxima debe ser un número mayor a 0")
    private Short capacidadMaxima;
}