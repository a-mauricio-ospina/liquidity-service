package com.citizens.banking.liquidity.service;

import com.citizens.banking.liquidity.domain.MarketRateVersionEntity;
import com.citizens.banking.liquidity.dto.CreateMarketRateVersionRequest;
import com.citizens.banking.liquidity.dto.MarketRateVersionResponse;
import com.citizens.banking.liquidity.exception.MarketRateVersionNotFoundException;
import com.citizens.banking.liquidity.repository.MarketRateVersionRepository;
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
class MarketRateVersionServiceTest {

    @Mock
    private MarketRateVersionRepository marketRateVersionRepository;

    @InjectMocks
    private MarketRateVersionService marketRateVersionService;

    private MarketRateVersionEntity buildEntity() {
        return MarketRateVersionEntity.builder()
                .rateVersionId(1L)
                .baseRate(new BigDecimal("4.00"))
                .spread(new BigDecimal("0.25"))
                .allInRate(new BigDecimal("4.25"))
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTill(null)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void findAll_returnsListOfMarketRateVersionResponses() {
        when(marketRateVersionRepository.findAll()).thenReturn(List.of(buildEntity()));

        List<MarketRateVersionResponse> result = marketRateVersionService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRateVersionId()).isEqualTo(1L);
        assertThat(result.get(0).getBaseRate()).isEqualByComparingTo("4.00");
        assertThat(result.get(0).getAllInRate()).isEqualByComparingTo("4.25");
    }

    @Test
    void findAll_returnsEmptyList_whenNoVersions() {
        when(marketRateVersionRepository.findAll()).thenReturn(List.of());

        List<MarketRateVersionResponse> result = marketRateVersionService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findById_returnsMarketRateVersionResponse_whenFound() {
        when(marketRateVersionRepository.findById(1L)).thenReturn(Optional.of(buildEntity()));

        MarketRateVersionResponse result = marketRateVersionService.findById(1L);

        assertThat(result.getRateVersionId()).isEqualTo(1L);
        assertThat(result.getSpread()).isEqualByComparingTo("0.25");
        assertThat(result.getEffectiveFrom()).isEqualTo(LocalDate.of(2026, 1, 1));
    }

    @Test
    void findById_throwsMarketRateVersionNotFoundException_whenNotFound() {
        when(marketRateVersionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> marketRateVersionService.findById(99L))
                .isInstanceOf(MarketRateVersionNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesEntityAndReturnsResponse() {
        when(marketRateVersionRepository.save(any(MarketRateVersionEntity.class))).thenReturn(buildEntity());

        CreateMarketRateVersionRequest request = CreateMarketRateVersionRequest.builder()
                .baseRate(new BigDecimal("4.00"))
                .spread(new BigDecimal("0.25"))
                .allInRate(new BigDecimal("4.25"))
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();

        MarketRateVersionResponse result = marketRateVersionService.create(request);

        assertThat(result.getRateVersionId()).isEqualTo(1L);
        assertThat(result.getBaseRate()).isEqualByComparingTo("4.00");
        assertThat(result.getAllInRate()).isEqualByComparingTo("4.25");
        verify(marketRateVersionRepository).save(any(MarketRateVersionEntity.class));
    }
}
