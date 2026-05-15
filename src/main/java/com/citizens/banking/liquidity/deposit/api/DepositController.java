package com.citizens.banking.liquidity.deposit.api;

import com.citizens.banking.liquidity.deposit.application.orchestration.DepositWorkflowOrchestrator;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositResponse;
import com.citizens.banking.liquidity.exception.ApiErrorResponse;
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

@Tag(name = "Deposits", description = "Operations for managing liquidity deposits")
@Slf4j
@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositWorkflowOrchestrator depositWorkflowOrchestrator;

    @Operation(summary = "List all deposits", description = "Returns a list of all deposit records in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposits retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepositResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<DepositResponse>> getAllDeposits() {
        log.debug("Request received: list all deposits");
        List<DepositResponse> deposits = depositWorkflowOrchestrator.findAll();
        log.debug("Returning {} deposits", deposits.size());
        return ResponseEntity.ok(deposits);
    }

    @Operation(summary = "Get deposit by ID", description = "Returns a single deposit record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit found",
            content = @Content(schema = @Schema(implementation = DepositResponse.class))),
        @ApiResponse(responseCode = "404", description = "Deposit not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{depositId}")
    public ResponseEntity<DepositResponse> getDepositById(
            @Parameter(description = "Unique identifier of the deposit", required = true)
            @PathVariable Long depositId) {
        log.debug("Request received: get deposit depositId={}", depositId);
        DepositResponse deposit = depositWorkflowOrchestrator.findById(depositId);
        log.debug("Returning deposit depositId={}", depositId);
        return ResponseEntity.ok(deposit);
    }

    @Operation(summary = "Create a new deposit", description = "Creates a new deposit record linked to an existing account. Validates the request payload before processing.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Deposit created successfully",
            content = @Content(schema = @Schema(implementation = DepositResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Account not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DepositResponse> createDeposit(
            @RequestBody(description = "Deposit creation payload", required = true,
                content = @Content(schema = @Schema(implementation = CreateDepositRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateDepositRequest request) {

        log.debug("Request received: create deposit accountId={}, dpfRefId={}", request.getAccountId(), request.getDpfRefId());
        DepositResponse created = depositWorkflowOrchestrator.createDeposit(request);
        log.debug("Deposit created depositId={}", created.getDepositId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
