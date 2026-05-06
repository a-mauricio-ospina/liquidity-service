package com.citizens.banking.liquidity.exception;

public class DepositNotFoundException extends RuntimeException {

    public DepositNotFoundException(String depositId) {
        super("Deposit not found with id: " + depositId);
    }
}
