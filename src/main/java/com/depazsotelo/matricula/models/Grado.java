package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "grado")
public class Grado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codGrado;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9°º ]{1,50}$", message = "Formato inválido para el grado")
    @Column(length = 50, unique = true, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado = true;
}