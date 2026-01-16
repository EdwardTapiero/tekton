package com.tekton.backend.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación para marcar métodos que deben registrar su ejecución en el historial.
 * Se usa en conjunto con ApiCallHistoryAspect para registro asíncrono.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogApiCall {
}
