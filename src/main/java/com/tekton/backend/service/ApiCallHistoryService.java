package com.tekton.backend.service;

import com.tekton.backend.entity.ApiCallHistory;
import com.tekton.backend.repository.ApiCallHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para guardar el historial de llamadas de forma asíncrona.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ApiCallHistoryService {

    private final ApiCallHistoryRepository repository;

    @Async("apiHistoryExecutor")
    public void saveHistory(LocalDateTime timestamp, String endpoint, String method,
                           String requestBody, String responseBody, Integer statusCode,
                           Long executionTimeMs, String errorMessage) {
        try {
            ApiCallHistory history = ApiCallHistory.builder()
                    .timestamp(timestamp)
                    .endpoint(endpoint)
                    .method(method)
                    .requestBody(requestBody)
                    .responseBody(responseBody)
                    .statusCode(statusCode)
                    .executionTimeMs(executionTimeMs)
                    .errorMessage(errorMessage)
                    .build();

            repository.save(history);
            log.debug("Historial guardado para endpoint: {}", endpoint);
        } catch (Exception e) {
            log.error("Error al guardar historial: {}", e.getMessage(), e);
        }
    }
}
