package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@Entity
@Table(name = "tipo_concepto")
public class TipoConcepto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codTipoConcepto; // PK

    @NotBlank(message = "El nombre es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÑáéíóúñ ]{2,50}$", message = "Solo letras y espacios")
    @Column(length = 50, unique = true, nullable = false)
    private String nombre; // Ej: Fijo, Mensual, Opcional

    @Column(nullable = false)
    private Boolean estado = true;
}