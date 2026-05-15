package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Schema(description = "Market rate version record returned by the API")
@Value
@Builder
public class MarketRateVersionResponse {

    @Schema(description = "Unique identifier of the market rate version", example = "1")
    Long rateVersionId;

    @Schema(description = "Base interest rate", example = "4.00")
    BigDecimal baseRate;

    @Schema(description = "Spread over base rate", example = "0.25")
    BigDecimal spread;

    @Schema(description = "All-in rate (base rate + spread)", example = "4.25")
    BigDecimal allInRate;

    @Schema(description = "Date from which this rate version is effective")
    LocalDate effectiveFrom;

    @Schema(description = "Date until which this rate version is effective")
    LocalDate effectiveTill;

    @Schema(description = "Timestamp when the record was created")
    OffsetDateTime createdAt;

    @Schema(description = "Timestamp when the record was last updated")
    OffsetDateTime updatedAt;
}
