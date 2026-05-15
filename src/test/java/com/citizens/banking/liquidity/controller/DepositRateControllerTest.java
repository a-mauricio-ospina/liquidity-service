package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.CreateDepositRateRequest;
import com.citizens.banking.liquidity.dto.DepositRateResponse;
import com.citizens.banking.liquidity.exception.DepositRateNotFoundException;
import com.citizens.banking.liquidity.exception.GlobalExceptionHandler;
import com.citizens.banking.liquidity.service.DepositRateService;
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

@WebMvcTest(DepositRateController.class)
@Import(GlobalExceptionHandler.class)
class DepositRateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepositRateService depositRateService;

    private DepositRateResponse buildResponse() {
        return DepositRateResponse.builder()
                .depositRateId(500L)
                .depositId(100L)
                .rateVersionId(1L)
                .allInRate(new BigDecimal("4.25"))
                .status("ACTIVE")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getAllDepositRates_returns200WithList() throws Exception {
        when(depositRateService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/deposit-rates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].depositRateId").value(500))
                .andExpect(jsonPath("$[0].depositId").value(100))
                .andExpect(jsonPath("$[0].rateVersionId").value(1));
    }

    @Test
    void getDepositRateById_returns200_whenFound() throws Exception {
        when(depositRateService.findById(500L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/deposit-rates/500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depositRateId").value(500))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getDepositRateById_returns404_whenNotFound() throws Exception {
        when(depositRateService.findById(99L)).thenThrow(new DepositRateNotFoundException(99L));

        mockMvc.perform(get("/api/v1/deposit-rates/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("DepositRate not found with id: 99"));
    }

    @Test
    void createDepositRate_returns201WithBody_whenValid() throws Exception {
        CreateDepositRateRequest request = CreateDepositRateRequest.builder()
                .depositId(100L)
                .rateVersionId(1L)
                .allInRate(new BigDecimal("4.25"))
                .status("ACTIVE")
                .build();

        when(depositRateService.create(any(CreateDepositRateRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/deposit-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.depositRateId").value(500))
                .andExpect(jsonPath("$.rateVersionId").value(1));
    }

    @Test
    void createDepositRate_returns400_whenValidationFails() throws Exception {
        String invalidPayload = """
                {
                  "depositId": null,
                  "rateVersionId": null,
                  "allInRate": -1,
                  "status": ""
                }
                """;

        mockMvc.perform(post("/api/v1/deposit-rates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }
}
