package com.moov.niger.cciportal.repository;

import com.moov.niger.cciportal.model.BulkHistoryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BulkHistoryDetailRepository extends JpaRepository<BulkHistoryDetail, Long> {

    List<BulkHistoryDetail> findByPreviewId(String previewId);

}