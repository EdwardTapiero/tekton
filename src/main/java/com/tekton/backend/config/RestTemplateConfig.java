package com.tekton.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de RestTemplate para llamadas a servicios externos.
 */
@Configuration
public class RestTemplateConfig {

    @Value("${external.service.percentage.timeout:5000}")
    private int timeout;

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) TimeUnit.MILLISECONDS.toMillis(timeout));
        factory.setReadTimeout((int) TimeUnit.MILLISECONDS.toMillis(timeout));
        
        return new RestTemplate(factory);
    }
}
