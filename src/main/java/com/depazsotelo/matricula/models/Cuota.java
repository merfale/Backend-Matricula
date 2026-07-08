package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cuota")
public class Cuota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codCuota;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_matricula", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_concepto", nullable = false)
    private Concepto concepto;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montoCobrado;

    @Column(length = 20, nullable = false)
    private String estado;

    private LocalDateTime fechaPago;

    @Column(length = 30)
    private String recibo;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    private Integer version;
}