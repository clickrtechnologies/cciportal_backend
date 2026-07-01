package com.moov.niger.cciportal.service;
import com.moov.niger.cciportal.dto.request.BulkProcessRequest;
import com.moov.niger.cciportal.dto.request.SetRbtRequest;
import com.moov.niger.cciportal.model.BulkHistory;
import com.moov.niger.cciportal.model.BulkHistoryDetail;
import com.moov.niger.cciportal.model.SongContent;
import com.moov.niger.cciportal.model.Subscription;
import com.moov.niger.cciportal.repository.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.moov.niger.cciportal.dto.response.*;
import com.moov.niger.cciportal.dto.*;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.domain.*;
@Service
public class BulkUploadService {

    @Autowired
    private SongContentRepository songContentRepository;

    @Autowired
    private BulkHistoryRepository bulkHistoryRepository;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private BulkHistoryDetailRepository bulkHistoryDetailRepository;


    //template download format

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



    //validation of bulk upload
    private String generatePreviewId() {

        Random random = new Random();
        int number = 1000 + random.nextInt(9000);
        return "ID-" + number;
    }

    public BulkPreviewResponse previewFile(MultipartFile file)
            throws Exception {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Please upload a file.");
        }
        long maxSize = 10 * 1024 * 1024; //10MB

        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size must not exceed 10 MB.");
        }
        String fileName = file.getOriginalFilename();

        if (fileName == null) {
            throw new RuntimeException("Invalid file");
        }

        BulkPreviewResponse response;

        if (fileName.endsWith(".csv")) {
            response = processCsv(file);
        } else if (fileName.endsWith(".xlsx")
                || fileName.endsWith(".xls")) {
            response = processExcel(file);
        } else {
            throw new RuntimeException("Unsupported file type");
        }
        response.setPreviewId(generatePreviewId());
        return response;
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



    //bulk upload for activation
    public String processBulk(BulkProcessRequest request) {

        int success = 0;
        int failed = 0;

        for (BulkPreviewRecord record : request.getRecords()) {

            BulkHistoryDetail detail = new BulkHistoryDetail();

            detail.setPreviewId(request.getPreviewId());
            detail.setMobile(Long.parseLong(record.getMobile()));
            detail.setToneId(record.getToneId());
            detail.setToneName(record.getToneName());
            detail.setArtistName(record.getArtistName());
            detail.setPackagePlan(record.getPackagePlan());
            detail.setTransactionDate(LocalDateTime.now());

            // Skip invalid preview records
            if (!record.isValid()) {

                detail.setStatus((byte) 0);
                detail.setMessage(record.getMessage());

                bulkHistoryDetailRepository.save(detail);

                failed++;
                continue;
            }

            try {

                SetRbtRequest activate = new SetRbtRequest();

                activate.setMsisdn(Long.parseLong(record.getMobile()));
                activate.setToneCode(record.getToneId());
                activate.setToneName(record.getToneName());
                activate.setPackName(convertPack(record.getPackagePlan()));

                subscriptionService.activate(activate);

                // Fetch latest subscription status
                Subscription subscription = subscriptionRepository
                        .findByMsisdn(Long.parseLong(record.getMobile()))
                        .orElse(null);

                if (subscription != null) {

                    byte status = subscription.getStatus();

                    detail.setStatus(status);

                    if (status == 1) {
                        detail.setMessage("Active");
                        success++;
                    } else if (status == 15) {
                        detail.setMessage("Retry");
                        failed++;
                    } else {
                        detail.setMessage("Inactive");
                        failed++;
                    }

                } else {

                    detail.setStatus((byte) 0);
                    detail.setMessage("Subscription not found");
                    failed++;
                }

            } catch (Exception e) {

                detail.setStatus((byte) 0);
                detail.setMessage(e.getMessage());

                failed++;
            }

            bulkHistoryDetailRepository.save(detail);
        }

        BulkHistory history = new BulkHistory();

        history.setPreviewId(request.getPreviewId());
        history.setFileName(request.getFileName());
        history.setTransactionDate(LocalDateTime.now());
        history.setTotalRecords(request.getRecords().size());
        history.setSuccessRecords(success);
        history.setFailedRecords(failed);

        if (success == request.getRecords().size()) {
            history.setStatus((byte) 1);
        } else if (success > 0) {
            history.setStatus((byte) 15);
        } else {
            history.setStatus((byte) 0);
        }

        bulkHistoryRepository.save(history);

        return "Bulk Activation Completed";
    }
    private String convertPack(String pack){
        if(pack.equalsIgnoreCase("Daily")){
            return "TSUBD";
        }
        if(pack.equalsIgnoreCase("Weekly")){
            return "TSUBW";
        }
        return "TSUBM";
    }


    //history of bulk uploads
    public Map<String, Object> getHistory(int page,
                                          int size,
                                          String previewId) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "transactionDate"));

        Page<BulkHistory> historyPage;

        if (previewId == null || previewId.trim().isEmpty()) {
            historyPage = bulkHistoryRepository.findAll(pageable);
        } else {
            historyPage = bulkHistoryRepository
                    .findByPreviewIdContainingIgnoreCase(previewId, pageable);
        }

        List<BulkHistoryResponse> response = new ArrayList<>();

        for (BulkHistory item : historyPage.getContent()) {

            BulkHistoryResponse dto = new BulkHistoryResponse();

            dto.setPreviewId(item.getPreviewId());
            dto.setFileName(item.getFileName());
            dto.setTotalRecords(item.getTotalRecords());
            dto.setSuccessRecords(item.getSuccessRecords());
            dto.setFailedRecords(item.getFailedRecords());
            dto.setStatus(item.getStatus());
            dto.setTransactionDate(item.getTransactionDate().toString());

            response.add(dto);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", response);
        result.put("page", page);
        result.put("size", size);
        result.put("totalElements", historyPage.getTotalElements());
        result.put("totalPages", historyPage.getTotalPages());

        return result;
    }

    //details of bulk by preview
    public ResponseEntity<Resource> downloadReport(String previewId)
            throws Exception {

        List<BulkHistoryDetail> records =
                bulkHistoryDetailRepository.findByPreviewId(previewId);

        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Bulk Report");

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Mobile Number");
        header.createCell(1).setCellValue("Tone ID");
        header.createCell(2).setCellValue("Tone Name");
        header.createCell(3).setCellValue("Artist Name");
        header.createCell(4).setCellValue("Package Plan");
        header.createCell(5).setCellValue("Status");
        header.createCell(6).setCellValue("Message");

        int rowNo = 1;

        for (BulkHistoryDetail item : records) {

            Row row = sheet.createRow(rowNo++);

            row.createCell(0).setCellValue(item.getMobile());

            row.createCell(1).setCellValue(item.getToneId());

            row.createCell(2).setCellValue(item.getToneName());

            row.createCell(3).setCellValue(item.getArtistName());

            row.createCell(4).setCellValue(item.getPackagePlan());
                    String status;
            switch (item.getStatus()) {
                case 1:
                    status = "SUCCESS";
                    break;
                case 15:
                    status = "RETRY";
                    break;
                default:
                    status = "FAILED";
            }

            row.createCell(5).setCellValue(status);
            row.createCell(6).setCellValue(item.getMessage());

        }

        for (int i = 0; i <= 6; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out =
                new ByteArrayOutputStream();

        workbook.write(out);

        workbook.close();

        ByteArrayResource resource =
                new ByteArrayResource(out.toByteArray());

        return ResponseEntity.ok()

                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" +
                                previewId + "_Report.xlsx")

                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(resource.contentLength())
                .body(resource);
    }
}