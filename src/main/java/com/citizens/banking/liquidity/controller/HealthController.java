package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.HealthResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(
            HealthResponse.builder()
                .status("UP")
                .service("liquidity-service")
                .build()
        );
    }
}
