package com.citizens.banking.liquidity.exception;

public class DepositRateNotFoundException extends RuntimeException {

    public DepositRateNotFoundException(Long depositRateId) {
        super("DepositRate not found with id: " + depositRateId);
    }
}
