package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.CreateMarketRateVersionRequest;
import com.citizens.banking.liquidity.dto.MarketRateVersionResponse;
import com.citizens.banking.liquidity.exception.GlobalExceptionHandler;
import com.citizens.banking.liquidity.exception.MarketRateVersionNotFoundException;
import com.citizens.banking.liquidity.service.MarketRateVersionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarketRateVersionController.class)
@Import(GlobalExceptionHandler.class)
class MarketRateVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MarketRateVersionService marketRateVersionService;

    private MarketRateVersionResponse buildResponse() {
        return MarketRateVersionResponse.builder()
                .rateVersionId(1L)
                .baseRate(new BigDecimal("4.00"))
                .spread(new BigDecimal("0.25"))
                .allInRate(new BigDecimal("4.25"))
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTill(null)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getAllMarketRateVersions_returns200WithList() throws Exception {
        when(marketRateVersionService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/market-rate-versions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].rateVersionId").value(1))
                .andExpect(jsonPath("$[0].baseRate").value(4.00))
                .andExpect(jsonPath("$[0].allInRate").value(4.25));
    }

    @Test
    void getMarketRateVersionById_returns200_whenFound() throws Exception {
        when(marketRateVersionService.findById(1L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/market-rate-versions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rateVersionId").value(1))
                .andExpect(jsonPath("$.spread").value(0.25));
    }

    @Test
    void getMarketRateVersionById_returns404_whenNotFound() throws Exception {
        when(marketRateVersionService.findById(99L)).thenThrow(new MarketRateVersionNotFoundException(99L));

        mockMvc.perform(get("/api/v1/market-rate-versions/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("MarketRateVersion not found with id: 99"));
    }

    @Test
    void createMarketRateVersion_returns201WithBody_whenValid() throws Exception {
        CreateMarketRateVersionRequest request = CreateMarketRateVersionRequest.builder()
                .baseRate(new BigDecimal("4.00"))
                .spread(new BigDecimal("0.25"))
                .allInRate(new BigDecimal("4.25"))
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();

        when(marketRateVersionService.create(any(CreateMarketRateVersionRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/market-rate-versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rateVersionId").value(1))
                .andExpect(jsonPath("$.allInRate").value(4.25));
    }

    @Test
    void createMarketRateVersion_returns400_whenValidationFails() throws Exception {
        String invalidPayload = """
                {
                  "baseRate": null,
                  "spread": -1,
                  "allInRate": null,
                  "effectiveFrom": null
                }
                """;

        mockMvc.perform(post("/api/v1/market-rate-versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }
}
