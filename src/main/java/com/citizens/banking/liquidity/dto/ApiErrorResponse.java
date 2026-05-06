package com.citizens.banking.liquidity.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class ApiErrorResponse {

    int status;
    String error;
    String message;
    String path;
    Instant timestamp;
}
