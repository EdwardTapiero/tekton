package com.tekton.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Respuesta del cálculo con porcentaje dinámico")
public class CalculationResponse {

    @Schema(description = "Resultado final del cálculo: (num1 + num2) * (1 + percentage / 100)", example = "34.08")
    private Double result;

    @Schema(description = "Primer número ingresado", example = "10.5")
    private Double num1;

    @Schema(description = "Segundo número ingresado", example = "20.3")
    private Double num2;

    @Schema(description = "Suma de num1 + num2", example = "30.8")
    private Double sum;

    @Schema(description = "Porcentaje obtenido del servicio externo", example = "10.5")
    private Double percentage;

    @Schema(description = "Fecha y hora del cálculo", example = "2024-01-15T10:30:00")
    private LocalDateTime timestamp;
}
