package com.citizens.banking.liquidity.deposit.application.service;

import com.citizens.banking.liquidity.account.domain.model.AccountEntity;
import com.citizens.banking.liquidity.customer.domain.model.CustomerEntity;
import com.citizens.banking.liquidity.deposit.domain.model.DepositEntity;
import com.citizens.banking.liquidity.deposit.domain.model.DepositRateEntity;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositRateRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositRateResponse;
import com.citizens.banking.liquidity.deposit.infrastructure.repository.DepositRateRepository;
import com.citizens.banking.liquidity.deposit.infrastructure.repository.DepositRepository;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.exception.DepositRateNotFoundException;
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
class DepositRateServiceTest {

    @Mock
    private DepositRateRepository depositRateRepository;

    @Mock
    private DepositRepository depositRepository;

    @InjectMocks
    private DepositRateService depositRateService;

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

    private DepositRateEntity buildEntity() {
        return DepositRateEntity.builder()
                .depositRateId(500L)
                .deposit(buildDeposit())
                .rateVersionId(1L)
                .allInRate(new BigDecimal("4.25"))
                .status("ACTIVE")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void findAll_returnsListOfDepositRateResponses() {
        when(depositRateRepository.findAll()).thenReturn(List.of(buildEntity()));

        List<DepositRateResponse> result = depositRateService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepositRateId()).isEqualTo(500L);
        assertThat(result.get(0).getDepositId()).isEqualTo(100L);
        assertThat(result.get(0).getRateVersionId()).isEqualTo(1L);
    }

    @Test
    void findAll_returnsEmptyList_whenNoDepositRates() {
        when(depositRateRepository.findAll()).thenReturn(List.of());

        List<DepositRateResponse> result = depositRateService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsDepositRateResponse_whenFound() {
        when(depositRateRepository.findById(500L)).thenReturn(Optional.of(buildEntity()));

        DepositRateResponse result = depositRateService.findById(500L);

        assertThat(result.getDepositRateId()).isEqualTo(500L);
        assertThat(result.getAllInRate()).isEqualByComparingTo("4.25");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }

    @Test
    void findById_throwsDepositRateNotFoundException_whenNotFound() {
        when(depositRateRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> depositRateService.findById(99L))
                .isInstanceOf(DepositRateNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEntityAndReturnsResponse() {
        when(depositRepository.findById(100L)).thenReturn(Optional.of(buildDeposit()));
        when(depositRateRepository.save(any(DepositRateEntity.class))).thenReturn(buildEntity());

        CreateDepositRateRequest request = CreateDepositRateRequest.builder()
                .depositId(100L)
                .rateVersionId(1L)
                .allInRate(new BigDecimal("4.25"))
                .status("ACTIVE")
                .build();

        DepositRateResponse result = depositRateService.create(request);

        assertThat(result.getDepositRateId()).isEqualTo(500L);
        assertThat(result.getDepositId()).isEqualTo(100L);
        assertThat(result.getRateVersionId()).isEqualTo(1L);
        verify(depositRateRepository).save(any(DepositRateEntity.class));
    }

    @Test
    void create_throwsDepositNotFoundException_whenDepositNotFound() {
        when(depositRepository.findById(99L)).thenReturn(Optional.empty());

        CreateDepositRateRequest request = CreateDepositRateRequest.builder()
                .depositId(99L)
                .rateVersionId(1L)
                .allInRate(new BigDecimal("4.25"))
                .status("ACTIVE")
                .build();

        assertThatThrownBy(() -> depositRateService.create(request))
                .isInstanceOf(DepositNotFoundException.class)
                .hasMessageContaining("99");
    }
}
