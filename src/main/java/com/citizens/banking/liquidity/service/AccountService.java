package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.AccountEntity;
import com.citizens.banking.liquidity.domain.CustomerEntity;
import com.citizens.banking.liquidity.dto.AccountResponse;
import com.citizens.banking.liquidity.dto.CreateAccountRequest;
import com.citizens.banking.liquidity.exception.AccountNotFoundException;
import com.citizens.banking.liquidity.exception.CustomerNotFoundException;
import com.citizens.banking.liquidity.repository.AccountRepository;
import com.citizens.banking.liquidity.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        log.info("Fetching all accounts from database");
        return accountRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(Long accountId) {
        log.info("Fetching account accountId={}", accountId);
        return accountRepository.findById(accountId)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("Account not found accountId={}", accountId);
                    return new AccountNotFoundException(accountId);
                });
    }

    @Transactional
    public AccountResponse create(CreateAccountRequest request) {
        log.info("Creating new account for customerId={}, type={}", request.getCustomerId(), request.getAccountType());
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> {
                    log.warn("Customer not found during account creation customerId={}", request.getCustomerId());
                    return new CustomerNotFoundException(request.getCustomerId());
                });

        OffsetDateTime now = OffsetDateTime.now();
        AccountEntity entity = AccountEntity.builder()
                .customer(customer)
                .accountNumber(request.getAccountNumber())
                .accountType(request.getAccountType())
                .currency(request.getCurrency())
                .status(request.getStatus())
                .effectiveFrom(request.getEffectiveFrom())
                .effectiveTill(request.getEffectiveTill())
                .createdAt(now)
                .updatedAt(now)
                .build();

        AccountEntity saved = accountRepository.save(entity);
        log.info("Account created successfully accountId={}", saved.getAccountId());
        return toResponse(saved);
    }

    private AccountResponse toResponse(AccountEntity entity) {
        return AccountResponse.builder()
                .accountId(entity.getAccountId())
                .customerId(entity.getCustomer().getCustomerId())
                .accountNumber(entity.getAccountNumber())
                .accountType(entity.getAccountType())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTill(entity.getEffectiveTill())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
