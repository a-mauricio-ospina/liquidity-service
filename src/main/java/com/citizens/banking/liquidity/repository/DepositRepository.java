package com.citizens.banking.liquidity.repository;

import com.citizens.banking.liquidity.domain.DepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepositRepository extends JpaRepository<DepositEntity, Long> {
}
