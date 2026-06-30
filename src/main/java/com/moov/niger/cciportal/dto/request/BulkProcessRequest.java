package com.moov.niger.cciportal.dto.request;

import com.moov.niger.cciportal.dto.BulkPreviewRecord;
import lombok.Data;
import java.util.List;

@Data
public class BulkProcessRequest {

    private String previewId;

    private String fileName;

    private List<BulkPreviewRecord> records;

}