package com.tekton.backend.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tekton.backend.service.ApiCallHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * Aspecto AOP para registrar automáticamente el historial de llamadas a la API.
 * Se ejecuta de forma asíncrona para no afectar el rendimiento de las peticiones.
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class ApiCallHistoryAspect {

    private final ApiCallHistoryService apiCallHistoryService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(com.tekton.backend.aspect.LogApiCall)")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        LocalDateTime timestamp = LocalDateTime.now();
        
        HttpServletRequest request = getRequest();
        String endpoint = request != null ? request.getRequestURI() : "unknown";
        String method = request != null ? request.getMethod() : "unknown";
        
        String requestBody = extractRequestBody(joinPoint);
        String responseBody = null;
        Integer statusCode = 200;
        String errorMessage = null;

        try {
            // Ejecutar el método
            Object result = joinPoint.proceed();
            
            // Extraer respuesta y status code
            if (result instanceof org.springframework.http.ResponseEntity) {
                org.springframework.http.ResponseEntity<?> responseEntity = (org.springframework.http.ResponseEntity<?>) result;
                statusCode = responseEntity.getStatusCode().value();
                responseBody = extractResponseBody(responseEntity.getBody());
            } else {
                responseBody = extractResponseBody(result);
            }
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Guardar historial de forma asíncrona
            apiCallHistoryService.saveHistory(timestamp, endpoint, method, requestBody, responseBody, statusCode, executionTime, null);
            
            return result;
            
        } catch (Exception e) {
            statusCode = 500;
            errorMessage = e.getMessage();
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Guardar historial con error de forma asíncrona
            apiCallHistoryService.saveHistory(timestamp, endpoint, method, requestBody, null, statusCode, executionTime, errorMessage);
            
            throw e;
        }
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            log.warn("No se pudo obtener el request: {}", e.getMessage());
            return null;
        }
    }

    private String extractRequestBody(ProceedingJoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                // Buscar el primer argumento que no sea HttpServletRequest/Response
                for (Object arg : args) {
                    if (arg != null && 
                        !arg.getClass().getName().startsWith("jakarta.servlet") &&
                        !arg.getClass().getName().startsWith("javax.servlet")) {
                        return objectMapper.writeValueAsString(arg);
                    }
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("Error al extraer request body: {}", e.getMessage());
            return null;
        }
    }

    private String extractResponseBody(Object result) {
        try {
            if (result != null) {
                return objectMapper.writeValueAsString(result);
            }
            return null;
        } catch (Exception e) {
            log.warn("Error al extraer response body: {}", e.getMessage());
            return null;
        }
    }

}
