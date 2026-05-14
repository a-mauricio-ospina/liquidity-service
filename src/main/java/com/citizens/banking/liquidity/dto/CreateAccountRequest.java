package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Schema(description = "Request payload to create a new account")
@Value
@Builder
@Jacksonized
public class CreateAccountRequest {

    @NotNull
    @Positive
    @Schema(description = "Customer identifier to associate with this account", example = "1")
    Long customerId;

    @NotBlank
    @Size(max = 30)
    @Schema(description = "Unique account number", example = "ACC-0001-2026")
    String accountNumber;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Type of account", example = "CHECKING")
    String accountType;

    @NotBlank
    @Size(max = 3)
    @Schema(description = "ISO-4217 currency code", example = "USD")
    String currency;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Current status of the account", example = "ACTIVE")
    String status;

    @NotNull
    @Schema(description = "Date from which the account is effective", example = "2026-01-01")
    LocalDate effectiveFrom;

    @Schema(description = "Date until which the account is effective (optional)", example = "2027-12-31")
    LocalDate effectiveTill;
}
