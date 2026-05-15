package com.citizens.banking.liquidity.account.infrastructure.repository;

import com.citizens.banking.liquidity.account.domain.model.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
}
