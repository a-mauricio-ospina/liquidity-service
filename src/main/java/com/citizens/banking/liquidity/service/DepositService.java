package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.dto.DepositResponse;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.util.DepositConstants;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DepositService {

    private static final List<DepositResponse> DEPOSITS = List.of(
        DepositResponse.builder()
            .depositId("DEP-1001")
            .accountNumber("123456789")
            .amount(new BigDecimal("1500.00"))
            .currency(DepositConstants.DEFAULT_CURRENCY)
            .status(DepositConstants.STATUS_PENDING)
            .build(),
        DepositResponse.builder()
            .depositId("DEP-1002")
            .accountNumber("987654321")
            .amount(new BigDecimal("3200.50"))
            .currency(DepositConstants.DEFAULT_CURRENCY)
            .status(DepositConstants.STATUS_COMPLETED)
            .build()
    );

    public List<DepositResponse> findAll() {
        return DEPOSITS;
    }

    public DepositResponse findById(String depositId) {
        return DEPOSITS.stream()
            .filter(d -> d.getDepositId().equals(depositId))
            .findFirst()
            .orElseThrow(() -> new DepositNotFoundException(depositId));
    }
}
