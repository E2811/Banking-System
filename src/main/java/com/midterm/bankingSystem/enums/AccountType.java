package com.midterm.bankingSystem.enums;

public enum AccountType {
    CHECKING("checking"),
    SAVING("saving"),
    CREDIT_CARD("creditcard"),
    STUDENT_CHECKING("studentchecking");

    private final String type;

    AccountType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return  type;
    }
}
