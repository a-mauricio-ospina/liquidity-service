package com.citizens.banking.liquidity.repository;

import com.citizens.banking.liquidity.domain.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
}
