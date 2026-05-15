package com.citizens.banking.liquidity.common;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "Application liveness check")
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    @Operation(summary = "Health check", description = "Returns the operational status of the liquidity service")
    @ApiResponse(responseCode = "200", description = "Service is up and running",
        content = @Content(schema = @Schema(implementation = HealthResponse.class)))
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
