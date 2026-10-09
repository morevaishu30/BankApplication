package com.bank.bankapplication.service;
import com.bank.bankapplication.dto.Request.CreateAccountRequest;
import com.bank.bankapplication.dto.Response.BankAccountResponse;
public interface BankAccountService {
    BankAccountResponse createAccount(CreateAccountRequest request, String email);
}
