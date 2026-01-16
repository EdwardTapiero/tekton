package com.tekton.backend.controller;

import com.tekton.backend.dto.ApiCallHistoryResponse;
import com.tekton.backend.service.ApiCallHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * Controller para consultar el historial de llamadas a la API.
 */
@Tag(name = "History", description = "API para consultar el historial de llamadas")
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class ApiCallHistoryController {

    private final ApiCallHistoryService apiCallHistoryService;

    @Operation(
        summary = "Obtener historial de llamadas",
        description = "Retorna el historial de llamadas con paginación y filtros opcionales"
    )
    @GetMapping
    public ResponseEntity<Page<ApiCallHistoryResponse>> getHistory(
            @Parameter(description = "Filtro por endpoint (opcional)")
            @RequestParam(required = false) String endpoint,
            
            @Parameter(description = "Fecha de inicio (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Fecha de fin (formato: yyyy-MM-dd)")
            @RequestParam(required = false) 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Número de página (default: 0)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página (default: 10)")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "Campo para ordenar (default: timestamp)")
            @RequestParam(defaultValue = "timestamp") String sortBy,
            
            @Parameter(description = "Dirección del orden (ASC o DESC, default: DESC)")
            @RequestParam(defaultValue = "DESC") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("ASC") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ApiCallHistoryResponse> historyPage = apiCallHistoryService.getHistory(
                endpoint, startDate, endDate, pageable);
        
        return ResponseEntity.status(HttpStatus.OK).body(historyPage);
    }
}
