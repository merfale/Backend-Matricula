package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
@Table(name = "rol")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRol;

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{3,40}$", message = "El nombre del rol solo puede contener letras y espacios")
    @Column(length = 40, unique = true, nullable = false)
    private String nombreRol;

    @Column(nullable = false)
    private Boolean estado = true;
}