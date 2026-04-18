package com.challenge.bandsapi.service;

import com.challenge.bandsapi.client.BandCatalogClient;
import com.challenge.bandsapi.exception.BandNotFoundException;
import com.challenge.bandsapi.model.Band;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class BandServiceImplTest {

    @Mock
    private BandCatalogClient clientMock;

    @InjectMocks
    private BandServiceImpl service;

    @Nested
    class Given_a_catalog_with_three_bands {

        private List<Band> catalog;

        @BeforeEach
        void setup() {
            catalog = List.of(
                    band("1", "Nirvana", "grunge"),
                    band("2", "Goo Goo Dolls", "rock"),
                    band("3", "The Beatles", "classic rock")
            );
            when(clientMock.fetchAll()).thenReturn(catalog);
        }

        @Nested
        class When_get_all_is_called {

            @Test
            void Then_should_return_all_bands() {
                List<Band> result = service.getAll();

                assertThat(result).hasSize(3).containsExactlyElementsOf(catalog);
            }
        }

        @Nested
        class When_get_by_name_is_called_with_exact_name {

            @Test
            void Then_should_return_the_matching_band() {
                Band result = service.getByName("Nirvana");

                assertThat(result.name()).isEqualTo("Nirvana");
            }
        }

        @Nested
        class When_get_by_name_is_called_with_different_casing {

            @Test
            void Then_should_return_the_matching_band() {
                Band result = service.getByName("nirvana");

                assertThat(result.name()).isEqualTo("Nirvana");
            }
        }

        @Nested
        class When_get_by_id_is_called_with_an_existing_id {

            @Test
            void Then_should_return_the_matching_band() {
                Band result = service.getById("2");

                assertThat(result.name()).isEqualTo("Goo Goo Dolls");
            }
        }

        @Nested
        class When_search_by_name_is_called_with_a_partial_match {

            @Test
            void Then_should_return_matching_bands() {
                List<Band> result = service.searchByName("goo");

                assertThat(result).extracting(Band::name).containsExactly("Goo Goo Dolls");
            }
        }

        @Nested
        class When_search_by_name_is_called_with_no_match {

            @Test
            void Then_should_return_empty_list() {
                List<Band> result = service.searchByName("xyz");

                assertThat(result).isEmpty();
            }
        }

        @Nested
        class When_get_by_name_is_called_with_an_unknown_name {

            @Test
            void Then_should_throw_band_not_found_exception() {
                assertThatThrownBy(() -> service.getByName("Unknown"))
                        .isInstanceOf(BandNotFoundException.class)
                        .hasMessageContaining("Unknown");
            }
        }

        @Nested
        class When_get_by_id_is_called_with_an_unknown_id {

            @Test
            void Then_should_throw_band_not_found_exception() {
                assertThatThrownBy(() -> service.getById("missing"))
                        .isInstanceOf(BandNotFoundException.class)
                        .hasMessageContaining("missing");
            }
        }
    }

    @Nested
    class Given_a_blank_band_name {

        @Nested
        class When_get_by_name_is_called {

            @Test
            void Then_should_throw_illegal_argument_exception() {
                assertThatThrownBy(() -> service.getByName("  "))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    @Nested
    class Given_a_blank_band_id {

        @Nested
        class When_get_by_id_is_called {

            @Test
            void Then_should_throw_illegal_argument_exception() {
                assertThatThrownBy(() -> service.getById(""))
                        .isInstanceOf(IllegalArgumentException.class);
            }
        }
    }

    private Band band(String id, String name, String genre) {
        return new Band(id, name, "img", genre, "bio", 100L, List.of());
    }
}
