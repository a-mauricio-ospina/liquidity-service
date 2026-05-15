package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.MarketRateVersionEntity;
import com.citizens.banking.liquidity.dto.CreateMarketRateVersionRequest;
import com.citizens.banking.liquidity.dto.MarketRateVersionResponse;
import com.citizens.banking.liquidity.exception.MarketRateVersionNotFoundException;
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
public class MarketRateVersionService {

    private final MarketRateVersionRepository marketRateVersionRepository;

    @Transactional(readOnly = true)
    public List<MarketRateVersionResponse> findAll() {
        log.info("Fetching all market rate versions from database");
        return marketRateVersionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MarketRateVersionResponse findById(Long rateVersionId) {
        log.info("Fetching market rate version rateVersionId={}", rateVersionId);
        return marketRateVersionRepository.findById(rateVersionId)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("MarketRateVersion not found rateVersionId={}", rateVersionId);
                    return new MarketRateVersionNotFoundException(rateVersionId);
                });
    }

    @Transactional
    public MarketRateVersionResponse create(CreateMarketRateVersionRequest request) {
        log.info("Creating new market rate version baseRate={}, allInRate={}", request.getBaseRate(), request.getAllInRate());
        OffsetDateTime now = OffsetDateTime.now();
        MarketRateVersionEntity entity = MarketRateVersionEntity.builder()
                .baseRate(request.getBaseRate())
                .spread(request.getSpread())
                .allInRate(request.getAllInRate())
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTill(request.getEffectiveTill())
                .createdAt(now)
                .updatedAt(now)
                .build();

        MarketRateVersionEntity saved = marketRateVersionRepository.save(entity);
        log.info("MarketRateVersion created successfully rateVersionId={}", saved.getRateVersionId());
        return toResponse(saved);
    }

    private MarketRateVersionResponse toResponse(MarketRateVersionEntity entity) {
        return MarketRateVersionResponse.builder()
                .rateVersionId(entity.getRateVersionId())
                .baseRate(entity.getBaseRate())
                .spread(entity.getSpread())
                .allInRate(entity.getAllInRate())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTill(entity.getEffectiveTill())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
