package com.citizens.banking.liquidity.exception;

public class MarketRateVersionNotFoundException extends RuntimeException {

    public MarketRateVersionNotFoundException(Long rateVersionId) {
        super("MarketRateVersion not found with id: " + rateVersionId);
    }
}
