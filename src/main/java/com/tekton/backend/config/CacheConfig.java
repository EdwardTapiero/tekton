package com.tekton.backend.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuración de Caffeine Cache para almacenar el porcentaje obtenido del servicio externo.
 * TTL: 30 minutos
 */
@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PERCENTAGE_CACHE_KEY = "percentage";
    public static final String PERCENTAGE_CACHE_NAME = "percentageCache";

    /**
     * CacheManager para Spring Cache abstraction.
     */
    @Bean
    public CaffeineCacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        return cacheManager;
    }

    /**
     * Bean para acceso directo al cache de porcentaje.
     * Útil para operaciones manuales de lectura/escritura cuando el servicio externo falla.
     */
    @Bean(name = "percentageCache")
    public Cache<String, Double> percentageCache() {
        return Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats()
                .build();
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .recordStats();
    }
}
