package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @Column(length = 30, unique = true, nullable = false)
    private String usuario;

    @Column(length = 255, nullable = false)
    private String password;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @Column(nullable = false)
    private Boolean estado = true;


    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(length = 100)
    private String secret2FA;

    @Column(nullable = false)
    private Integer intentosFallidos = 0;
    private LocalDateTime bloqueadoHasta;

    @UpdateTimestamp
    private LocalDateTime fechaModificacion;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creacion_id")
    private Usuario usuarioCreacion;
}