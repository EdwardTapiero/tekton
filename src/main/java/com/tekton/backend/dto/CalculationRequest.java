package com.tekton.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la petición de cálculo.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationRequest {

    @NotNull(message = "num1 es requerido")
    private Double num1;

    @NotNull(message = "num2 es requerido")
    private Double num2;
}
