package com.midterm.bankingSystem.exception;

public class UserWithNameUsed extends RuntimeException {
    public UserWithNameUsed(String message) {
        super(message);
    }
}
