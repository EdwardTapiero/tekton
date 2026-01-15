package com.tekton.backend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.tekton.backend.config.CacheConfig;
import com.tekton.backend.exception.PercentageNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Servicio para gestionar el caché del porcentaje obtenido del servicio externo.
 * Implementa la estrategia Cache-Aside pattern con fallback automático.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PercentageCacheService {

    private final Cache<String, Double> percentageCache;
    private final ExternalPercentageService externalPercentageService;

    /**
     * Obtiene el porcentaje: primero intenta desde el servicio externo,
     * si falla usa el caché, y si no hay caché lanza excepción.
     * 
     * @return Porcentaje obtenido
     * @throws PercentageNotFoundException si no hay valor disponible
     */
    public Double getPercentage() {
        try {
            // Intentar obtener del servicio externo
            Double percentage = externalPercentageService.getPercentage();
            
            // Si es exitoso, actualizar caché
            if (percentage != null) {
                percentageCache.put(CacheConfig.PERCENTAGE_CACHE_KEY, percentage);
                log.debug("Porcentaje obtenido del servicio externo y almacenado en caché: {}", percentage);
                return percentage;
            }
        } catch (Exception e) {
            log.warn("Error al obtener porcentaje del servicio externo: {}", e.getMessage());
        }

        // Si falla, intentar desde caché
        Double cachedPercentage = percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY);
        
        if (cachedPercentage != null) {
            log.info("Usando porcentaje desde caché: {}", cachedPercentage);
            return cachedPercentage;
        }

        // Si no hay caché, lanzar excepción
        log.error("No se pudo obtener el porcentaje ni desde el servicio externo ni desde el caché");
        throw new PercentageNotFoundException("No se pudo obtener el porcentaje. El servicio externo no está disponible y no hay valor en caché.");
    }

    /**
     * Obtiene el porcentaje desde el caché sin intentar el servicio externo.
     * Útil para operaciones que requieren solo el valor en caché.
     * 
     * @return Porcentaje desde caché o null si no existe
     */
    public Double getPercentageFromCache() {
        return percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY);
    }

    /**
     * Verifica si existe un valor en caché.
     * 
     * @return true si hay valor en caché, false en caso contrario
     */
    public boolean hasCachedValue() {
        return percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY) != null;
    }
}
