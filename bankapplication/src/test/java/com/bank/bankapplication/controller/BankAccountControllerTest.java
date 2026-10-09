package com.bank.bankapplication.controller;

import com.bank.bankapplication.dto.Request.CreateAccountRequest;
import com.bank.bankapplication.dto.Response.BankAccountResponse;
import com.bank.bankapplication.service.BankAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountControllerTest {

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private BankAccountController bankAccountController;

    @Test
    @DisplayName("Should return 201 Created on successful bank account creation")
    void testCreateAccount_Success() {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType("SAVINGS");
        request.setInitialDeposit(new BigDecimal("5000.00"));

        BankAccountResponse bankAccountResponse = BankAccountResponse.builder()
                .message("Bank account created successfully")
                .accountId(1L)
                .accountNumber("100012345678")
                .accountType("SAVINGS")
                .balance(new BigDecimal("5000.00"))
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        when(authentication.getName()).thenReturn("testuser@example.com");
        when(bankAccountService.createAccount(request, "testuser@example.com")).thenReturn(bankAccountResponse);

        ResponseEntity<BankAccountResponse> response = bankAccountController.createAccount(request, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Bank account created successfully", response.getBody().getMessage());
        assertEquals("100012345678", response.getBody().getAccountNumber());
        verify(authentication, times(1)).getName();
        verify(bankAccountService, times(1)).createAccount(request, "testuser@example.com");
    }
}
