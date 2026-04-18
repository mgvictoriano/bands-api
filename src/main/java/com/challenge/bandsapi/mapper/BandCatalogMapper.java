package com.challenge.bandsapi.mapper;

import com.challenge.bandsapi.dto.ExternalBandResponse;
import com.challenge.bandsapi.model.Band;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BandCatalogMapper {

    public Band toDomain(ExternalBandResponse dto) {
        if (dto.numPlays() == null) {
            log.warn("Upstream band '{}' (id={}) has null numPlays; defaulting to 0", dto.name(), dto.id());
        }
        if (dto.albums() == null) {
            log.warn("Upstream band '{}' (id={}) has null albums; defaulting to empty list", dto.name(), dto.id());
        }
        return new Band(
                dto.id(),
                dto.name(),
                dto.image(),
                dto.genre(),
                dto.biography(),
                Objects.requireNonNullElse(dto.numPlays(), 0L),
                Objects.requireNonNullElse(dto.albums(), List.of())
        );
    }
}

