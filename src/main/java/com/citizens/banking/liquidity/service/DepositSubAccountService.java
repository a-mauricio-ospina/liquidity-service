package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.DepositEntity;
import com.citizens.banking.liquidity.domain.DepositSubAccountEntity;
import com.citizens.banking.liquidity.dto.CreateDepositSubAccountRequest;
import com.citizens.banking.liquidity.dto.DepositSubAccountResponse;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.exception.DepositSubAccountNotFoundException;
import com.citizens.banking.liquidity.repository.DepositRepository;
import com.citizens.banking.liquidity.repository.DepositSubAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositSubAccountService {

    private final DepositSubAccountRepository depositSubAccountRepository;
    private final DepositRepository depositRepository;

    @Transactional(readOnly = true)
    public List<DepositSubAccountResponse> findAll() {
        log.info("Fetching all deposit sub-accounts from database");
        return depositSubAccountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepositSubAccountResponse findById(Long depositSubAccountId) {
        log.info("Fetching deposit sub-account depositSubAccountId={}", depositSubAccountId);
        return depositSubAccountRepository.findById(depositSubAccountId)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("DepositSubAccount not found depositSubAccountId={}", depositSubAccountId);
                    return new DepositSubAccountNotFoundException(depositSubAccountId);
                });
    }

    @Transactional
    public DepositSubAccountResponse create(CreateDepositSubAccountRequest request) {
        log.info("Creating new deposit sub-account for depositId={}, partyName={}", request.getDepositId(), request.getPartyName());
        DepositEntity deposit = depositRepository.findById(request.getDepositId())
                .orElseThrow(() -> {
                    log.warn("Deposit not found during deposit sub-account creation depositId={}", request.getDepositId());
                    return new DepositNotFoundException(request.getDepositId());
                });

        OffsetDateTime now = OffsetDateTime.now();
        DepositSubAccountEntity entity = DepositSubAccountEntity.builder()
                .deposit(deposit)
                .partyName(request.getPartyName())
                .share(request.getShare())
                .rate(request.getRate())
                .createdAt(now)
                .updatedAt(now)
                .build();

        DepositSubAccountEntity saved = depositSubAccountRepository.save(entity);
        log.info("DepositSubAccount created successfully depositSubAccountId={}", saved.getDepositSubAccountId());
        return toResponse(saved);
    }

    private DepositSubAccountResponse toResponse(DepositSubAccountEntity entity) {
        return DepositSubAccountResponse.builder()
                .depositSubAccountId(entity.getDepositSubAccountId())
                .depositId(entity.getDeposit().getDepositId())
                .partyName(entity.getPartyName())
                .share(entity.getShare())
                .rate(entity.getRate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
