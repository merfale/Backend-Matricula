package com.depazsotelo.matricula.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PermisoItemRequest {

    @NotNull(message = "Debe indicar la funcionalidad")
    private Integer idFuncionalidad;

    @NotNull private Boolean ver;
    @NotNull private Boolean crear;
    @NotNull private Boolean editar;
    @NotNull private Boolean eliminar;
    @NotNull private Boolean imprimir;
}