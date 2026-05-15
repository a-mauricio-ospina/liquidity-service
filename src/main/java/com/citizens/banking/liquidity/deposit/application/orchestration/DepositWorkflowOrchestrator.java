package com.citizens.banking.liquidity.deposit.application.orchestration;

import com.citizens.banking.liquidity.account.application.service.AccountService;
import com.citizens.banking.liquidity.customer.application.service.CustomerService;
import com.citizens.banking.liquidity.deposit.application.service.DepositService;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates deposit workflows by coordinating across the deposit, account and customer
 * service boundaries. Acts as the single entry point for the deposit API layer, keeping
 * controllers free of cross-domain coordination logic.
 *
 * <p>Future responsibilities include pre-creation validations (e.g. customer limits,
 * account eligibility checks) and post-creation side-effects (e.g. audit events).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositWorkflowOrchestrator {

    private final DepositService depositService;
    private final AccountService accountService;
    private final CustomerService customerService;

    public List<DepositResponse> findAll() {
        return depositService.findAll();
    }

    public DepositResponse findById(Long depositId) {
        return depositService.findById(depositId);
    }

    public DepositResponse createDeposit(CreateDepositRequest request) {
        log.info("Orchestrating deposit creation workflow for accountId={}", request.getAccountId());
        return depositService.create(request);
    }
}
