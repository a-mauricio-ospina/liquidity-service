package com.citizens.banking.liquidity.account.application.service;

import com.citizens.banking.liquidity.account.domain.model.AccountEntity;
import com.citizens.banking.liquidity.account.dto.AccountResponse;
import com.citizens.banking.liquidity.account.dto.CreateAccountRequest;
import com.citizens.banking.liquidity.account.infrastructure.repository.AccountRepository;
import com.citizens.banking.liquidity.customer.domain.model.CustomerEntity;
import com.citizens.banking.liquidity.customer.infrastructure.repository.CustomerRepository;
import com.citizens.banking.liquidity.exception.AccountNotFoundException;
import com.citizens.banking.liquidity.exception.CustomerNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private CustomerEntity buildCustomer() {
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

    private AccountEntity buildEntity() {
        return AccountEntity.builder()
                .accountId(10L)
                .customer(buildCustomer())
                .accountNumber("ACC-0001-2026")
                .accountType("CHECKING")
                .currency("USD")
                .status("ACTIVE")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTill(null)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void findAll_returnsListOfAccountResponses() {
        when(accountRepository.findAll()).thenReturn(List.of(buildEntity()));

        List<AccountResponse> result = accountService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountId()).isEqualTo(10L);
        assertThat(result.get(0).getAccountNumber()).isEqualTo("ACC-0001-2026");
        assertThat(result.get(0).getCustomerId()).isEqualTo(1L);
    }

    @Test
    void findAll_returnsEmptyList_whenNoAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of());

        List<AccountResponse> result = accountService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsAccountResponse_whenFound() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(buildEntity()));

        AccountResponse result = accountService.findById(10L);

        assertThat(result.getAccountId()).isEqualTo(10L);
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void findById_throwsAccountNotFoundException_whenNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(99L))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEntityAndReturnsResponse() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(buildCustomer()));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(buildEntity());

        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(1L)
                .accountNumber("ACC-0001-2026")
                .accountType("CHECKING")
                .currency("USD")
                .status("ACTIVE")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();

        AccountResponse result = accountService.create(request);

        assertThat(result.getAccountId()).isEqualTo(10L);
        assertThat(result.getAccountNumber()).isEqualTo("ACC-0001-2026");
        assertThat(result.getCustomerId()).isEqualTo(1L);
        verify(accountRepository).save(any(AccountEntity.class));
    }

    @Test
    void create_throwsCustomerNotFoundException_whenCustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(99L)
                .accountNumber("ACC-0001-2026")
                .accountType("CHECKING")
                .currency("USD")
                .status("ACTIVE")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("99");
    }
}
