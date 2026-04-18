package com.challenge.bandsapi.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "cache.bands")
public record BandsCacheProperties(
        @NotNull Duration ttl,
        @Min(1) long maximumSize
) {
    public BandsCacheProperties {
        if (ttl == null) ttl = Duration.ofMinutes(10);
    }
}