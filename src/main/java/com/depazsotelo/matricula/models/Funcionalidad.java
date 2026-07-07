package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "funcionalidad")
public class Funcionalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idFuncionalidad; // PK

    @NotBlank(message = "El nombre de la funcionalidad es obligatorio")
    @Column(length = 80, unique = true, nullable = false)
    private String nombre; // UK

    @Column(length = 60)
    private String icono;

    // Auto-referencia para construir el Tree JavaScript del menú
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id")
    private Funcionalidad padre; // FK a la misma tabla
}