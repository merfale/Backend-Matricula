package com.depazsotelo.matricula.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "vacante", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cod_aula"})
})
public class Vacante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codVacante;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cod_aula", nullable = false, unique = true)
    private Aula aula;

    @Column(nullable = false)
    private Short vacantesOcupadas = 0;

    @Column(nullable = false)
    private Short vacantesDisponibles;

    private LocalDateTime fechaActualizacion;

    @Version
    private Integer version;
}