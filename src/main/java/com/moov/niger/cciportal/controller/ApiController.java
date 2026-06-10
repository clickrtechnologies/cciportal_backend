package com.moov.niger.cciportal.controller;

import com.moov.niger.cciportal.dto.*;
import com.moov.niger.cciportal.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

}
