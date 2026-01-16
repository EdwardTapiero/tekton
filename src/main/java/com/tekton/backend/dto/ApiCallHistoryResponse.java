package com.tekton.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta del historial de llamadas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallHistoryResponse {

    private Long id;
    private LocalDateTime timestamp;
    private String endpoint;
    private String method;
    private String requestBody;
    private String responseBody;
    private Integer statusCode;
    private Long executionTimeMs;
    private String errorMessage;
}
