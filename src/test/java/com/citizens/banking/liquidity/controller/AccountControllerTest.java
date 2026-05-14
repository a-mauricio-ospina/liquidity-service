package com.citizens.banking.liquidity.controller;

import com.citizens.banking.liquidity.dto.AccountResponse;
import com.citizens.banking.liquidity.dto.CreateAccountRequest;
import com.citizens.banking.liquidity.exception.AccountNotFoundException;
import com.citizens.banking.liquidity.exception.GlobalExceptionHandler;
import com.citizens.banking.liquidity.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService accountService;

    private AccountResponse buildResponse() {
        return AccountResponse.builder()
                .accountId(10L)
                .customerId(1L)
                .accountNumber("ACC-0001-2026")
                .accountType("CHECKING")
                .currency("USD")
                .status("ACTIVE")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .effectiveTill(null)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void getAllAccounts_returns200WithList() throws Exception {
        when(accountService.findAll()).thenReturn(List.of(buildResponse()));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(10))
                .andExpect(jsonPath("$[0].accountNumber").value("ACC-0001-2026"))
                .andExpect(jsonPath("$[0].currency").value("USD"));
    }

    @Test
    void getAccountById_returns200_whenFound() throws Exception {
        when(accountService.findById(10L)).thenReturn(buildResponse());

        mockMvc.perform(get("/api/v1/accounts/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(10))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void getAccountById_returns404_whenNotFound() throws Exception {
        when(accountService.findById(99L)).thenThrow(new AccountNotFoundException(99L));

        mockMvc.perform(get("/api/v1/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Account not found with id: 99"));
    }

    @Test
    void createAccount_returns201WithBody_whenValid() throws Exception {
        CreateAccountRequest request = CreateAccountRequest.builder()
                .customerId(1L)
                .accountNumber("ACC-0001-2026")
                .accountType("CHECKING")
                .currency("USD")
                .status("ACTIVE")
                .effectiveFrom(LocalDate.of(2026, 1, 1))
                .build();

        when(accountService.create(any(CreateAccountRequest.class))).thenReturn(buildResponse());

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(10))
                .andExpect(jsonPath("$.accountNumber").value("ACC-0001-2026"));
    }

    @Test
    void createAccount_returns400_whenValidationFails() throws Exception {
        String invalidPayload = """
                {
                  "customerId": null,
                  "accountNumber": "",
                  "accountType": "",
                  "currency": "TOOLONG",
                  "status": "ACTIVE",
                  "effectiveFrom": null
                }
                """;

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors").isMap());
    }
}
