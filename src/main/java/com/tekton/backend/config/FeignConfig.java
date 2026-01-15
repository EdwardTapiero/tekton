package com.tekton.backend.config;

import feign.Logger;
import feign.Request;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de Feign Client para el servicio externo.
 */
@Configuration
public class FeignConfig {

    @Value("${external.service.percentage.timeout:5000}")
    private int timeout;

    /**
     * Configuración del nivel de logging de Feign.
     */
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    /**
     * Configuración de timeout para las peticiones.
     */
    @Bean
    public Request.Options requestOptions() {
        return new Request.Options(timeout, TimeUnit.MILLISECONDS, timeout, TimeUnit.MILLISECONDS, true);
    }

    /**
     * Configuración de reintentos.
     * Retryer.NEVER_RETRY: No reintentar (el fallback manejará el error)
     */
    @Bean
    public Retryer retryer() {
        return Retryer.NEVER_RETRY;
    }
}
