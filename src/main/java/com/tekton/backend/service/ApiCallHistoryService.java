package com.tekton.backend.service;

import com.tekton.backend.dto.ApiCallHistoryResponse;
import com.tekton.backend.entity.ApiCallHistory;
import com.tekton.backend.repository.ApiCallHistoryRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        Specification<ApiCallHistory> spec = buildSpecification(endpoint, startDate, endDate);
        Page<ApiCallHistory> historyPage = repository.findAll(spec, pageable);
        
        return historyPage.map(this::toResponse);
    }

    /**
     * Construye la especificación para los filtros dinámicos.
     */
    private Specification<ApiCallHistory> buildSpecification(String endpoint, LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (endpoint != null && !endpoint.isEmpty()) {
                predicates.add(cb.equal(root.get("endpoint"), endpoint));
            }

            if (startDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), startDateTime));
            }

            if (endDate != null) {
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), endDateTime));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
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
