package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.DepositEntity;
import com.citizens.banking.liquidity.domain.DepositRateEntity;
import com.citizens.banking.liquidity.domain.MarketRateVersionEntity;
import com.citizens.banking.liquidity.dto.CreateDepositRateRequest;
import com.citizens.banking.liquidity.dto.DepositRateResponse;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.exception.DepositRateNotFoundException;
import com.citizens.banking.liquidity.exception.MarketRateVersionNotFoundException;
import com.citizens.banking.liquidity.repository.DepositRateRepository;
import com.citizens.banking.liquidity.repository.DepositRepository;
import com.citizens.banking.liquidity.repository.MarketRateVersionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositRateService {

    private final DepositRateRepository depositRateRepository;
    private final DepositRepository depositRepository;
    private final MarketRateVersionRepository marketRateVersionRepository;

    @Transactional(readOnly = true)
    public List<DepositRateResponse> findAll() {
        log.info("Fetching all deposit rates from database");
        return depositRateRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepositRateResponse findById(Long depositRateId) {
        log.info("Fetching deposit rate depositRateId={}", depositRateId);
        return depositRateRepository.findById(depositRateId)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("DepositRate not found depositRateId={}", depositRateId);
                    return new DepositRateNotFoundException(depositRateId);
                });
    }

    @Transactional
    public DepositRateResponse create(CreateDepositRateRequest request) {
        log.info("Creating new deposit rate for depositId={}, rateVersionId={}", request.getDepositId(), request.getRateVersionId());

        DepositEntity deposit = depositRepository.findById(request.getDepositId())
                .orElseThrow(() -> {
                    log.warn("Deposit not found during deposit rate creation depositId={}", request.getDepositId());
                    return new DepositNotFoundException(request.getDepositId());
                });

        MarketRateVersionEntity marketRateVersion = marketRateVersionRepository.findById(request.getRateVersionId())
                .orElseThrow(() -> {
                    log.warn("MarketRateVersion not found during deposit rate creation rateVersionId={}", request.getRateVersionId());
                    return new MarketRateVersionNotFoundException(request.getRateVersionId());
                });

        OffsetDateTime now = OffsetDateTime.now();
        DepositRateEntity entity = DepositRateEntity.builder()
                .deposit(deposit)
                .marketRateVersion(marketRateVersion)
                .allInRate(request.getAllInRate())
                .status(request.getStatus())
                .createdAt(now)
                .updatedAt(now)
                .build();

        DepositRateEntity saved = depositRateRepository.save(entity);
        log.info("DepositRate created successfully depositRateId={}", saved.getDepositRateId());
        return toResponse(saved);
    }

    private DepositRateResponse toResponse(DepositRateEntity entity) {
        return DepositRateResponse.builder()
                .depositRateId(entity.getDepositRateId())
                .depositId(entity.getDeposit().getDepositId())
                .rateVersionId(entity.getMarketRateVersion().getRateVersionId())
                .allInRate(entity.getAllInRate())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
