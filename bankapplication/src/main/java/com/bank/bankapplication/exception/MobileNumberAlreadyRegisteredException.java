package com.bank.bankapplication.exception;

public class MobileNumberAlreadyRegisteredException extends RuntimeException {
    public MobileNumberAlreadyRegisteredException(String message) {
        super(message);
    }
}
