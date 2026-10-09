package com.bank.bankapplication.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    @DisplayName("Should handle EmailAlreadyRegisteredException and return 409 CONFLICT")
    void testHandleEmailAlreadyRegistered() {
        EmailAlreadyRegisteredException exception = new EmailAlreadyRegisteredException("Email already registered");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleEmailAlreadyRegistered(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Email Already Registered", response.getBody().get("error"));
        assertEquals("Email already registered", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle MobileNumberAlreadyRegisteredException and return 409 CONFLICT")
    void testHandleMobileNumberAlreadyRegistered() {
        MobileNumberAlreadyRegisteredException exception = new MobileNumberAlreadyRegisteredException("Mobile number already registered");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleMobileNumberAlreadyRegistered(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Mobile Number Already Registered", response.getBody().get("error"));
        assertEquals("Mobile number already registered", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }

    @Test
    @DisplayName("Should handle generic RuntimeException and return 400 BAD_REQUEST")
    void testHandleRuntimeException() {
        RuntimeException exception = new RuntimeException("Invalid email or password");

        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleRuntimeException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Bad Request", response.getBody().get("error"));
        assertEquals("Invalid email or password", response.getBody().get("message"));
        assertNotNull(response.getBody().get("timestamp"));
    }
}
