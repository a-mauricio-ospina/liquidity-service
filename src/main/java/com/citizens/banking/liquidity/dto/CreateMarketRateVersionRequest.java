package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request payload to create a new market rate version")
@Value
@Builder
@Jacksonized
public class CreateMarketRateVersionRequest {

    @NotNull
    @Positive
    @Schema(description = "Base interest rate", example = "4.00")
    BigDecimal baseRate;

    @NotNull
    @Positive
    @Schema(description = "Spread over base rate", example = "0.25")
    BigDecimal spread;

    @NotNull
    @Positive
    @Schema(description = "All-in rate (base rate + spread)", example = "4.25")
    BigDecimal allInRate;

    @NotNull
    @Schema(description = "Date from which this rate version is effective", example = "2026-01-01")
    LocalDate effectiveFrom;

    @Schema(description = "Date until which this rate version is effective (optional)", example = "2026-12-31")
    LocalDate effectiveTill;
}
