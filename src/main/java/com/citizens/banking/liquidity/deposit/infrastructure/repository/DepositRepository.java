package com.citizens.banking.liquidity.deposit.infrastructure.repository;

import com.citizens.banking.liquidity.deposit.domain.model.DepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<DepositEntity, Long> {
}
