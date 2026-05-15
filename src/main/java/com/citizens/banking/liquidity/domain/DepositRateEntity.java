package com.citizens.banking.liquidity.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "deposit_rate")
public class DepositRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_rate_id")
    private Long depositRateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id", nullable = false)
    private DepositEntity deposit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rate_version_id", nullable = false)
    private MarketRateVersionEntity marketRateVersion;

    @Column(name = "all_in_rate", nullable = false, precision = 18, scale = 2)
    private BigDecimal allInRate;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;
}
