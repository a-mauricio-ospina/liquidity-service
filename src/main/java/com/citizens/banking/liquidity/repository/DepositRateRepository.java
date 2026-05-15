package com.citizens.banking.liquidity.repository;

import com.citizens.banking.liquidity.domain.DepositRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRateRepository extends JpaRepository<DepositRateEntity, Long> {
}
