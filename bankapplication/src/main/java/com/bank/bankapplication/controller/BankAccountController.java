package com.bank.bankapplication.controller;

import com.bank.bankapplication.dto.Request.CreateAccountRequest;
import com.bank.bankapplication.dto.Response.BankAccountResponse;
import com.bank.bankapplication.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request, Authentication authentication) {

        String email = authentication.getName();

        BankAccountResponse response =
                bankAccountService.createAccount(request, email);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
