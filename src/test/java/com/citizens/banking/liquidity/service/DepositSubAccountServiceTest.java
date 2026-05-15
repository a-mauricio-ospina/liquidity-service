package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.AccountEntity;
import com.citizens.banking.liquidity.domain.CustomerEntity;
import com.citizens.banking.liquidity.domain.DepositEntity;
import com.citizens.banking.liquidity.domain.DepositSubAccountEntity;
import com.citizens.banking.liquidity.dto.CreateDepositSubAccountRequest;
import com.citizens.banking.liquidity.dto.DepositSubAccountResponse;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.exception.DepositSubAccountNotFoundException;
import com.citizens.banking.liquidity.repository.DepositRepository;
import com.citizens.banking.liquidity.repository.DepositSubAccountRepository;
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
class DepositSubAccountServiceTest {

    @Mock
    private DepositSubAccountRepository depositSubAccountRepository;

    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositSubAccountService depositSubAccountService;

    private DepositEntity buildDeposit() {
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

        AccountEntity account = AccountEntity.builder()
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

        return DepositEntity.builder()
                .depositId(100L)
                .account(account)
                .dpfRefId("DPF-2026-00001")
                .depositAmount(new BigDecimal("50000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private DepositSubAccountEntity buildEntity() {
        return DepositSubAccountEntity.builder()
                .depositSubAccountId(200L)
                .deposit(buildDeposit())
                .partyName("John Doe")
                .share(new BigDecimal("50.00"))
                .rate(new BigDecimal("4.25"))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void findAll_returnsListOfDepositSubAccountResponses() {
        when(depositSubAccountRepository.findAll()).thenReturn(List.of(buildEntity()));

        List<DepositSubAccountResponse> result = depositSubAccountService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepositSubAccountId()).isEqualTo(200L);
        assertThat(result.get(0).getDepositId()).isEqualTo(100L);
        assertThat(result.get(0).getPartyName()).isEqualTo("John Doe");
    }

    @Test
    void findAll_returnsEmptyList_whenNoDepositSubAccounts() {
        when(depositSubAccountRepository.findAll()).thenReturn(List.of());

        List<DepositSubAccountResponse> result = depositSubAccountService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsDepositSubAccountResponse_whenFound() {
        when(depositSubAccountRepository.findById(200L)).thenReturn(Optional.of(buildEntity()));

        DepositSubAccountResponse result = depositSubAccountService.findById(200L);

        assertThat(result.getDepositSubAccountId()).isEqualTo(200L);
        assertThat(result.getShare()).isEqualByComparingTo("50.00");
        assertThat(result.getRate()).isEqualByComparingTo("4.25");
    }

    @Test
    void findById_throwsDepositSubAccountNotFoundException_whenNotFound() {
        when(depositSubAccountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> depositSubAccountService.findById(99L))
                .isInstanceOf(DepositSubAccountNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEntityAndReturnsResponse() {
        when(depositRepository.findById(100L)).thenReturn(Optional.of(buildDeposit()));
        when(depositSubAccountRepository.save(any(DepositSubAccountEntity.class))).thenReturn(buildEntity());

        CreateDepositSubAccountRequest request = CreateDepositSubAccountRequest.builder()
                .depositId(100L)
                .partyName("John Doe")
                .share(new BigDecimal("50.00"))
                .rate(new BigDecimal("4.25"))
                .build();

        DepositSubAccountResponse result = depositSubAccountService.create(request);

        assertThat(result.getDepositSubAccountId()).isEqualTo(200L);
        assertThat(result.getPartyName()).isEqualTo("John Doe");
        assertThat(result.getDepositId()).isEqualTo(100L);
        verify(depositSubAccountRepository).save(any(DepositSubAccountEntity.class));
    }

    @Test
    void create_throwsDepositNotFoundException_whenDepositNotFound() {
        when(depositRepository.findById(99L)).thenReturn(Optional.empty());

        CreateDepositSubAccountRequest request = CreateDepositSubAccountRequest.builder()
                .depositId(99L)
                .partyName("John Doe")
                .share(new BigDecimal("50.00"))
                .rate(new BigDecimal("4.25"))
                .build();

        assertThatThrownBy(() -> depositSubAccountService.create(request))
                .isInstanceOf(DepositNotFoundException.class)
                .hasMessageContaining("99");
    }
}
