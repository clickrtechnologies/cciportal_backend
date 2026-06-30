package com.moov.niger.cciportal.service;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class BulkUploadService {

    public ResponseEntity<Resource> downloadTemplate() {

        StringBuilder builder = new StringBuilder();

        builder.append("Mobile Number,");
        builder.append("Tone ID,");
        builder.append("Tone Name,");
        builder.append("Package Plan,");
        builder.append("Artist Name");
        builder.append("\n");

        ByteArrayResource resource =
                new ByteArrayResource(builder.toString().getBytes());

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=Bulk_Activity_Template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}