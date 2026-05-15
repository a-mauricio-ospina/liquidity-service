package com.citizens.banking.liquidity.deposit.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.math.BigDecimal;

@Schema(description = "Request payload to create a new deposit rate")
@Value
@Builder
@Jacksonized
public class CreateDepositRateRequest {

    @NotNull
    @Positive
    @Schema(description = "Deposit identifier associated with this rate", example = "100")
    Long depositId;

    @NotNull
    @Positive
    @Schema(description = "Market rate version identifier applied to this deposit rate", example = "1")
    Long rateVersionId;

    @NotNull
    @Positive
    @Schema(description = "All-in rate applied to the deposit", example = "4.25")
    BigDecimal allInRate;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Current status of the deposit rate", example = "ACTIVE")
    String status;
}
