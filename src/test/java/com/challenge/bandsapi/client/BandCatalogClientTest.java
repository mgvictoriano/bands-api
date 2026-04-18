package com.challenge.bandsapi.client;

import com.challenge.bandsapi.dto.ExternalBandResponse;
import com.challenge.bandsapi.exception.ExternalServiceException;
import com.challenge.bandsapi.mapper.BandCatalogMapper;
import com.challenge.bandsapi.model.Band;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class BandCatalogClientTest {

    private RestClient restClientMock;

    @Mock
    private BandCatalogMapper mapperMock;

    private BandCatalogClient client;

    @BeforeEach
    void setUp() {
        restClientMock = Mockito.mock(RestClient.class, Answers.RETURNS_DEEP_STUBS);
        client = new BandCatalogClient(restClientMock, mapperMock);
    }

    @Nested
    class Given_external_api_returns_a_band_list {

        private Band expectedBand;

        @BeforeEach
        void setup() {
            ExternalBandResponse dto = new ExternalBandResponse("id-1", "Nirvana", "img", "rock", "bio", 10L, List.of("a1"));
            expectedBand = new Band("id-1", "Nirvana", "img", "rock", "bio", 10L, List.of("a1"));

            when(restClientMock.get().uri(eq("/api/bands")).retrieve().body(ExternalBandResponse[].class))
                    .thenReturn(new ExternalBandResponse[]{dto});
            when(mapperMock.toDomain(dto)).thenReturn(expectedBand);
        }

        @Nested
        class When_fetch_all_is_called {

            @Test
            void Then_should_return_the_mapped_bands() {
                List<Band> result = client.fetchAll();

                assertThat(result).hasSize(1);
                assertThat(result.getFirst().name()).isEqualTo("Nirvana");
                assertThat(result.getFirst().id()).isEqualTo("id-1");
            }
        }
    }

    @Nested
    class Given_external_api_returns_null {

        @BeforeEach
        void setup() {
            when(restClientMock.get().uri(eq("/api/bands")).retrieve().body(ExternalBandResponse[].class))
                    .thenReturn(null);
        }

        @Nested
        class When_fetch_all_is_called {

            @Test
            void Then_should_return_empty_list() {
                List<Band> result = client.fetchAll();

                assertThat(result).isEmpty();
            }
        }
    }

    @Nested
    class Given_external_api_throws_a_rest_client_exception {

        @BeforeEach
        void setup() {
            when(restClientMock.get().uri(eq("/api/bands")).retrieve().body(ExternalBandResponse[].class))
                    .thenThrow(new RestClientException("timeout"));
        }

        @Nested
        class When_fetch_all_is_called {

            @Test
            void Then_should_throw_external_service_exception() {
                assertThatThrownBy(client::fetchAll)
                        .isInstanceOf(ExternalServiceException.class)
                        .hasMessageContaining("Band catalog client request failed");
            }
        }
    }
}
