package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.AccountEntity;
import com.citizens.banking.liquidity.domain.DepositEntity;
import com.citizens.banking.liquidity.dto.CreateDepositRequest;
import com.citizens.banking.liquidity.dto.DepositResponse;
import com.citizens.banking.liquidity.exception.AccountNotFoundException;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.repository.AccountRepository;
import com.citizens.banking.liquidity.repository.DepositRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositService {

    private final DepositRepository depositRepository;
    private final AccountRepository accountRepository;

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

    @Transactional
    public DepositResponse create(CreateDepositRequest request) {
        log.info("Creating new deposit for accountId={}, dpfRefId={}", request.getAccountId(), request.getDpfRefId());
        AccountEntity account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> {
                    log.warn("Account not found during deposit creation accountId={}", request.getAccountId());
                    return new AccountNotFoundException(request.getAccountId());
                });

        OffsetDateTime now = OffsetDateTime.now();
        DepositEntity entity = DepositEntity.builder()
                .account(account)
                .dpfRefId(request.getDpfRefId())
                .depositAmount(request.getDepositAmount())
                .currency(request.getCurrency())
                .status(request.getStatus())
                .createdAt(now)
                .updatedAt(now)
                .build();

        DepositEntity saved = depositRepository.save(entity);
        log.info("Deposit created successfully depositId={}", saved.getDepositId());
        return toResponse(saved);
    }

    private DepositResponse toResponse(DepositEntity entity) {
        return DepositResponse.builder()
                .depositId(entity.getDepositId())
                .accountId(entity.getAccount().getAccountId())
                .dpfRefId(entity.getDpfRefId())
                .depositAmount(entity.getDepositAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
