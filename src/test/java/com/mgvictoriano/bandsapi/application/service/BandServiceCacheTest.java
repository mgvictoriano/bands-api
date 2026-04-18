package com.mgvictoriano.bandsapi.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mgvictoriano.bandsapi.BandsApiApplication;
import com.mgvictoriano.bandsapi.domain.model.Band;
import com.mgvictoriano.bandsapi.domain.port.out.BandClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = BandsApiApplication.class)
class BandServiceCacheTest {

    @Autowired
    private BandService bandService;

    @MockBean
    private BandClient bandClient;

    @Test
    void shouldCacheBandLookupById() {
        Band expected = new Band(1L, "Nirvana", "Grunge");
        when(bandClient.findById(1L)).thenReturn(expected);

        Band firstCall = bandService.getBandById(1L);
        Band secondCall = bandService.getBandById(1L);

        assertThat(firstCall).isEqualTo(expected);
        assertThat(secondCall).isEqualTo(expected);
        verify(bandClient, times(1)).findById(1L);
    }
}
