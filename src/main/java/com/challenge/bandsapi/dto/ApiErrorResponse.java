package com.challenge.bandsapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Standard error response body")
public record ApiErrorResponse(
        @Schema(description = "UTC timestamp of the error") Instant timestamp,
        @Schema(description = "HTTP status code", example = "404") int status,
        @Schema(description = "HTTP status description", example = "Not Found") String error,
        @Schema(description = "Human-readable error message", example = "Band not found: Nirvana") String message,
        @Schema(description = "Request path that caused the error", example = "/api/v1/bands/Nirvana") String path
) {
}
