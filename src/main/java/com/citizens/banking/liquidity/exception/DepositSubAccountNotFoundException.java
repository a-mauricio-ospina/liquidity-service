package com.citizens.banking.liquidity.exception;

public class DepositSubAccountNotFoundException extends RuntimeException {

    public DepositSubAccountNotFoundException(Long depositSubAccountId) {
        super("DepositSubAccount not found with id: " + depositSubAccountId);
    }
}
