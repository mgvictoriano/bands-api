package com.challenge.bandsapi.service;

import com.challenge.bandsapi.client.BandCatalogClient;
import com.challenge.bandsapi.model.BandCatalog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Loads the bands catalog from the external source and caches the
 * resulting {@link BandCatalog}. Kept as a separate Spring bean so
 * that {@link org.springframework.cache.annotation.Cacheable} is
 * applied through the proxy (avoiding self-invocation pitfalls).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BandCatalogLoader {

    private final BandCatalogClient client;

    @Cacheable(cacheNames = "bandsCatalog")
    public BandCatalog load() {
        log.debug("Cache miss - loading bands catalog from client");
        return new BandCatalog(client.fetchAll());
    }
}
