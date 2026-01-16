package com.tekton.backend.controller;

import com.tekton.backend.aspect.LogApiCall;
import com.tekton.backend.dto.CalculationRequest;
import com.tekton.backend.dto.CalculationResponse;
import com.tekton.backend.service.CalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller para el endpoint de cálculo con porcentaje dinámico.
 */
@Tag(name = "Calculation", description = "API para cálculos con porcentaje dinámico")
@RestController
@RequestMapping("/api/calculate")
@RequiredArgsConstructor
public class CalculationController {

    private final CalculationService calculationService;

    @Operation(
        summary = "Calcular con porcentaje dinámico",
        description = "Suma num1 y num2, y aplica un porcentaje adicional obtenido del servicio externo"
    )
    @PostMapping
    @LogApiCall
    public ResponseEntity<CalculationResponse> calculate(@Valid @RequestBody CalculationRequest request) {
        CalculationResponse response = calculationService.calculate(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
