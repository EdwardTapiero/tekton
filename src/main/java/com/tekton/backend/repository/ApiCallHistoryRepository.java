package com.tekton.backend.repository;

import com.tekton.backend.entity.ApiCallHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para el historial de llamadas a la API.
 */
@Repository
public interface ApiCallHistoryRepository extends JpaRepository<ApiCallHistory, Long>, JpaSpecificationExecutor<ApiCallHistory> {
}
