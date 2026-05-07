package com.citizens.banking.liquidity.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
@Builder
public class DepositResponse {

    Long depositId;
    Long accountId;
    String depositType;
    BigDecimal principalAmount;
    BigDecimal interestRate;
    BigDecimal accruedInterest;
    LocalDate maturityDate;
    String status;
}
