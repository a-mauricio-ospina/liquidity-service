package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Schema(description = "Request payload to create a new deposit")
@Value
@Builder
@Jacksonized
public class CreateDepositRequest {

    @NotNull
    @Positive
    @Schema(description = "Account identifier associated with this deposit", example = "10")
    Long accountId;

    @NotBlank
    @Size(max = 100)
    @Schema(description = "DPF reference identifier", example = "DPF-2026-00001")
    String dpfRefId;

    @NotNull
    @Positive
    @Schema(description = "Deposit amount", example = "50000.00")
    BigDecimal depositAmount;

    @NotBlank
    @Size(max = 3)
    @Schema(description = "ISO-4217 currency code", example = "USD")
    String currency;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Current status of the deposit", example = "ACTIVE")
    String status;
}
