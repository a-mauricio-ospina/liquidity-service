package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.DepositEntity;
import com.citizens.banking.liquidity.dto.DepositResponse;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;

    @Transactional(readOnly = true)
    public List<DepositResponse> findAll() {
        log.info("Fetching all deposits from database");
        return depositRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepositResponse findById(Long depositId) {
        log.info("Fetching deposit depositId={}", depositId);
        return depositRepository.findById(depositId)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("Deposit not found depositId={}", depositId);
                    return new DepositNotFoundException(depositId);
                });
    }

    private DepositResponse toResponse(DepositEntity entity) {
        return DepositResponse.builder()
                .depositId(entity.getDepositId())
                .accountId(entity.getAccountId())
                .depositType(entity.getDepositType())
                .principalAmount(entity.getPrincipalAmount())
                .interestRate(entity.getInterestRate())
                .accruedInterest(entity.getAccruedInterest())
                .maturityDate(entity.getMaturityDate())
                .status(entity.getStatus())
                .build();
    }
}
