package com.citizens.banking.liquidity.deposit.api;

import com.citizens.banking.liquidity.deposit.application.service.DepositSubAccountService;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositSubAccountRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositSubAccountResponse;
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

@Tag(name = "DepositSubAccounts", description = "Operations for managing deposit sub-accounts")
@Slf4j
@RestController
@RequestMapping("/api/v1/deposit-sub-accounts")
@RequiredArgsConstructor
public class DepositSubAccountController {

    private final DepositSubAccountService depositSubAccountService;

    @Operation(summary = "List all deposit sub-accounts", description = "Returns a list of all deposit sub-account records in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit sub-accounts retrieved successfully",
            content = @Content(schema = @Schema(implementation = DepositSubAccountResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<DepositSubAccountResponse>> getAllDepositSubAccounts() {
        log.debug("Request received: list all deposit sub-accounts");
        List<DepositSubAccountResponse> subAccounts = depositSubAccountService.findAll();
        log.debug("Returning {} deposit sub-accounts", subAccounts.size());
        return ResponseEntity.ok(subAccounts);
    }

    @Operation(summary = "Get deposit sub-account by ID", description = "Returns a single deposit sub-account record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Deposit sub-account found",
            content = @Content(schema = @Schema(implementation = DepositSubAccountResponse.class))),
        @ApiResponse(responseCode = "404", description = "Deposit sub-account not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{depositSubAccountId}")
    public ResponseEntity<DepositSubAccountResponse> getDepositSubAccountById(
            @Parameter(description = "Unique identifier of the deposit sub-account", required = true)
            @PathVariable Long depositSubAccountId) {
        log.debug("Request received: get deposit sub-account depositSubAccountId={}", depositSubAccountId);
        DepositSubAccountResponse subAccount = depositSubAccountService.findById(depositSubAccountId);
        log.debug("Returning deposit sub-account depositSubAccountId={}", depositSubAccountId);
        return ResponseEntity.ok(subAccount);
    }

    @Operation(summary = "Create a new deposit sub-account", description = "Creates a new deposit sub-account record linked to an existing deposit. Validates the request payload before processing.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Deposit sub-account created successfully",
            content = @Content(schema = @Schema(implementation = DepositSubAccountResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Deposit not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<DepositSubAccountResponse> createDepositSubAccount(
            @RequestBody(description = "Deposit sub-account creation payload", required = true,
                content = @Content(schema = @Schema(implementation = CreateDepositSubAccountRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateDepositSubAccountRequest request) {

        log.debug("Request received: create deposit sub-account depositId={}, partyName={}", request.getDepositId(), request.getPartyName());
        DepositSubAccountResponse created = depositSubAccountService.create(request);
        log.debug("Deposit sub-account created depositSubAccountId={}", created.getDepositSubAccountId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
