package com.tekton.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExternalPercentageServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ExternalPercentageService externalPercentageService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(externalPercentageService, "serviceUrl", "http://localhost:8081/api/percentage");
    }

    @Test
    void testGetPercentage_Success() {
        // Given
        Double expectedPercentage = 10.5;
        ResponseEntity<Double> response = new ResponseEntity<>(expectedPercentage, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Double.class))).thenReturn(response);

        // When
        Double result = externalPercentageService.getPercentage();

        // Then
        assertNotNull(result);
        assertEquals(expectedPercentage, result);
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Double.class));
    }

    @Test
    void testGetPercentage_ThrowsException_WhenServiceUnavailable() {
        // Given
        when(restTemplate.getForEntity(anyString(), eq(Double.class)))
                .thenThrow(new RestClientException("Connection refused"));

        // When & Then
        assertThrows(RestClientException.class, () -> {
            externalPercentageService.getPercentage();
        });
        
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Double.class));
    }

    @Test
    void testGetPercentage_ThrowsException_WhenServiceReturnsNull() {
        // Given
        ResponseEntity<Double> response = ResponseEntity.ok(null);
        when(restTemplate.getForEntity(anyString(), eq(Double.class))).thenReturn(response);

        // When & Then
        assertThrows(RestClientException.class, () -> {
            externalPercentageService.getPercentage();
        });
    }

    @Test
    void testGetPercentage_ThrowsException_WhenServiceReturnsErrorStatus() {
        // Given
        ResponseEntity<Double> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        when(restTemplate.getForEntity(anyString(), eq(Double.class))).thenReturn(response);

        // When & Then
        assertThrows(RestClientException.class, () -> {
            externalPercentageService.getPercentage();
        });
    }
}
