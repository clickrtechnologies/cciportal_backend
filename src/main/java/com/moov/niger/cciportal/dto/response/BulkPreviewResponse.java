package com.moov.niger.cciportal.dto.response;
import com.moov.niger.cciportal.dto.BulkPreviewRecord;
import lombok.Data;

import java.util.List;

@Data
public class BulkPreviewResponse {

    private int totalRecords;
    private int validRecords;
    private int invalidRecords;

    private List<BulkPreviewRecord> records;
}
