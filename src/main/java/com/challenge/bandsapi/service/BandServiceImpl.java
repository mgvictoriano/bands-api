package com.challenge.bandsapi.service;

import com.challenge.bandsapi.exception.BandNotFoundException;
import com.challenge.bandsapi.model.Band;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BandServiceImpl implements BandService {

    private static final Logger log = LoggerFactory.getLogger(BandServiceImpl.class);

    private final BandCatalogLoader catalogLoader;

    public BandServiceImpl(BandCatalogLoader catalogLoader) {
        this.catalogLoader = catalogLoader;
    }

    @Override
    public List<Band> getAll() {
        return catalogLoader.load().all();
    }

    @Override
    public Band getById(String id) {
        log.debug("Looking up band by id: {}", id);
        return catalogLoader.load().findById(id)
                .orElseThrow(() -> new BandNotFoundException(id.trim()));
    }

    @Override
    public Band getByName(String name) {
        log.debug("Looking up band by name: {}", name);
        return catalogLoader.load().findByName(name)
                .orElseThrow(() -> new BandNotFoundException(name.trim()));
    }

    @Override
    public List<Band> searchByName(String name) {
        log.debug("Searching bands by name term: {}", name);
        return catalogLoader.load().search(name);
    }
}
