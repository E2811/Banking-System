package com.midterm.bankingSystem.exception;

public class InvalidAccountUser extends RuntimeException {
    public InvalidAccountUser(String message) {
        super(message);
    }
}
