package com.citizens.banking.liquidity.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;

@Schema(description = "Customer record returned by the API")
@Value
@Builder
public class CustomerResponse {

    @Schema(description = "Unique identifier of the customer", example = "1")
    Long customerId;

    @Schema(description = "Full name of the customer", example = "Acme Corporation")
    String customerName;

    @Schema(description = "Customer classification type", example = "CORPORATE")
    String customerType;

    @Schema(description = "Current status of the customer account", example = "ACTIVE")
    String status;

    @Schema(description = "Relationship Manager identifier", example = "501")
    Long rmId;

    @Schema(description = "Acquisition or servicing channel", example = "DIGITAL")
    String channel;

    @Schema(description = "Geographic region of the customer", example = "NORTHEAST")
    String region;

    @Schema(description = "Timestamp when the customer record was created")
    OffsetDateTime createdAt;

    @Schema(description = "Timestamp when the customer record was last updated")
    OffsetDateTime updatedAt;
}
