package com.citizens.banking.liquidity.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Schema(description = "Request payload to create a new customer")
@Value
@Builder
@Jacksonized
public class CreateCustomerRequest {

    @NotBlank
    @Size(max = 255)
    @Schema(description = "Full name of the customer", example = "Acme Corporation")
    String customerName;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Customer classification type", example = "CORPORATE")
    String customerType;

    @NotBlank
    @Size(max = 20)
    @Schema(description = "Current status of the customer account", example = "ACTIVE")
    String status;

    @NotNull
    @Positive
    @Schema(description = "Relationship Manager identifier assigned to this customer", example = "501")
    Long rmId;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Acquisition or servicing channel", example = "DIGITAL")
    String channel;

    @NotBlank
    @Size(max = 50)
    @Schema(description = "Geographic region of the customer", example = "NORTHEAST")
    String region;
}
