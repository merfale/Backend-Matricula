package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "pago")
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codPago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_cuota", nullable = false)
    private Cuota cuota;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montoPagado;

    @Column(length = 30, nullable = false)
    private String metodoPago; // EFECTIVO, TRANSFERENCIA, TARJETA

    @Column(length = 30, nullable = false, unique = true)
    private String recibo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_registro_id")
    private Usuario usuarioRegistro;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaPago;

    @Version
    private Integer version;
}