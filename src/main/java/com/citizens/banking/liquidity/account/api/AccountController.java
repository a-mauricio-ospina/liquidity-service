package com.citizens.banking.liquidity.account.api;

import com.citizens.banking.liquidity.account.application.service.AccountService;
import com.citizens.banking.liquidity.account.dto.AccountResponse;
import com.citizens.banking.liquidity.account.dto.CreateAccountRequest;
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

@Tag(name = "Accounts", description = "Operations for managing banking accounts")
@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "List all accounts", description = "Returns a list of all account records in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Accounts retrieved successfully",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        log.debug("Request received: list all accounts");
        List<AccountResponse> accounts = accountService.findAll();
        log.debug("Returning {} accounts", accounts.size());
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Get account by ID", description = "Returns a single account record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Account found",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "404", description = "Account not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccountById(
            @Parameter(description = "Unique identifier of the account", required = true)
            @PathVariable Long accountId) {
        log.debug("Request received: get account accountId={}", accountId);
        AccountResponse account = accountService.findById(accountId);
        log.debug("Returning account accountId={}", accountId);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Create a new account", description = "Creates a new account record linked to an existing customer. Validates the request payload before processing.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Account created successfully",
            content = @Content(schema = @Schema(implementation = AccountResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody(description = "Account creation payload", required = true,
                content = @Content(schema = @Schema(implementation = CreateAccountRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateAccountRequest request) {

        log.debug("Request received: create account customerId={}, type={}", request.getCustomerId(), request.getAccountType());
        AccountResponse created = accountService.create(request);
        log.debug("Account created accountId={}", created.getAccountId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
