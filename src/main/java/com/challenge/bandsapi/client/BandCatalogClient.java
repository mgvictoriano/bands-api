package com.challenge.bandsapi.client;

import com.challenge.bandsapi.dto.ExternalBandResponse;
import com.challenge.bandsapi.exception.ExternalServiceException;
import com.challenge.bandsapi.mapper.BandCatalogMapper;
import com.challenge.bandsapi.model.Band;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BandCatalogClient {

    private static final String BANDS_ENDPOINT = "/api/bands";

    private final RestClient bandCatalogRestClient;
    private final BandCatalogMapper mapper;

    public List<Band> fetchAll() {
        log.info("Fetching all bands from external catalog");
        try {
            ExternalBandResponse[] response = bandCatalogRestClient.get()
                    .uri(BANDS_ENDPOINT)
                    .retrieve()
                    .body(ExternalBandResponse[].class);

            if (response == null) {
                log.warn("External catalog returned null response");
                return List.of();
            }

            List<Band> bands = Arrays.stream(response)
                    .map(mapper::toDomain)
                    .toList();

            log.info("Fetched {} bands from external catalog", bands.size());
            return bands;
        } catch (HttpClientErrorException.NotFound ex) {
            log.warn("Bands endpoint not found: {}", ex.getMessage());
            return List.of();
        } catch (RestClientException ex) {
            log.error("Failed to fetch bands from external catalog", ex);
            throw new ExternalServiceException("Band catalog client request failed", ex);
        }
    }
}
