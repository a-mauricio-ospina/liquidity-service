package com.citizens.banking.liquidity.exception;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
public class ApiErrorResponse {

    int status;
    String error;
    String message;
    String path;
    Instant timestamp;
    Map<String, String> fieldErrors;
}
