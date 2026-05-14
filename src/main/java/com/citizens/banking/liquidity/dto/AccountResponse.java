package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(description = "Account record returned by the API")
@Value
@Builder
public class AccountResponse {

    @Schema(description = "Unique identifier of the account", example = "1")
    Long accountId;

    @Schema(description = "Identifier of the associated customer", example = "1")
    Long customerId;

    @Schema(description = "Unique account number", example = "ACC-0001-2026")
    String accountNumber;

    @Schema(description = "Type of account", example = "CHECKING")
    String accountType;

    @Schema(description = "ISO-4217 currency code", example = "USD")
    String currency;

    @Schema(description = "Current status of the account", example = "ACTIVE")
    String status;

    @Schema(description = "Date from which the account is effective")
    LocalDate effectiveFrom;

    @Schema(description = "Date until which the account is effective")
    LocalDate effectiveTill;

    @Schema(description = "Timestamp when the account record was created")
    OffsetDateTime createdAt;

    @Schema(description = "Timestamp when the account record was last updated")
    OffsetDateTime updatedAt;
}
