package com.midterm.bankingSystem.enums;

public enum Status {
    FROZEN("Frozen"),
    ACTIVE("Active");
    private final String status;

    Status(String status) {
        this.status = status;
    }
}
