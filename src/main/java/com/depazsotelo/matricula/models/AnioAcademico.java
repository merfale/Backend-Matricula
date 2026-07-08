package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "anio_academico")
public class AnioAcademico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codAnioAcademico;

    @NotBlank(message = "El año es obligatorio")
    @Pattern(regexp = "^\\d{4}$", message = "El año debe tener 4 dígitos (Ej: 2026)")
    @Column(length = 4, unique = true, nullable = false)
    private String anio;

    @Column(nullable = false)
    private Boolean estado = true;

    private Integer ultimoCorrelativoRecibo = 0;

    @Version
    private Integer version;
}