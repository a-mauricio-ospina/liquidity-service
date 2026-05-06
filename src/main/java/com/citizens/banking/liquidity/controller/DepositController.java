package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.DepositResponse;
import com.citizens.banking.liquidity.service.DepositService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {

    private final DepositService depositService;

    @GetMapping
    public ResponseEntity<List<DepositResponse>> getAllDeposits() {
        return ResponseEntity.ok(depositService.findAll());
    }

    @GetMapping("/{depositId}")
    public ResponseEntity<DepositResponse> getDepositById(@PathVariable String depositId) {
        return ResponseEntity.ok(depositService.findById(depositId));
    }
}
