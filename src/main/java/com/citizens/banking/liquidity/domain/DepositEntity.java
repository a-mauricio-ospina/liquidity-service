package com.citizens.banking.liquidity.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "deposits")
public class DepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deposit_id")
    private Long depositId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "deposit_type", nullable = false)
    private String depositType;

    @Column(name = "principal_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal principalAmount;

    @Column(name = "interest_rate", nullable = false, precision = 18, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "accrued_interest", precision = 18, scale = 2)
    private BigDecimal accruedInterest;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "status", nullable = false)
    private String status;
}
