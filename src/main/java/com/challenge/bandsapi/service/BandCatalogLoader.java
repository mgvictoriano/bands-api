package com.challenge.bandsapi.service;

import com.challenge.bandsapi.client.BandCatalogClient;
import com.challenge.bandsapi.model.BandCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * Loads the bands catalog from the external source and caches the
 * resulting {@link BandCatalog}. Kept as a separate Spring bean so
 * that {@link org.springframework.cache.annotation.Cacheable} is
 * applied through the proxy (avoiding self-invocation pitfalls).
 */
@Component
public class BandCatalogLoader {

    private static final Logger log = LoggerFactory.getLogger(BandCatalogLoader.class);

    private final BandCatalogClient client;

    public BandCatalogLoader(BandCatalogClient client) {
        this.client = client;
    }

    @Cacheable(cacheNames = "bandsCatalog")
    public BandCatalog load() {
        log.debug("Cache miss - loading bands catalog from client");
        return new BandCatalog(client.fetchAll());
    }
}
