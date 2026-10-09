package com.bank.bankapplication.controller;

import com.bank.bankapplication.dto.Request.LoginRequest;
import com.bank.bankapplication.dto.Request.LoginResponse;
import com.bank.bankapplication.dto.Request.RegistrationRequest;
import com.bank.bankapplication.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Test
    @DisplayName("Should return 201 Created on successful registration")
    void testRegister_Success() {
        RegistrationRequest request = RegistrationRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .mobileNumber("9876543210")
                .password("Password@123")
                .build();

        when(authenticationService.register(request)).thenReturn("User registered successfully");

        ResponseEntity<String> response = authenticationController.register(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
        verify(authenticationService, times(1)).register(request);
    }

    @Test
    @DisplayName("Should return 200 OK on successful login")
    void testLogin_Success() {
        LoginRequest request = LoginRequest.builder()
                .email("john@example.com")
                .password("Password@123")
                .build();

        LoginResponse loginResponse = LoginResponse.builder()
                .message("Login successful")
                .userId(1L)
                .email("john@example.com")
                .role("ROLE_USER")
                .token("mockToken")
                .build();

        when(authenticationService.login(request)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = authenticationController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Login successful", response.getBody().getMessage());
        assertEquals("mockToken", response.getBody().getToken());
        verify(authenticationService, times(1)).login(request);
    }
}
