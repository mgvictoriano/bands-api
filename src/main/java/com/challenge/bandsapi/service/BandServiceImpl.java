package com.challenge.bandsapi.service;

import com.challenge.bandsapi.client.BandCatalogClient;
import com.challenge.bandsapi.exception.BandNotFoundException;
import com.challenge.bandsapi.model.Band;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class BandServiceImpl implements BandService {

    private final BandCatalogClient client;

    @Override
    @Cacheable(cacheNames = "bandsCatalog")
    public List<Band> getAll() {
        log.debug("Cache miss — loading bands catalog from client");
        return client.fetchAll();
    }

    @Override
    public Band getById(String id) {
        requireNonBlank(id, "Band id must not be blank");
        log.debug("Looking up band by id: {}", id);
        return getAll().stream()
                .filter(band -> id.trim().equalsIgnoreCase(band.id()))
                .findFirst()
                .orElseThrow(() -> new BandNotFoundException(id.trim()));
    }

    @Override
    public Band getByName(String name) {
        requireNonBlank(name, "Band name must not be blank");
        log.debug("Looking up band by name: {}", name);
        return getAll().stream()
                .filter(band -> name.trim().equalsIgnoreCase(band.name()))
                .findFirst()
                .orElseThrow(() -> new BandNotFoundException(name.trim()));
    }

    @Override
    public List<Band> searchByName(String name) {
        requireNonBlank(name, "Search term must not be blank");
        String term = name.trim().toLowerCase(Locale.ROOT);
        log.debug("Searching bands by name term: {}", term);
        return getAll().stream()
                .filter(band -> band.name() != null && band.name().toLowerCase(Locale.ROOT).contains(term))
                .toList();
    }

    private void requireNonBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
