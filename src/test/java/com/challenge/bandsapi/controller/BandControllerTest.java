package com.challenge.bandsapi.controller;

import com.challenge.bandsapi.mapper.BandResponseMapper;
import com.challenge.bandsapi.model.Band;
import com.challenge.bandsapi.service.BandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(BandController.class)
@Import(BandResponseMapper.class)
class BandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BandService bandServiceMock;

    @Nested
    class Given_multiple_bands_exist_in_the_catalog {

        @BeforeEach
        void setup() {
            when(bandServiceMock.getAll()).thenReturn(List.of(
                    band("1", "Nirvana", "Grunge", 99L),
                    band("2", "Pearl Jam", "Rock", 88L)
            ));
        }

        @Nested
        class When_get_all_bands_endpoint_is_called {

            @Test
            void Then_should_return_all_bands_with_200_ok() throws Exception {
                mockMvc.perform(get("/api/v1/bands"))
                        .andExpect(status().isOk())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$[0].name").value("Nirvana"))
                        .andExpect(jsonPath("$[1].name").value("Pearl Jam"));
            }
        }
    }

    @Nested
    class Given_a_band_named_Nirvana_exists {

        @BeforeEach
        void setup() {
            when(bandServiceMock.getByName("Nirvana"))
                    .thenReturn(band("1", "Nirvana", "Grunge", 99L));
        }

        @Nested
        class When_get_band_by_name_endpoint_is_called {

            @Test
            void Then_should_return_the_band_details_with_200_ok() throws Exception {
                mockMvc.perform(get("/api/v1/bands/Nirvana"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value("1"))
                        .andExpect(jsonPath("$.name").value("Nirvana"))
                        .andExpect(jsonPath("$.genre").value("Grunge"))
                        .andExpect(jsonPath("$.numPlays").value(99));
            }
        }
    }

    @Nested
    class Given_a_band_with_id_abc_1_exists {

        @BeforeEach
        void setup() {
            when(bandServiceMock.getById("abc-1"))
                    .thenReturn(band("abc-1", "U2", "Rock", 10L));
        }

        @Nested
        class When_get_band_by_id_endpoint_is_called {

            @Test
            void Then_should_return_the_band_details_with_200_ok() throws Exception {
                mockMvc.perform(get("/api/v1/bands/id/abc-1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value("abc-1"))
                        .andExpect(jsonPath("$.name").value("U2"));
            }
        }
    }

    @Nested
    class Given_bands_matching_the_search_term_nir_exist {

        @BeforeEach
        void setup() {
            when(bandServiceMock.searchByName("nir")).thenReturn(List.of(
                    band("1", "Nirvana", "Grunge", 99L)
            ));
        }

        @Nested
        class When_search_bands_by_name_endpoint_is_called {

            @Test
            void Then_should_return_matching_bands_with_200_ok() throws Exception {
                mockMvc.perform(get("/api/v1/bands/search").queryParam("name", "nir"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].name").value("Nirvana"));
            }
        }
    }

    private Band band(String id, String name, String genre, long numPlays) {
        return new Band(id, name, "img", genre, "bio", numPlays, List.of());
    }
}
