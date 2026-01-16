package com.tekton.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad para almacenar el historial de llamadas a la API.
 */
@Entity
@Table(name = "api_call_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiCallHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 500)
    private String endpoint;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(nullable = false)
    private Integer statusCode;

    @Column
    private Long executionTimeMs;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}
