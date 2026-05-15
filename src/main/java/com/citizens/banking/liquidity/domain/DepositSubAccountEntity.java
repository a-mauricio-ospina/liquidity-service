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
@Table(name = "deposit_sub_account")
public class DepositSubAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_sub_account_id")
    private Long depositSubAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deposit_id", nullable = false)
    private DepositEntity deposit;

    @Column(name = "party_name", nullable = false, length = 255)
    private String partyName;

    @Column(name = "share", nullable = false, precision = 5, scale = 2)
    private BigDecimal share;

    @Column(name = "rate", nullable = false, precision = 18, scale = 2)
    private BigDecimal rate;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;
}
