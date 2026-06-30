package com.moov.niger.cciportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_bulk_history")
@Data
public class BulkHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preview_id")
    private String previewId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "total_records")
    private Integer totalRecords;

    @Column(name = "success_records")
    private Integer successRecords;

    @Column(name = "failed_records")
    private Integer failedRecords;

    @Column(name = "status")
    private Byte status;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
}