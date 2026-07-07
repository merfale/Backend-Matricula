package com.depazsotelo.matricula.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class AplicarPermisosRequest {

    @NotNull(message = "Debe indicar el rol")
    private Integer idRol;

    @NotEmpty(message = "Debe enviar al menos un permiso")
    @Valid
    private List<PermisoItemRequest> permisos;
}