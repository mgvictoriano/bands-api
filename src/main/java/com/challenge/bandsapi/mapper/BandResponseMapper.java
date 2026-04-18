package com.challenge.bandsapi.mapper;

import com.challenge.bandsapi.dto.BandResponse;
import com.challenge.bandsapi.model.Band;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BandResponseMapper {

    public BandResponse toResponse(Band band) {
        return new BandResponse(
                band.id(),
                band.name(),
                band.image(),
                band.genre(),
                band.biography(),
                band.numPlays(),
                band.albums()
        );
    }
}
