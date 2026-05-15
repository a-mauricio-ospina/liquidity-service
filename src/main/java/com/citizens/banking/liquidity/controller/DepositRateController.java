package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.ApiErrorResponse;
import com.citizens.banking.liquidity.dto.CreateDepositRateRequest;
import com.citizens.banking.liquidity.dto.DepositRateResponse;
import com.citizens.banking.liquidity.service.DepositRateService;
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

@Tag(name = "DepositRates", description = "Operations for managing deposit rates")
@Slf4j
@RestController
@RequestMapping("/api/v1/deposit-rates")
@RequiredArgsConstructor
public class DepositRateController {

    private final DepositRateService depositRateService;

    @Operation(summary = "List all deposit rates", description = "Returns a list of all deposit rate records in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit rates retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepositRateResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<DepositRateResponse>> getAllDepositRates() {
        log.debug("Request received: list all deposit rates");
        List<DepositRateResponse> rates = depositRateService.findAll();
        log.debug("Returning {} deposit rates", rates.size());
        return ResponseEntity.ok(rates);
    }

    @Operation(summary = "Get deposit rate by ID", description = "Returns a single deposit rate record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit rate found",
            content = @Content(schema = @Schema(implementation = DepositRateResponse.class))),
        @ApiResponse(responseCode = "404", description = "Deposit rate not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{depositRateId}")
    public ResponseEntity<DepositRateResponse> getDepositRateById(
            @Parameter(description = "Unique identifier of the deposit rate", required = true)
            @PathVariable Long depositRateId) {
        log.debug("Request received: get deposit rate depositRateId={}", depositRateId);
        DepositRateResponse rate = depositRateService.findById(depositRateId);
        log.debug("Returning deposit rate depositRateId={}", depositRateId);
        return ResponseEntity.ok(rate);
    }

    @Operation(summary = "Create a new deposit rate", description = "Creates a new deposit rate record linked to an existing deposit and market rate version. Validates the request payload before processing.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Deposit rate created successfully",
            content = @Content(schema = @Schema(implementation = DepositRateResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Deposit or market rate version not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DepositRateResponse> createDepositRate(
            @RequestBody(description = "Deposit rate creation payload", required = true,
                content = @Content(schema = @Schema(implementation = CreateDepositRateRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateDepositRateRequest request) {

        log.debug("Request received: create deposit rate depositId={}, rateVersionId={}", request.getDepositId(), request.getRateVersionId());
        DepositRateResponse created = depositRateService.create(request);
        log.debug("Deposit rate created depositRateId={}", created.getDepositRateId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
