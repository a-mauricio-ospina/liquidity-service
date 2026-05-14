package com.citizens.banking.liquidity.repository;

import com.citizens.banking.liquidity.domain.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
