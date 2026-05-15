package com.citizens.banking.liquidity.deposit.infrastructure.repository;

import com.citizens.banking.liquidity.deposit.domain.model.DepositRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRateRepository extends JpaRepository<DepositRateEntity, Long> {
}
