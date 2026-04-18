package com.mgvictoriano.bandsapi.presentation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;
import com.mgvictoriano.bandsapi.application.service.BandService;
import com.mgvictoriano.bandsapi.domain.model.Band;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BandController.class)
class BandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BandService bandService;

    @Test
    void shouldReturnBandById() throws Exception {
        when(bandService.getBandById(1L)).thenReturn(new Band(1L, "Nirvana", "Grunge"));

        mockMvc.perform(get("/api/v1/bands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Nirvana"))
                .andExpect(jsonPath("$.genre").value("Grunge"));
    }

    @Test
    void shouldReturnNotFoundWhenBandDoesNotExist() throws Exception {
        when(bandService.getBandById(99L)).thenThrow(new NoSuchElementException("Band not found with id: 99"));

        mockMvc.perform(get("/api/v1/bands/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Band not found with id: 99"));
    }
}
