package com.tekton.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request para calcular con porcentaje dinámico")
public class CalculationRequest {

    @NotNull(message = "num1 es requerido")
    @Schema(description = "Primer número para el cálculo", example = "10.5", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double num1;

    @NotNull(message = "num2 es requerido")
    @Schema(description = "Segundo número para el cálculo", example = "20.3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Double num2;
}
