package com.challenge.bandsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Band information returned by the API")
public record BandResponse(
        @Schema(description = "Unique band identifier (UUID)", example = "bc710bcf-8815-42cf-bad2-3f1d12246aeb")
        String id,

        @Schema(description = "Band name", example = "Nickelback")
        String name,

        @Schema(description = "URL of the band cover image")
        String image,

        @Schema(description = "Music genre", example = "rock")
        String genre,

        @Schema(description = "Short biography of the band")
        String biography,

        @Schema(description = "Total number of plays on Last.fm", example = "17605491")
        long numPlays,

        @Schema(description = "List of album UUIDs associated with the band")
        List<String> albums
) {
}
