package com.depazsotelo.matricula.util;

// Constantes centrales de reglas del sistema. No son filas de una tabla:
// son valores fijos usados directamente en las validaciones (@Size, etc.),
// expuestos de solo lectura vía ParametroController para el panel de Superusuario.
public final class ParametrosSistema {

    private ParametrosSistema() {
    }

    public static final int MIN_LONGITUD_PASSWORD = 6;


    public static final int MAX_INTENTOS_FALLIDOS = 5;
    public static final int MINUTOS_BLOQUEO = 15;
}