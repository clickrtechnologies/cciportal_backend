package com.moov.niger.cciportal.controller;

import com.moov.niger.cciportal.dto.request.BulkProcessRequest;
import com.moov.niger.cciportal.dto.request.SetRbtRequest;
import com.moov.niger.cciportal.dto.response.*;
import com.moov.niger.cciportal.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/cci")
@RequiredArgsConstructor
public class ApiController {

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private SongContentService songContentService;

    @Autowired
    private BulkUploadService bulkUploadService;

    //user status details
    @GetMapping("/{msisdn}")
    public ResponseEntity<SubscriptionDetailsResponse>
    getSubscription(@PathVariable Long msisdn) {

        return ResponseEntity.ok(
                subscriptionService.getDetails(msisdn));
    }

    //browse catalog to set rbt
    @GetMapping("/catalog")
    public ResponseEntity<List<SongResponse>> getSongs() {
        return ResponseEntity.ok(songContentService.getAllSongs());
    }

    //activate rbt
    @PostMapping("/activate")
    public ResponseEntity<SetRbtResponse> activate(
            @RequestBody SetRbtRequest request) {

        return ResponseEntity.ok(
                subscriptionService.activate(request));
    }

    //deactivate rbt
    @PostMapping("/deactivate")
    public ResponseEntity<DeactivateResponse> deactivate(
            @RequestParam Long msisdn) {

        return ResponseEntity.ok(
                subscriptionService.deactivate(msisdn));
    }

    //list of categories
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategoryList() {
        return ResponseEntity.ok(songContentService.getCategoryList());
    }

    //list of artist
    @GetMapping("/artist")
    public ResponseEntity<List<String>> getArtistList() {
        return ResponseEntity.ok(songContentService.getArtistList());
    }

    //search-tones
    @GetMapping("/catalog/search")
    public ResponseEntity<Map<String, Object>> searchCatalog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String artist,
            @RequestParam(defaultValue = "default") String sort) {

        return ResponseEntity.ok(
                songContentService.searchCatalog(page, size, search, category, artist, sort)
        );
    }

    //template for bulk activity
    @GetMapping("/template/download")
    public ResponseEntity<Resource> downloadTemplate() {
        return bulkUploadService.downloadTemplate();
    }


    //bulk preview
    @PostMapping("/bulk/preview")
    public ResponseEntity<BulkPreviewResponse> previewFile(
            @RequestParam("file") MultipartFile file)
            throws Exception {

        return ResponseEntity.ok(
                bulkUploadService.previewFile(file));

    }

    //bulk process for activation
    @PostMapping("/bulk/process")
    public ResponseEntity<?> process(
            @RequestBody BulkProcessRequest request){
        return ResponseEntity.ok(
                bulkUploadService.processBulk(request));

    }

    //history of bulk upload
    @GetMapping("/bulk/history")
    public ResponseEntity<Map<String, Object>> history(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String previewId) {

        return ResponseEntity.ok(
                bulkUploadService.getHistory(page, size, previewId));
    }

    //details of bulk by preview id
    @GetMapping("/bulk/report/{previewId}")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable String previewId) throws Exception {

        return bulkUploadService.downloadReport(previewId);
    }

    //report generation
    @GetMapping("/bulk/history/export")
    public ResponseEntity<Resource> exportHistory() throws Exception {
        return bulkUploadService.exportHistory();
    }
}
