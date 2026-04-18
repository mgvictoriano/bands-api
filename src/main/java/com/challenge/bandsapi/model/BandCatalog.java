package com.challenge.bandsapi.model;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Immutable in-memory view of the bands catalog.
 * Builds O(1) lookup indices by id and by normalized name so that
 * service-level queries do not scan the whole list on every request.
 */
public final class BandCatalog {

    private final List<Band> bands;
    private final Map<String, Band> byId;
    private final Map<String, Band> byNormalizedName;

    public BandCatalog(List<Band> bands) {
        this.bands = List.copyOf(bands);
        this.byId = this.bands.stream()
                .filter(b -> b.id() != null)
                .collect(Collectors.toUnmodifiableMap(Band::id, Function.identity(), (a, b) -> a));
        this.byNormalizedName = this.bands.stream()
                .filter(b -> b.name() != null)
                .collect(Collectors.toUnmodifiableMap(b -> normalize(b.name()), Function.identity(), (a, b) -> a));
    }

    public List<Band> all() {
        return bands;
    }

    public Optional<Band> findById(String id) {
        return Optional.ofNullable(byId.get(id.trim()));
    }

    public Optional<Band> findByName(String name) {
        return Optional.ofNullable(byNormalizedName.get(normalize(name)));
    }

    public List<Band> search(String term) {
        String normalized = normalize(term);
        return bands.stream()
                .filter(b -> b.name() != null && normalize(b.name()).contains(normalized))
                .toList();
    }

    public static String normalize(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
