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
    private Integer idFuncionalidad;

    @NotBlank(message = "El nombre de la funcionalidad es obligatorio")
    @Column(length = 80, unique = true, nullable = false)
    private String nombre; // UK

    @Column(length = 60)
    private String icono;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "padre_id")
    private Funcionalidad padre;
}