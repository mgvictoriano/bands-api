package com.challenge.bandsapi.service;

import com.challenge.bandsapi.exception.BandNotFoundException;
import com.challenge.bandsapi.model.Band;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BandServiceImpl implements BandService {

    private final BandCatalogLoader catalogLoader;

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
