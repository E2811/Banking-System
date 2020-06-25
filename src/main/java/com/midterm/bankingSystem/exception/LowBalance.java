package com.midterm.bankingSystem.exception;

public class LowBalance extends RuntimeException {
    public LowBalance(String message) {
        super(message);
    }
}
