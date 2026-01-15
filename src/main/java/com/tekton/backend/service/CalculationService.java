package com.tekton.backend.service;

import com.tekton.backend.dto.CalculationRequest;
import com.tekton.backend.dto.CalculationResponse;
import com.tekton.backend.exception.PercentageNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Servicio para realizar cálculos con porcentaje dinámico.
 * Suma num1 + num2 y aplica un porcentaje adicional obtenido del servicio externo.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CalculationService {

    private final PercentageCacheService percentageCacheService;

    /**
     * Calcula el resultado: suma num1 + num2 y aplica el porcentaje obtenido del servicio externo.
     * 
     * Fórmula: (num1 + num2) * (1 + percentage / 100)
     * 
     * @param request Request con num1 y num2
     * @return CalculationResponse con el resultado del cálculo
     * @throws PercentageNotFoundException si no se puede obtener el porcentaje
     */
    public CalculationResponse calculate(CalculationRequest request) {
        log.debug("Iniciando cálculo para num1={}, num2={}", request.getNum1(), request.getNum2());

        // Obtener porcentaje del servicio externo o caché
        Double percentage = percentageCacheService.getPercentage();
        log.debug("Porcentaje obtenido: {}", percentage);

        // Calcular suma
        Double sum = request.getNum1() + request.getNum2();
        log.debug("Suma calculada: {}", sum);

        // Aplicar porcentaje: (suma) * (1 + percentage / 100)
        Double result = sum * (1 + percentage / 100);
        log.debug("Resultado final: {}", result);

        return CalculationResponse.builder()
                .result(result)
                .num1(request.getNum1())
                .num2(request.getNum2())
                .sum(sum)
                .percentage(percentage)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
