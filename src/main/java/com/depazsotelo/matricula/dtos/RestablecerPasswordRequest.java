package com.depazsotelo.matricula.dtos;

import com.depazsotelo.matricula.util.ParametrosSistema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RestablecerPasswordRequest {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = ParametrosSistema.MIN_LONGITUD_PASSWORD, message = "La nueva contraseña debe tener al menos " + ParametrosSistema.MIN_LONGITUD_PASSWORD + " caracteres")
    private String passwordNueva;
}
