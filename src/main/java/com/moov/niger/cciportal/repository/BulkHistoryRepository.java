package com.moov.niger.cciportal.repository;

import com.moov.niger.cciportal.model.BulkHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulkHistoryRepository extends JpaRepository<BulkHistory,Long> {
}