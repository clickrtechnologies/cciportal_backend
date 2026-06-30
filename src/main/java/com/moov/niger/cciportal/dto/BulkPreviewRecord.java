package com.moov.niger.cciportal.dto;

import lombok.Data;

@Data
public class BulkPreviewRecord {

    private String mobile;
    private String toneId;
    private String toneName;
    private String artistName;
    private String packagePlan;

    private boolean valid;

    private String message;
}
