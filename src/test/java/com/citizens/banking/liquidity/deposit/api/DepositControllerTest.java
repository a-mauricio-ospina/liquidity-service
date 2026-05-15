package com.citizens.banking.liquidity.deposit.api;

import com.citizens.banking.liquidity.deposit.application.orchestration.DepositWorkflowOrchestrator;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositResponse;
import com.citizens.banking.liquidity.exception.DepositNotFoundException;
import com.citizens.banking.liquidity.exception.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepositController.class)
@Import(GlobalExceptionHandler.class)
class DepositControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepositWorkflowOrchestrator depositWorkflowOrchestrator;

    private DepositResponse buildResponse() {
        return DepositResponse.builder()
                .depositId(100L)
                .accountId(10L)
                .dpfRefId("DPF-2026-00001")
                .depositAmount(new BigDecimal("50000.00"))
                .currency("USD")
                .status("ACTIVE")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getAllDeposits_returns200WithList() throws Exception {
        when(depositWorkflowOrchestrator.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/deposits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].depositId").value(100))
                .andExpect(jsonPath("$[0].dpfRefId").value("DPF-2026-00001"))
                .andExpect(jsonPath("$[0].currency").value("USD"));
    }

    @Test
    void getDepositById_returns200_whenFound() throws Exception {
        when(depositWorkflowOrchestrator.findById(100L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/deposits/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depositId").value(100))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getDepositById_returns404_whenNotFound() throws Exception {
        when(depositWorkflowOrchestrator.findById(99L)).thenThrow(new DepositNotFoundException(99L));

        mockMvc.perform(get("/api/v1/deposits/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Deposit not found with id: 99"));
    }

    @Test
    void createDeposit_returns201WithBody_whenValid() throws Exception {
        CreateDepositRequest request = CreateDepositRequest.builder()
                .accountId(10L)
                .dpfRefId("DPF-2026-00001")
                .depositAmount(new BigDecimal("50000.00"))
                .currency("USD")
                .status("ACTIVE")
                .build();

        when(depositWorkflowOrchestrator.createDeposit(any(CreateDepositRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.depositId").value(100))
                .andExpect(jsonPath("$.dpfRefId").value("DPF-2026-00001"));
    }

    @Test
    void createDeposit_returns400_whenValidationFails() throws Exception {
        String invalidPayload = """
                {
                  "accountId": null,
                  "dpfRefId": "",
                  "depositAmount": -1,
                  "currency": "TOOLONG",
                  "status": ""
                }
                """;

        mockMvc.perform(post("/api/v1/deposits")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }
}
