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

    private String previewId;

    private String fileName;

    private Integer totalRecords;

    private Integer successRecords;

    private Integer failedRecords;

    private Byte status;

    private LocalDateTime transactionDate;

}