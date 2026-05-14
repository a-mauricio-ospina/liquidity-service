package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Deposit record returned by the API")
@Value
@Builder
public class DepositResponse {

    @Schema(description = "Unique identifier of the deposit", example = "1")
    Long depositId;

    @Schema(description = "Identifier of the associated account", example = "10")
    Long accountId;

    @Schema(description = "DPF reference identifier", example = "DPF-2026-00001")
    String dpfRefId;

    @Schema(description = "Deposit amount", example = "50000.00")
    BigDecimal depositAmount;

    @Schema(description = "ISO-4217 currency code", example = "USD")
    String currency;

    @Schema(description = "Current status of the deposit", example = "ACTIVE")
    String status;

    @Schema(description = "Timestamp when the deposit record was created")
    OffsetDateTime createdAt;

    @Schema(description = "Timestamp when the deposit record was last updated")
    OffsetDateTime updatedAt;
}
