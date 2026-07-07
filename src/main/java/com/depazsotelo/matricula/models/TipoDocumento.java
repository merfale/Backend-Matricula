package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Entity
@Table(name = "tipo_documento")
public class TipoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codTipoDocumento; // PK

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{2,50}$", message = "Solo letras y espacios")
    @Column(length = 20, unique = true, nullable = false)
    private String nombre; // Ej: DNI, CE

    @Column(nullable = false)
    private Boolean estado = true;
}