package com.mgvictoriano.bandsapi.application.service;

import com.mgvictoriano.bandsapi.domain.model.Band;
import com.mgvictoriano.bandsapi.domain.port.out.BandClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BandService {

    private final BandClient bandClient;

    public BandService(BandClient bandClient) {
        this.bandClient = bandClient;
    }

    @Cacheable(cacheNames = "bands", key = "#id")
    public Band getBandById(Long id) {
        return bandClient.findById(id);
    }
}
