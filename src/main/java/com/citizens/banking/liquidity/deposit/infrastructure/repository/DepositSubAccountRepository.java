package com.citizens.banking.liquidity.deposit.infrastructure.repository;

import com.citizens.banking.liquidity.deposit.domain.model.DepositSubAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositSubAccountRepository extends JpaRepository<DepositSubAccountEntity, Long> {
}
