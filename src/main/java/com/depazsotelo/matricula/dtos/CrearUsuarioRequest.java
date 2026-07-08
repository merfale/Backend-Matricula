package com.depazsotelo.matricula.dtos;

import com.depazsotelo.matricula.util.ParametrosSistema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearUsuarioRequest {

    @NotBlank(message = "El usuario es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9._-]{4,30}$",
            message = "El usuario solo puede tener letras, números, puntos, guiones y guion bajo (4-30 caracteres)")
    private String usuario;

    @Size(max = 20, message = "El documento no puede tener más de 20 caracteres")
    private String doc;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = ParametrosSistema.MIN_LONGITUD_PASSWORD, message = "La contraseña debe tener al menos " + ParametrosSistema.MIN_LONGITUD_PASSWORD + " caracteres")
    private String password;

    @NotNull(message = "Debe seleccionar un rol")
    private Integer idRol;
}