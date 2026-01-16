package com.tekton.backend.repository;

import com.tekton.backend.entity.ApiCallHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Repositorio para el historial de llamadas a la API.
 */
@Repository
public interface ApiCallHistoryRepository extends JpaRepository<ApiCallHistory, Long> {

    /**
     * Busca el historial con filtros opcionales.
     */
    @Query("SELECT h FROM ApiCallHistory h WHERE " +
           "(:endpoint IS NULL OR h.endpoint = :endpoint) AND " +
           "(:startDate IS NULL OR h.timestamp >= :startDate) AND " +
           "(:endDate IS NULL OR h.timestamp <= :endDate) " +
           "ORDER BY h.timestamp DESC")
    Page<ApiCallHistory> findByFilters(
            @Param("endpoint") String endpoint,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
