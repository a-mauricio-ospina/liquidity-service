package com.citizens.banking.liquidity.repository;

import com.citizens.banking.liquidity.domain.DepositSubAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositSubAccountRepository extends JpaRepository<DepositSubAccountEntity, Long> {
}
