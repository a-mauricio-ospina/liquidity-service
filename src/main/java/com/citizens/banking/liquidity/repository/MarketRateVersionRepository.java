package com.citizens.banking.liquidity.repository;

import com.citizens.banking.liquidity.domain.MarketRateVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRateVersionRepository extends JpaRepository<MarketRateVersionEntity, Long> {
}
