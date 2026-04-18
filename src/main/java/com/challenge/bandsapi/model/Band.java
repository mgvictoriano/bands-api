package com.challenge.bandsapi.model;

import java.util.List;

public record Band(
        String id,
        String name,
        String image,
        String genre,
        String biography,
        long numPlays,
        List<String> albums
) {
}

