package com.midterm.bankingSystem.exception;

public class NotEnoughData extends RuntimeException {
    public NotEnoughData(String message) {
        super(message);
    }
}
