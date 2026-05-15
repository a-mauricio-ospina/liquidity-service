package com.citizens.banking.liquidity.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Schema(description = "Deposit sub-account record returned by the API")
@Value
@Builder
public class DepositSubAccountResponse {

    @Schema(description = "Unique identifier of the deposit sub-account", example = "1")
    Long depositSubAccountId;

    @Schema(description = "Identifier of the associated deposit", example = "100")
    Long depositId;

    @Schema(description = "Name of the party holding this sub-account", example = "John Doe")
    String partyName;

    @Schema(description = "Participation share as a percentage", example = "50.00")
    BigDecimal share;

    @Schema(description = "Interest rate applied to this sub-account", example = "4.25")
    BigDecimal rate;

    @Schema(description = "Timestamp when the deposit sub-account record was created")
    OffsetDateTime createdAt;

    @Schema(description = "Timestamp when the deposit sub-account record was last updated")
    OffsetDateTime updatedAt;
}
