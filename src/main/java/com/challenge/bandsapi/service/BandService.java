package com.challenge.bandsapi.service;

import com.challenge.bandsapi.model.Band;

import java.util.List;

public interface BandService {
    List<Band> getAll();

    Band getById(String id);

    Band getByName(String name);

    List<Band> searchByName(String name);
}

