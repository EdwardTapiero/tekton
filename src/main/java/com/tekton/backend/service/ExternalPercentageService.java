package com.tekton.backend.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Cliente Feign para consumir el servicio externo de porcentaje.
 * Configurado para conectarse al servicio mock (SoapUI o similar).
 * 
 * Nota: No usa fallback. Si falla, lanza excepción para que
 * PercentageCacheService maneje el fallback al caché.
 */
@FeignClient(
    name = "externalPercentageService",
    url = "${external.service.percentage.url}"
)
public interface ExternalPercentageService {

    /**
     * Obtiene el porcentaje desde el servicio externo.
     * 
     * @return Porcentaje como Double
     * @throws feign.FeignException si el servicio no está disponible
     */
    @GetMapping
    Double getPercentage();
}
