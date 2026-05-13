package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request payload to create a new deposit")
@Value
@Builder
@Jacksonized
public class CreateDepositRequest {

    @NotNull
    @Schema(description = "Account identifier associated with this deposit", example = "1001")
    Long accountId;

    @NotBlank
    @Schema(description = "Type of deposit product", example = "TERM_DEPOSIT")
    String depositType;

    @NotNull
    @Positive
    @Schema(description = "Principal amount in USD", example = "50000.00")
    BigDecimal principalAmount;

    @NotNull
    @Positive
    @Schema(description = "Annual interest rate as a percentage", example = "4.50")
    BigDecimal interestRate;

    @NotNull
    @Schema(description = "Date on which the deposit matures", example = "2027-05-13")
    LocalDate maturityDate;
}
