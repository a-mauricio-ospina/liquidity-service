package com.citizens.banking.liquidity.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class DepositResponse {

    String depositId;
    String accountNumber;
    BigDecimal amount;
    String currency;
    String status;
}
