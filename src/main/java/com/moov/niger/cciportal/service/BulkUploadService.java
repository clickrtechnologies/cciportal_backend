package com.moov.niger.cciportal.service;
import com.moov.niger.cciportal.model.SongContent;
import com.moov.niger.cciportal.repository.SongContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.moov.niger.cciportal.dto.response.*;
import com.moov.niger.cciportal.dto.*;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.util.*;

@Service
public class BulkUploadService {

    @Autowired
    private SongContentRepository songContentRepository;

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


    public BulkPreviewResponse previewFile(MultipartFile file)
            throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new RuntimeException("Invalid file");
        }
        if (fileName.endsWith(".csv")) {
            return processCsv(file);
        }
        if (fileName.endsWith(".xlsx")
                || fileName.endsWith(".xls")) {
            return processExcel(file);
        }
        throw new RuntimeException("Unsupported file type");
    }

    private BulkPreviewResponse processCsv(MultipartFile file)
            throws Exception {

        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(file.getInputStream()));
        BulkPreviewResponse response =
                new BulkPreviewResponse();
        List<BulkPreviewRecord> list =
                new ArrayList<>();

        String line;

        boolean skipHeader = true;

        while ((line = reader.readLine()) != null) {

            if (skipHeader) {
                skipHeader = false;
                continue;
            }

            String[] row = line.split(",");

            BulkPreviewRecord record =
                    validateRecord(
                            row[0].trim(),
                            row[1].trim(),
                            row[2].trim(),
                            row[3].trim(),
                            row[4].trim());

            list.add(record);

        }
        prepareSummary(response, list);
        return response;

    }

    private BulkPreviewResponse processExcel(MultipartFile file)
            throws Exception {

        Workbook workbook =
                WorkbookFactory.create(file.getInputStream());
        Sheet sheet =
                workbook.getSheetAt(0);
        List<BulkPreviewRecord> list =
                new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null)
                continue;
            BulkPreviewRecord record =
                    validateRecord(
                            getValue(row.getCell(0)),
                            getValue(row.getCell(1)),
                            getValue(row.getCell(2)),
                            getValue(row.getCell(3)),
                            getValue(row.getCell(4))

                    );
            list.add(record);

        }
        workbook.close();
        BulkPreviewResponse response =
                new BulkPreviewResponse();
        prepareSummary(response, list);
        return response;
    }


    private BulkPreviewRecord validateRecord(

            String mobile,
            String toneId,
            String toneName,
            String packagePlan,
            String artistName) {

        BulkPreviewRecord record =
                new BulkPreviewRecord();

        record.setMobile(mobile);
        record.setToneId(toneId);
        record.setToneName(toneName);
        record.setPackagePlan(packagePlan);
        record.setArtistName(artistName);

        List<String> errors =
                new ArrayList<>();


        if (!mobile.matches("\\d{8}")) {
            errors.add("Invalid Mobile Number");
        }

        if (!(packagePlan.equalsIgnoreCase("Daily")
                || packagePlan.equalsIgnoreCase("Weekly")
                || packagePlan.equalsIgnoreCase("Monthly"))) {

            errors.add("Invalid Package Plan");

        }
        Optional<SongContent> tone =
                songContentRepository.findByToneCode(toneId);

        if (tone.isEmpty()) {
            errors.add("Tone ID not found");

        } else {
            SongContent dbTone = tone.get();
            if (!dbTone.getToneName()
                    .equalsIgnoreCase(toneName)) {
                errors.add("Tone Name mismatch");
            }

            if (!dbTone.getArtistName()
                    .equalsIgnoreCase(artistName)) {
                errors.add("Artist Name mismatch");
            }
        }

        if (errors.isEmpty()) {
            record.setValid(true);
            record.setMessage("Valid");

        } else {
            record.setValid(false);
            record.setMessage(
                    String.join(", ", errors));

        }
        return record;

    }

    private void prepareSummary(
            BulkPreviewResponse response,
            List<BulkPreviewRecord> records) {

        response.setRecords(records);
        response.setTotalRecords(records.size());
        response.setValidRecords(

                (int) records.stream()
                        .filter(BulkPreviewRecord::isValid)
                        .count()

        );
        response.setInvalidRecords(
                (int) records.stream()
                        .filter(r -> !r.isValid())
                        .count()

        );
    }
    private final DataFormatter formatter = new DataFormatter();

    private String getValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        return formatter.formatCellValue(cell).trim();
    }
}