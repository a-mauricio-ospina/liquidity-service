package com.citizens.banking.liquidity.deposit.application.service;

import com.citizens.banking.liquidity.account.domain.model.AccountEntity;
import com.citizens.banking.liquidity.account.infrastructure.repository.AccountRepository;
import com.citizens.banking.liquidity.customer.domain.model.CustomerEntity;
import com.citizens.banking.liquidity.deposit.domain.model.DepositEntity;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositResponse;
import com.citizens.banking.liquidity.deposit.infrastructure.repository.DepositRepository;
import com.citizens.banking.liquidity.exception.AccountNotFoundException;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class DepositServiceTest {

    @Mock
    private DepositRepository depositRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private DepositService depositService;

    private AccountEntity buildAccount() {
        CustomerEntity customer = CustomerEntity.builder()
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

        return AccountEntity.builder()
                .accountId(10L)
                .customer(customer)
                .accountNumber("ACC-0001-2026")
                .accountType("CHECKING")
                .currency("USD")
                .status("ACTIVE")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private DepositEntity buildEntity() {
        return DepositEntity.builder()
                .depositId(100L)
                .account(buildAccount())
                .dpfRefId("DPF-2026-00001")
                .depositAmount(new BigDecimal("50000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void findAll_returnsListOfDepositResponses() {
        when(depositRepository.findAll()).thenReturn(List.of(buildEntity()));

        List<DepositResponse> result = depositService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepositId()).isEqualTo(100L);
        assertThat(result.get(0).getAccountId()).isEqualTo(10L);
        assertThat(result.get(0).getDpfRefId()).isEqualTo("DPF-2026-00001");
    }

    @Test
    void findAll_returnsEmptyList_whenNoDeposits() {
        when(depositRepository.findAll()).thenReturn(List.of());

        List<DepositResponse> result = depositService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsDepositResponse_whenFound() {
        when(depositRepository.findById(100L)).thenReturn(Optional.of(buildEntity()));

        DepositResponse result = depositService.findById(100L);

        assertThat(result.getDepositId()).isEqualTo(100L);
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void findById_throwsDepositNotFoundException_whenNotFound() {
        when(depositRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> depositService.findById(99L))
                .isInstanceOf(DepositNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEntityAndReturnsResponse() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(buildAccount()));
        when(depositRepository.save(any(DepositEntity.class))).thenReturn(buildEntity());

        CreateDepositRequest request = CreateDepositRequest.builder()
                .accountId(10L)
                .dpfRefId("DPF-2026-00001")
                .depositAmount(new BigDecimal("50000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();

        DepositResponse result = depositService.create(request);

        assertThat(result.getDepositId()).isEqualTo(100L);
        assertThat(result.getDpfRefId()).isEqualTo("DPF-2026-00001");
        assertThat(result.getAccountId()).isEqualTo(10L);
        verify(depositRepository).save(any(DepositEntity.class));
    }

    @Test
    void create_throwsAccountNotFoundException_whenAccountNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        CreateDepositRequest request = CreateDepositRequest.builder()
                .accountId(99L)
                .dpfRefId("DPF-2026-00001")
                .depositAmount(new BigDecimal("50000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();

        assertThatThrownBy(() -> depositService.create(request))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessageContaining("99");
    }
}
