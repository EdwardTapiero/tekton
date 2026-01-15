package com.tekton.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta del cálculo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationResponse {

    private Double result;
    private Double num1;
    private Double num2;
    private Double sum;
    private Double percentage;
    private LocalDateTime timestamp;
}
