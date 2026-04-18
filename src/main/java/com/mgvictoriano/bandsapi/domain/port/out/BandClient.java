package com.mgvictoriano.bandsapi.domain.port.out;

import com.mgvictoriano.bandsapi.domain.model.Band;

public interface BandClient {

    Band findById(Long id);
}
