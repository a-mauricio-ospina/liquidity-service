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

@Schema(description = "Request payload to create a new deposit sub-account")
@Value
@Builder
@Jacksonized
public class CreateDepositSubAccountRequest {

    @NotNull
    @Positive
    @Schema(description = "Deposit identifier associated with this sub-account", example = "100")
    Long depositId;

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Name of the party holding this sub-account", example = "John Doe")
    String partyName;

    @NotNull
    @Positive
    @Schema(description = "Participation share as a percentage", example = "50.00")
    BigDecimal share;

    @NotNull
    @Positive
    @Schema(description = "Interest rate applied to this sub-account", example = "4.25")
    BigDecimal rate;
}
