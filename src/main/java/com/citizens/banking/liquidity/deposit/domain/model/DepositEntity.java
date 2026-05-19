package com.citizens.banking.liquidity.deposit.domain.model;

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
@Table(name = "deposit")
public class DepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    private Long depositId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "dpf_ref_id", nullable = false, length = 100)
    private String dpfRefId;

    @Column(name = "deposit_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal depositAmount;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime updatedAt;
}
