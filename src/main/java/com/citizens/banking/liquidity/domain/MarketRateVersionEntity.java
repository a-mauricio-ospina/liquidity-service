package com.citizens.banking.liquidity.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "market_rate_version")
public class MarketRateVersionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rate_version_id")
    private Long rateVersionId;

    @Column(name = "base_rate", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "spread", nullable = false, precision = 18, scale = 2)
    private BigDecimal spread;

    @Column(name = "all_in_rate", nullable = false, precision = 18, scale = 2)
    private BigDecimal allInRate;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_till")
    private LocalDate effectiveTill;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;
}
