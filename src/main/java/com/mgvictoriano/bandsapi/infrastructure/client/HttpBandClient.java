package com.mgvictoriano.bandsapi.infrastructure.client;

import com.mgvictoriano.bandsapi.domain.model.Band;
import com.mgvictoriano.bandsapi.domain.port.out.BandClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HttpBandClient implements BandClient {

    private final RestClient restClient;

    public HttpBandClient(RestClient bandRestClient) {
        this.restClient = bandRestClient;
    }

    @Override
    public Band findById(Long id) {
        Band band = restClient.get()
                .uri("/bands/{id}", id)
                .retrieve()
                .body(Band.class);

        if (band == null) {
            throw new IllegalStateException("Band not found for id " + id);
        }

        return band;
    }
}
