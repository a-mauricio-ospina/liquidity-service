package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Deposit rate record returned by the API")
@Value
@Builder
public class DepositRateResponse {

    @Schema(description = "Unique identifier of the deposit rate", example = "1")
    Long depositRateId;

    @Schema(description = "Identifier of the associated deposit", example = "100")
    Long depositId;

    @Schema(description = "Identifier of the associated market rate version", example = "1")
    Long rateVersionId;

    @Schema(description = "All-in rate applied to the deposit", example = "4.25")
    BigDecimal allInRate;

    @Schema(description = "Current status of the deposit rate", example = "ACTIVE")
    String status;

    @Schema(description = "Timestamp when the deposit rate record was created")
    OffsetDateTime createdAt;

    @Schema(description = "Timestamp when the deposit rate record was last updated")
    OffsetDateTime updatedAt;
}
