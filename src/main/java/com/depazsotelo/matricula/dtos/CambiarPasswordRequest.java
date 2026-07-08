package com.depazsotelo.matricula.dtos;

import com.depazsotelo.matricula.util.ParametrosSistema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CambiarPasswordRequest {

    @NotBlank(message = "Debe indicar la contraseña actual")
    private String passwordActual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = ParametrosSistema.MIN_LONGITUD_PASSWORD, message = "La nueva contraseña debe tener al menos " + ParametrosSistema.MIN_LONGITUD_PASSWORD + " caracteres")
    private String passwordNueva;
}