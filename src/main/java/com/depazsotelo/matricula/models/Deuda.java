package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "deuda", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_matricula"})
})
public class Deuda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codDeuda;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_matricula", nullable = false, unique = true)
    private Matricula matricula;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTotal;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal montoPendiente;

    @Column(length = 20, nullable = false)
    private String estado; // AL_DIA, PENDIENTE, VENCIDA

    private LocalDateTime fechaActualizacion;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    private Integer version;
}