package com.challenge.bandsapi.controller;

import com.challenge.bandsapi.dto.ApiErrorResponse;
import com.challenge.bandsapi.dto.BandResponse;
import com.challenge.bandsapi.mapper.BandResponseMapper;
import com.challenge.bandsapi.service.BandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Bands", description = "Operations for querying the bands catalog")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bands")
public class BandController {

    private final BandService bandService;
    private final BandResponseMapper mapper;

    @Operation(summary = "List all bands", description = "Returns the full bands catalog from the external source (cached)")
    @ApiResponse(responseCode = "200", description = "Bands list returned successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BandResponse.class))))
    @GetMapping
    public List<BandResponse> getAll() {
        return bandService.getAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Operation(summary = "Get band by ID", description = "Returns a single band matching the given UUID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Band found",
                    content = @Content(schema = @Schema(implementation = BandResponse.class))),
            @ApiResponse(responseCode = "404", description = "Band not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/id/{id}")
    public BandResponse getById(
            @Parameter(description = "Band UUID", example = "bc710bcf-8815-42cf-bad2-3f1d12246aeb")
            @PathVariable @NotBlank String id) {
        return mapper.toResponse(bandService.getById(id));
    }

    @Operation(summary = "Get band by name", description = "Returns a single band matching the exact name (case-insensitive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Band found",
                    content = @Content(schema = @Schema(implementation = BandResponse.class))),
            @ApiResponse(responseCode = "404", description = "Band not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/by-name/{name}")
    public BandResponse getByName(
            @Parameter(description = "Exact band name", example = "Nickelback")
            @PathVariable @NotBlank String name) {
        return mapper.toResponse(bandService.getByName(name));
    }

    @Operation(summary = "Search bands by name", description = "Returns all bands whose name contains the given search term (case-insensitive)")
    @ApiResponse(responseCode = "200", description = "Search results",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BandResponse.class))))
    @GetMapping("/search")
    public List<BandResponse> searchByName(
            @Parameter(description = "Partial name to search for", example = "the")
            @RequestParam("name") @NotBlank String name) {
        return bandService.searchByName(name).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
