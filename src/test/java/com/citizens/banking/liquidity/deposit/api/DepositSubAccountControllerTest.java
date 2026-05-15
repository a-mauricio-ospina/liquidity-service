package com.citizens.banking.liquidity.deposit.api;

import com.citizens.banking.liquidity.deposit.application.service.DepositSubAccountService;
import com.citizens.banking.liquidity.deposit.dto.request.CreateDepositSubAccountRequest;
import com.citizens.banking.liquidity.deposit.dto.response.DepositSubAccountResponse;
import com.citizens.banking.liquidity.exception.DepositSubAccountNotFoundException;
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

@WebMvcTest(DepositSubAccountController.class)
@Import(GlobalExceptionHandler.class)
class DepositSubAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepositSubAccountService depositSubAccountService;

    private DepositSubAccountResponse buildResponse() {
        return DepositSubAccountResponse.builder()
                .depositSubAccountId(200L)
                .depositId(100L)
                .partyName("John Doe")
                .share(new BigDecimal("50.00"))
                .rate(new BigDecimal("4.25"))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getAllDepositSubAccounts_returns200WithList() throws Exception {
        when(depositSubAccountService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/deposit-sub-accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].depositSubAccountId").value(200))
                .andExpect(jsonPath("$[0].partyName").value("John Doe"))
                .andExpect(jsonPath("$[0].depositId").value(100));
    }

    @Test
    void getDepositSubAccountById_returns200_whenFound() throws Exception {
        when(depositSubAccountService.findById(200L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/deposit-sub-accounts/200"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.depositSubAccountId").value(200))
                .andExpect(jsonPath("$.partyName").value("John Doe"));
    }

    @Test
    void getDepositSubAccountById_returns404_whenNotFound() throws Exception {
        when(depositSubAccountService.findById(99L)).thenThrow(new DepositSubAccountNotFoundException(99L));

        mockMvc.perform(get("/api/v1/deposit-sub-accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("DepositSubAccount not found with id: 99"));
    }

    @Test
    void createDepositSubAccount_returns201WithBody_whenValid() throws Exception {
        CreateDepositSubAccountRequest request = CreateDepositSubAccountRequest.builder()
                .depositId(100L)
                .partyName("John Doe")
                .share(new BigDecimal("50.00"))
                .rate(new BigDecimal("4.25"))
                .build();

        when(depositSubAccountService.create(any(CreateDepositSubAccountRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/deposit-sub-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.depositSubAccountId").value(200))
                .andExpect(jsonPath("$.partyName").value("John Doe"));
    }

    @Test
    void createDepositSubAccount_returns400_whenValidationFails() throws Exception {
        String invalidPayload = """
                {
                  "depositId": null,
                  "partyName": "",
                  "share": -1,
                  "rate": null
                }
                """;

        mockMvc.perform(post("/api/v1/deposit-sub-accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }
}
