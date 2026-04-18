package com.mgvictoriano.bandsapi.presentation.controller;

import com.mgvictoriano.bandsapi.application.service.BandService;
import com.mgvictoriano.bandsapi.presentation.dto.BandResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/bands")
public class BandController {

    private final BandService bandService;

    public BandController(BandService bandService) {
        this.bandService = bandService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BandResponse> getBandById(@PathVariable Long id) {
        return ResponseEntity.ok(BandResponse.from(bandService.getBandById(id)));
    }
}
