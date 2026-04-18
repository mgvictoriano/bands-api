package com.challenge.bandsapi.dto;

import java.util.List;

public record ExternalBandResponse(
        String id,
        String name,
        String image,
        String genre,
        String biography,
        Long numPlays,
        List<String> albums
) {
}

