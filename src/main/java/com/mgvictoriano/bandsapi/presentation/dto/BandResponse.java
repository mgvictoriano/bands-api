package com.mgvictoriano.bandsapi.presentation.dto;

import com.mgvictoriano.bandsapi.domain.model.Band;

public record BandResponse(Long id, String name, String genre) {

    public static BandResponse from(Band band) {
        return new BandResponse(band.id(), band.name(), band.genre());
    }
}
