package com.citizens.banking.liquidity.customer.application.service;

import com.citizens.banking.liquidity.customer.domain.model.CustomerEntity;
import com.citizens.banking.liquidity.customer.dto.CreateCustomerRequest;
import com.citizens.banking.liquidity.customer.dto.CustomerResponse;
import com.citizens.banking.liquidity.customer.infrastructure.repository.CustomerRepository;
import com.citizens.banking.liquidity.exception.CustomerNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        log.info("Fetching all customers from database");
        return customerRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long customerId) {
        log.info("Fetching customer customerId={}", customerId);
        return customerRepository.findById(customerId)
                .map(this::toResponse)
                .orElseThrow(() -> {
                    log.warn("Customer not found customerId={}", customerId);
                    return new CustomerNotFoundException(customerId);
                });
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        log.info("Creating new customer name={}, type={}", request.getCustomerName(), request.getCustomerType());
        OffsetDateTime now = OffsetDateTime.now();
        CustomerEntity entity = CustomerEntity.builder()
                .customerName(request.getCustomerName())
                .customerType(request.getCustomerType())
                .status(request.getStatus())
                .rmId(request.getRmId())
                .channel(request.getChannel())
                .region(request.getRegion())
                .createdAt(now)
                .updatedAt(now)
                .build();
        CustomerEntity saved = customerRepository.save(entity);
        log.info("Customer created successfully customerId={}", saved.getCustomerId());
        return toResponse(saved);
    }

    private CustomerResponse toResponse(CustomerEntity entity) {
        return CustomerResponse.builder()
                .customerId(entity.getCustomerId())
                .customerName(entity.getCustomerName())
                .customerType(entity.getCustomerType())
                .status(entity.getStatus())
                .rmId(entity.getRmId())
                .channel(entity.getChannel())
                .region(entity.getRegion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
