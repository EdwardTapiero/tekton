package com.tekton.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Servicio para consumir el servicio externo de porcentaje.
 * Usa RestTemplate para realizar llamadas HTTP al servicio mock (SoapUI o similar).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalPercentageService {

    private final RestTemplate restTemplate;

    @Value("${external.service.percentage.url}")
    private String serviceUrl;

    /**
     * Obtiene el porcentaje desde el servicio externo.
     * 
     * @return Porcentaje como Double
     * @throws RestClientException si el servicio no está disponible
     */
    public Double getPercentage() {
        try {
            log.debug("Llamando al servicio externo: {}", serviceUrl);
            ResponseEntity<Double> response = restTemplate.getForEntity(serviceUrl, Double.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.debug("Porcentaje obtenido del servicio externo: {}", response.getBody());
                return response.getBody();
            }
            
            throw new RestClientException("El servicio externo retornó un código de estado inválido: " + response.getStatusCode());
        } catch (RestClientException e) {
            log.error("Error al llamar al servicio externo: {}", e.getMessage());
            throw e;
        }
    }
}
