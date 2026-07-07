package com.depazsotelo.matricula.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AlumnoRequest {

    @NotNull(message = "Debe seleccionar el tipo de documento")
    private Integer codTipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9]{6,20}$",
            message = "El número de documento solo puede tener letras y números (6-20 caracteres), sin espacios ni símbolos")
    private String numeroDocumento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{2,80}$",
            message = "Los nombres solo pueden contener letras y espacios")
    private String nombres;

    @NotBlank(message = "El apellido paterno es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{2,60}$",
            message = "El apellido paterno solo puede contener letras y espacios")
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno es obligatorio")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ ]{2,60}$",
            message = "El apellido materno solo puede contener letras y espacios")
    private String apellidoMaterno;

    @NotBlank(message = "La fecha de nacimiento es obligatoria")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$",
            message = "La fecha de nacimiento debe tener el formato AAAA-MM-DD")
    private String fechaNacimiento;
}