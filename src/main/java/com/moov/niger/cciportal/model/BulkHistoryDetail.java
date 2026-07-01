package com.moov.niger.cciportal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_bulk_history_detail")
@Data
public class BulkHistoryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "preview_id", nullable = false)
    private String previewId;

    @Column(name = "mobile", nullable = false)
    private Long mobile;

    @Column(name = "tone_id")
    private String toneId;

    @Column(name = "tone_name")
    private String toneName;

    @Column(name = "artist_name")
    private String artistName;

    @Column(name = "package_plan")
    private String packagePlan;

    @Column(name = "status")
    private Byte status;

    @Column(name = "message")
    private String message;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
}
