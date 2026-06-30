package com.moov.niger.cciportal.repository;

import com.moov.niger.cciportal.model.BulkHistory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BulkHistoryRepository extends JpaRepository<BulkHistory,Long> {

    Page<BulkHistory> findByPreviewIdContainingIgnoreCase(
            String previewId,
            Pageable pageable);

}