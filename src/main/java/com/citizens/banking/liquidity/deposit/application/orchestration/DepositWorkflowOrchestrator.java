package com.citizens.banking.liquidity.deposit.application.orchestration;

import com.citizens.banking.liquidity.deposit.application.service.DepositService;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Orchestrates deposit workflows by coordinating deposit service operations.
 * Acts as the single entry point for the deposit API layer, keeping controllers
 * free of coordination logic.
 *
 * <p>Future responsibilities include pre-creation validations (e.g. account eligibility,
 * rate validation) and post-creation side-effects (e.g. audit events).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositWorkflowOrchestrator {

    private final DepositService depositService;

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
