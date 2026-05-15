package com.citizens.banking.liquidity.customer.infrastructure.repository;

import com.citizens.banking.liquidity.customer.domain.model.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
}
