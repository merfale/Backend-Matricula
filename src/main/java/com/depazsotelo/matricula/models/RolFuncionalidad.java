package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "rol_funcionalidad")
public class RolFuncionalidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRolFuncionalidad;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_rol", nullable = false)
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionalidad", nullable = false)
    private Funcionalidad funcionalidad;


    @Column(nullable = false)
    private Boolean ver = false;

    @Column(nullable = false)
    private Boolean crear = false;

    @Column(nullable = false)
    private Boolean editar = false;

    @Column(nullable = false)
    private Boolean eliminar = false;

    @Column(nullable = false)
    private Boolean imprimir = false;
}