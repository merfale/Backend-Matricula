package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "matricula", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_alumno", "cod_anio_academico"})
})
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codMatricula; // PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_alumno", nullable = false)
    private Alumno alumno; // FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_aula", nullable = false)
    private Aula aula; // FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_anio_academico", nullable = false)
    private AnioAcademico anioAcademico; // FK

    @Column(nullable = false)
    private LocalDate fechaMatricula;

    @Column(length = 20, nullable = false)
    private String estado;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaRegistro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_registro_id")
    private Usuario usuarioRegistro;

    @Version
    private Integer version;
}