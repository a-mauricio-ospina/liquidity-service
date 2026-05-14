package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.CreateCustomerRequest;
import com.citizens.banking.liquidity.dto.CustomerResponse;
import com.citizens.banking.liquidity.exception.CustomerNotFoundException;
import com.citizens.banking.liquidity.exception.GlobalExceptionHandler;
import com.citizens.banking.liquidity.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerResponse buildResponse() {
        return CustomerResponse.builder()
                .customerId(1L)
                .customerName("Acme Corporation")
                .customerType("CORPORATE")
                .status("ACTIVE")
                .rmId(501L)
                .channel("DIGITAL")
                .region("NORTHEAST")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getAllCustomers_returns200WithList() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].customerId").value(1))
                .andExpect(jsonPath("$[0].customerName").value("Acme Corporation"))
                .andExpect(jsonPath("$[0].customerType").value("CORPORATE"));
    }

    @Test
    void getCustomerById_returns200_whenFound() throws Exception {
        when(customerService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getCustomerById_returns404_whenNotFound() throws Exception {
        when(customerService.findById(99L)).thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Customer not found with id: 99"));
    }

    @Test
    void createCustomer_returns201WithBody_whenValid() throws Exception {
        CreateCustomerRequest request = CreateCustomerRequest.builder()
                .customerName("Acme Corporation")
                .customerType("CORPORATE")
                .status("ACTIVE")
                .rmId(501L)
                .channel("DIGITAL")
                .region("NORTHEAST")
                .build();

        when(customerService.create(any(CreateCustomerRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.customerName").value("Acme Corporation"));
    }

    @Test
    void createCustomer_returns400_whenValidationFails() throws Exception {
        String invalidPayload = """
                {
                  "customerName": "",
                  "customerType": "",
                  "status": "ACTIVE",
                  "rmId": -1,
                  "channel": "DIGITAL",
                  "region": "NORTHEAST"
                }
                """;

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }
}
