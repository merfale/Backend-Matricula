package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "concepto", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_anio_academico", "nombre_concepto"})
})
public class Concepto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codConcepto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cod_anio_academico", nullable = false)
    private AnioAcademico anioAcademico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cod_tipo_concepto", nullable = false)
    private TipoConcepto tipoConcepto;

    @Column(name = "nombre_concepto", length = 80, nullable = false)
    private String nombreConcepto;


    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(nullable = false)
    private Short ordenPago;

    @Column(nullable = false)
    private Boolean obligatorio;


    @Version
    private Integer version;

    @Column(nullable = false)
    private Boolean estado = true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaRegistro;
}