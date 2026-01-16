package com.tekton.backend.integration;

import com.tekton.backend.dto.CalculationRequest;
import com.tekton.backend.dto.CalculationResponse;
import com.tekton.backend.repository.ApiCallHistoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestPropertySource(properties = {
    "external.service.percentage.url=http://localhost:9999/api/percentage"
})
class CalculationIntegrationTest {

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ApiCallHistoryRepository historyRepository;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("tekton_test")
            .withUsername("test_user")
            .withPassword("test_password")
            .withReuse(true);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void testCalculate_Integration_ServiceUnavailable() {
        // Given - El servicio externo no está disponible (esperado en tests)
        CalculationRequest request = new CalculationRequest();
        request.setNum1(10.0);
        request.setNum2(20.0);

        String url = "http://localhost:" + port + "/api/calculate";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CalculationRequest> entity = new HttpEntity<>(request, headers);

        // When & Then - Esperamos que falle porque no hay servicio externo ni caché
        assertThrows(HttpServerErrorException.ServiceUnavailable.class, () -> {
            restTemplate.exchange(url, HttpMethod.POST, entity, CalculationResponse.class);
        });
    }

    @Test
    void testHistory_IsSaved_AfterCalculation_EvenWhenServiceFails() {
        // Given - El servicio externo no está disponible, pero el historial debe guardarse
        CalculationRequest request = new CalculationRequest();
        request.setNum1(10.0);
        request.setNum2(20.0);

        String url = "http://localhost:" + port + "/api/calculate";
        long initialCount = historyRepository.count();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CalculationRequest> entity = new HttpEntity<>(request, headers);
        
        // When - La llamada falla pero el historial debe guardarse
        try {
            restTemplate.exchange(url, HttpMethod.POST, entity, CalculationResponse.class);
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            // Esperado - el servicio externo no está disponible
        }

        // Then - Esperar un poco para que el guardado asíncrono se complete
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long finalCount = historyRepository.count();
        assertTrue(finalCount > initialCount, "El historial debería haberse guardado incluso cuando el servicio falla");
    }
}
