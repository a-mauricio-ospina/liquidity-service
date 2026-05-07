package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.DepositResponse;
import com.citizens.banking.liquidity.service.DepositService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @GetMapping
    public ResponseEntity<List<DepositResponse>> getAllDeposits() {
        log.debug("Request received: list all deposits");
        List<DepositResponse> deposits = depositService.findAll();
        log.debug("Returning {} deposits", deposits.size());
        return ResponseEntity.ok(deposits);
    }

    @GetMapping("/{depositId}")
    public ResponseEntity<DepositResponse> getDepositById(@PathVariable Long depositId) {
        log.debug("Request received: get deposit depositId={}", depositId);
        DepositResponse deposit = depositService.findById(depositId);
        log.debug("Returning deposit depositId={}", depositId);
        return ResponseEntity.ok(deposit);
    }
}
