package com.tekton.backend.service;

import com.tekton.backend.dto.ApiCallHistoryResponse;
import com.tekton.backend.entity.ApiCallHistory;
import com.tekton.backend.repository.ApiCallHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Servicio para gestionar el historial de llamadas.
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

    /**
     * Obtiene el historial de llamadas con filtros opcionales y paginación.
     * 
     * @param endpoint Filtro por endpoint (opcional)
     * @param startDate Fecha de inicio (opcional)
     * @param endDate Fecha de fin (opcional)
     * @param pageable Configuración de paginación
     * @return Página de historial de llamadas
     */
    public Page<ApiCallHistoryResponse> getHistory(String endpoint, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(LocalTime.MAX) : null;

        Page<ApiCallHistory> historyPage = repository.findByFilters(endpoint, startDateTime, endDateTime, pageable);
        
        return historyPage.map(this::toResponse);
    }

    /**
     * Convierte la entidad a DTO.
     */
    private ApiCallHistoryResponse toResponse(ApiCallHistory history) {
        return ApiCallHistoryResponse.builder()
                .id(history.getId())
                .timestamp(history.getTimestamp())
                .endpoint(history.getEndpoint())
                .method(history.getMethod())
                .requestBody(history.getRequestBody())
                .responseBody(history.getResponseBody())
                .statusCode(history.getStatusCode())
                .executionTimeMs(history.getExecutionTimeMs())
                .errorMessage(history.getErrorMessage())
                .build();
    }
}
