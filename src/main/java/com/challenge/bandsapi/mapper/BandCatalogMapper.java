package com.challenge.bandsapi.mapper;

import com.challenge.bandsapi.dto.ExternalBandResponse;
import com.challenge.bandsapi.model.Band;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BandCatalogMapper {

    public Band toDomain(ExternalBandResponse dto) {
        return new Band(
                dto.id(),
                dto.name(),
                dto.image(),
                dto.genre(),
                dto.biography(),
                dto.numPlays() != null ? dto.numPlays() : 0L,
                dto.albums() != null ? dto.albums() : List.of()
        );
    }
}

