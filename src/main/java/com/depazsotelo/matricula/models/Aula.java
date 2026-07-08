package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "aula", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_anio_academico", "cod_nivel", "cod_grado", "seccion"})
})
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codAula;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cod_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cod_nivel", nullable = false)
    private Nivel nivel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cod_grado", nullable = false)
    private Grado grado;

    @Column(length = 2, nullable = false)
    private String seccion;

    @Column(nullable = false)
    private Short capacidadMaxima;

    @Column(nullable = false)
    private Boolean estado = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaRegistro;
    @Version
    private Integer version;
}