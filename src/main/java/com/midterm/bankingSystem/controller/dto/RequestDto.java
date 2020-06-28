package com.midterm.bankingSystem.controller.dto;

import com.midterm.bankingSystem.enums.AccountType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RequestDto {
    @DecimalMin("0")
    private BigDecimal amount;
    @NotNull
    private Integer accountId;
    @NotNull
    private AccountType typeAccount;
    @NotNull
    private String action;
    private String secretKey;

    public RequestDto(@DecimalMin("0") BigDecimal amount, @NotNull Integer accountId, @NotNull AccountType typeAccount, @NotNull String action, String secretKey) {
        this.amount = amount;
        this.accountId = accountId;
        this.typeAccount = typeAccount;
        this.action = action;
        this.secretKey = secretKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAction() {
        return action;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public AccountType getTypeAccount() {
        return typeAccount;
    }
}
