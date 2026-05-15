package com.citizens.banking.liquidity.customer.api;

import com.citizens.banking.liquidity.customer.application.service.CustomerService;
import com.citizens.banking.liquidity.customer.dto.CreateCustomerRequest;
import com.citizens.banking.liquidity.customer.dto.CustomerResponse;
import com.citizens.banking.liquidity.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Customers", description = "Operations for managing banking customers")
@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "List all customers", description = "Returns a list of all customer records in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customers retrieved successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        log.debug("Request received: list all customers");
        List<CustomerResponse> customers = customerService.findAll();
        log.debug("Returning {} customers", customers.size());
        return ResponseEntity.ok(customers);
    }

    @Operation(summary = "Get customer by ID", description = "Returns a single customer record by its unique identifier")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomerById(
            @Parameter(description = "Unique identifier of the customer", required = true)
            @PathVariable Long customerId) {
        log.debug("Request received: get customer customerId={}", customerId);
        CustomerResponse customer = customerService.findById(customerId);
        log.debug("Returning customer customerId={}", customerId);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Create a new customer", description = "Creates a new customer record. Validates the request payload before processing.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Customer created successfully",
            content = @Content(schema = @Schema(implementation = CustomerResponse.class))),
        @ApiResponse(responseCode = "400", description = "Validation failed",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @RequestBody(description = "Customer creation payload", required = true,
                content = @Content(schema = @Schema(implementation = CreateCustomerRequest.class)))
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateCustomerRequest request) {

        log.debug("Request received: create customer name={}, type={}", request.getCustomerName(), request.getCustomerType());
        CustomerResponse created = customerService.create(request);
        log.debug("Customer created customerId={}", created.getCustomerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
