package com.tekton.backend.exception;

/**
 * Excepción lanzada cuando no se puede obtener el porcentaje
 * ni desde el servicio externo ni desde el caché.
 */
public class PercentageNotFoundException extends RuntimeException {

    public PercentageNotFoundException(String message) {
        super(message);
    }

    public PercentageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
