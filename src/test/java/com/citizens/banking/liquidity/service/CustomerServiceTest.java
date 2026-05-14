package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.CustomerEntity;
import com.citizens.banking.liquidity.dto.CreateCustomerRequest;
import com.citizens.banking.liquidity.dto.CustomerResponse;
import com.citizens.banking.liquidity.exception.CustomerNotFoundException;
import com.citizens.banking.liquidity.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerEntity buildEntity() {
        return CustomerEntity.builder()
                .customerId(1L)
                .customerName("Acme Corporation")
                .customerType("CORPORATE")
                .status("ACTIVE")
                .rmId(501L)
                .channel("DIGITAL")
                .region("NORTHEAST")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void findAll_returnsListOfCustomerResponses() {
        when(customerRepository.findAll()).thenReturn(List.of(buildEntity()));

        List<CustomerResponse> result = customerService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
        assertThat(result.get(0).getCustomerName()).isEqualTo("Acme Corporation");
        assertThat(result.get(0).getCustomerType()).isEqualTo("CORPORATE");
    }

    @Test
    void findAll_returnsEmptyList_whenNoCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of());

        List<CustomerResponse> result = customerService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsCustomerResponse_whenFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(buildEntity()));

        CustomerResponse result = customerService.findById(1L);

        assertThat(result.getCustomerId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("Acme Corporation");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void findById_throwsCustomerNotFoundException_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEntityAndReturnsResponse() {
        CustomerEntity saved = buildEntity();
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(saved);

        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .customerName("Acme Corporation")
                .customerType("CORPORATE")
                .status("ACTIVE")
                .rmId(501L)
                .channel("DIGITAL")
                .region("NORTHEAST")
                .build();

        CustomerResponse result = customerService.create(request);

        assertThat(result.getCustomerId()).isEqualTo(1L);
        assertThat(result.getCustomerName()).isEqualTo("Acme Corporation");
        assertThat(result.getRmId()).isEqualTo(501L);
        verify(customerRepository).save(any(CustomerEntity.class));
    }
}
