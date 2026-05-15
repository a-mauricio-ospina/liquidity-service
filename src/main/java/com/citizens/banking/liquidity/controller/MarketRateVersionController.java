package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.ApiErrorResponse;
import com.citizens.banking.liquidity.dto.CreateMarketRateVersionRequest;
import com.citizens.banking.liquidity.dto.MarketRateVersionResponse;
import com.citizens.banking.liquidity.service.MarketRateVersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MarketRateVersions", description = "Operations for managing market rate versions")
@Slf4j
@RestController
@RequestMapping("/api/v1/market-rate-versions")
@RequiredArgsConstructor
public class MarketRateVersionController {

    private final MarketRateVersionService marketRateVersionService;

    @Operation(summary = "List all market rate versions", description = "Returns a list of all market rate version records in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Market rate versions retrieved successfully",
            content = @Content(schema = @Schema(implementation = MarketRateVersionResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<MarketRateVersionResponse>> getAllMarketRateVersions() {
        log.debug("Request received: list all market rate versions");
        List<MarketRateVersionResponse> versions = marketRateVersionService.findAll();
        log.debug("Returning {} market rate versions", versions.size());
        return ResponseEntity.ok(versions);
    }

    @Operation(summary = "Get market rate version by ID", description = "Returns a single market rate version record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Market rate version found",
            content = @Content(schema = @Schema(implementation = MarketRateVersionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Market rate version not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{rateVersionId}")
    public ResponseEntity<MarketRateVersionResponse> getMarketRateVersionById(
            @Parameter(description = "Unique identifier of the market rate version", required = true)
            @PathVariable Long rateVersionId) {
        log.debug("Request received: get market rate version rateVersionId={}", rateVersionId);
        MarketRateVersionResponse version = marketRateVersionService.findById(rateVersionId);
        log.debug("Returning market rate version rateVersionId={}", rateVersionId);
        return ResponseEntity.ok(version);
    }

    @Operation(summary = "Create a new market rate version", description = "Creates a new market rate version record. Validates the request payload before processing.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Market rate version created successfully",
            content = @Content(schema = @Schema(implementation = MarketRateVersionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<MarketRateVersionResponse> createMarketRateVersion(
            @RequestBody(description = "Market rate version creation payload", required = true,
                content = @Content(schema = @Schema(implementation = CreateMarketRateVersionRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateMarketRateVersionRequest request) {

        log.debug("Request received: create market rate version baseRate={}, allInRate={}", request.getBaseRate(), request.getAllInRate());
        MarketRateVersionResponse created = marketRateVersionService.create(request);
        log.debug("Market rate version created rateVersionId={}", created.getRateVersionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
