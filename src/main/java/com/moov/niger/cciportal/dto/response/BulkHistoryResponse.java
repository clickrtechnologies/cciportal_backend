package com.moov.niger.cciportal.dto.response;

import lombok.Data;

@Data
public class BulkHistoryResponse {

    private String transactionDate;
    private String previewId;
    private String fileName;
    private Integer totalRecords;
    private Integer successRecords;
    private Integer failedRecords;
    private Integer status;

}