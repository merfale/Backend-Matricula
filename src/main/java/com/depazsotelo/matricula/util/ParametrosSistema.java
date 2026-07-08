package com.depazsotelo.matricula.util;

// Constantes centrales de reglas del sistema. No son filas de una tabla:
// son valores fijos usados directamente en las validaciones (@Size, etc.),
// expuestos de solo lectura vía ParametroController para el panel de Superusuario.
public final class ParametrosSistema {

    private ParametrosSistema() {
    }

    public static final int MIN_LONGITUD_PASSWORD = 6;
}
