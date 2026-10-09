package com.bank.bankapplication.exception;

public class EmailAlreadyRegisteredException extends  RuntimeException{
    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }
}
