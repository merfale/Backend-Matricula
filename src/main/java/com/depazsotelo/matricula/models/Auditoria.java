package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codAuditoria;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_usuario", nullable = false)
    private Usuario usuario;

    @Column(length = 50, nullable = false)
    private String modulo;

    @Column(length = 50, nullable = false)
    private String tablaAfectada;

    @Column(length = 20, nullable = false)
    private String operacion;

    @Column(nullable = false)
    private Integer codigoRegistro;


    @Column(columnDefinition = "TEXT")
    private String valorAnterior;


    @Column(columnDefinition = "TEXT")
    private String valorNuevo;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(length = 45, nullable = false)
    private String ipOrigen;

    @Column(length = 100)
    private String equipo;

    @Column(length = 150)
    private String navegador;
}