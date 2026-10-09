package com.bank.bankapplication.service;

import com.bank.bankapplication.dto.Request.LoginRequest;
import com.bank.bankapplication.dto.Request.LoginResponse;
import com.bank.bankapplication.dto.Request.RegistrationRequest;

public interface AuthenticationService {
    public  String register(RegistrationRequest registrationRequest);
    LoginResponse login(LoginRequest loginRequest);
}
