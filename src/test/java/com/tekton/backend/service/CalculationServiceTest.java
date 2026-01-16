package com.tekton.backend.service;

import com.tekton.backend.dto.CalculationRequest;
import com.tekton.backend.dto.CalculationResponse;
import com.tekton.backend.exception.PercentageNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationServiceTest {

    @Mock
    private PercentageCacheService percentageCacheService;

    @InjectMocks
    private CalculationService calculationService;

    @BeforeEach
    void setUp() {
        // Configuración inicial si es necesaria
    }

    @Test
    void testCalculate_Success() {
        // Given
        CalculationRequest request = CalculationRequest.builder()
                .num1(10.0)
                .num2(20.0)
                .build();
        
        Double percentage = 10.5;
        when(percentageCacheService.getPercentage()).thenReturn(percentage);

        // When
        CalculationResponse response = calculationService.calculate(request);

        // Then
        assertNotNull(response);
        assertEquals(10.0, response.getNum1());
        assertEquals(20.0, response.getNum2());
        assertEquals(30.0, response.getSum());
        assertEquals(percentage, response.getPercentage());
        assertEquals(33.15, response.getResult(), 0.01); // (30 * 1.105)
        assertNotNull(response.getTimestamp());
        
        verify(percentageCacheService, times(1)).getPercentage();
    }

    @Test
    void testCalculate_WithPercentageNotFound() {
        // Given
        CalculationRequest request = CalculationRequest.builder()
                .num1(10.0)
                .num2(20.0)
                .build();
        
        when(percentageCacheService.getPercentage())
                .thenThrow(new PercentageNotFoundException("No se pudo obtener el porcentaje"));

        // When & Then
        assertThrows(PercentageNotFoundException.class, () -> {
            calculationService.calculate(request);
        });
        
        verify(percentageCacheService, times(1)).getPercentage();
    }

    @Test
    void testCalculate_WithZeroPercentage() {
        // Given
        CalculationRequest request = CalculationRequest.builder()
                .num1(10.0)
                .num2(20.0)
                .build();
        
        when(percentageCacheService.getPercentage()).thenReturn(0.0);

        // When
        CalculationResponse response = calculationService.calculate(request);

        // Then
        assertNotNull(response);
        assertEquals(30.0, response.getResult(), 0.01); // (30 * 1.0)
        assertEquals(0.0, response.getPercentage());
    }
}
