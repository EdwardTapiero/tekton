package com.tekton.backend.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.tekton.backend.config.CacheConfig;
import com.tekton.backend.exception.PercentageNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PercentageCacheServiceTest {

    @Mock
    private Cache<String, Double> percentageCache;

    @Mock
    private ExternalPercentageService externalPercentageService;

    @InjectMocks
    private PercentageCacheService percentageCacheService;

    @Test
    void testGetPercentage_FromExternalService_Success() {
        // Given
        Double expectedPercentage = 10.5;
        when(externalPercentageService.getPercentage()).thenReturn(expectedPercentage);

        // When
        Double result = percentageCacheService.getPercentage();

        // Then
        assertNotNull(result);
        assertEquals(expectedPercentage, result);
        verify(externalPercentageService, times(1)).getPercentage();
        verify(percentageCache, times(1)).put(CacheConfig.PERCENTAGE_CACHE_KEY, expectedPercentage);
    }

    @Test
    void testGetPercentage_FromCache_WhenExternalServiceFails() {
        // Given
        Double cachedPercentage = 15.0;
        when(externalPercentageService.getPercentage())
                .thenThrow(new RestClientException("Service unavailable"));
        when(percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY))
                .thenReturn(cachedPercentage);

        // When
        Double result = percentageCacheService.getPercentage();

        // Then
        assertNotNull(result);
        assertEquals(cachedPercentage, result);
        verify(externalPercentageService, times(1)).getPercentage();
        verify(percentageCache, times(1)).getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY);
    }

    @Test
    void testGetPercentage_ThrowsException_WhenNoCacheAndServiceFails() {
        // Given
        when(externalPercentageService.getPercentage())
                .thenThrow(new RestClientException("Service unavailable"));
        when(percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY))
                .thenReturn(null);

        // When & Then
        assertThrows(PercentageNotFoundException.class, () -> {
            percentageCacheService.getPercentage();
        });
        
        verify(externalPercentageService, times(1)).getPercentage();
        verify(percentageCache, times(1)).getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY);
    }

    @Test
    void testGetPercentageFromCache() {
        // Given
        Double cachedValue = 12.5;
        when(percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY))
                .thenReturn(cachedValue);

        // When
        Double result = percentageCacheService.getPercentageFromCache();

        // Then
        assertEquals(cachedValue, result);
        verify(percentageCache, times(1)).getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY);
    }

    @Test
    void testHasCachedValue_ReturnsTrue() {
        // Given
        when(percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY))
                .thenReturn(10.0);

        // When
        boolean result = percentageCacheService.hasCachedValue();

        // Then
        assertTrue(result);
    }

    @Test
    void testHasCachedValue_ReturnsFalse() {
        // Given
        when(percentageCache.getIfPresent(CacheConfig.PERCENTAGE_CACHE_KEY))
                .thenReturn(null);

        // When
        boolean result = percentageCacheService.hasCachedValue();

        // Then
        assertFalse(result);
    }
}
