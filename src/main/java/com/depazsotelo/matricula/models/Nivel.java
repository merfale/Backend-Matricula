package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "nivel")
public class Nivel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codNivel;

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{2,50}$", message = "Solo letras y espacios")
    @Column(length = 50, unique = true, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado = true;
}