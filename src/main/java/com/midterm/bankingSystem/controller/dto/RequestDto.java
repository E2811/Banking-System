package com.midterm.bankingSystem.controller.dto;

import com.midterm.bankingSystem.enums.AccountType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RequestDto {
    @NotNull
    private BigDecimal amount;
    @NotNull
    private Integer accountId;
    @NotNull
    private AccountType typeAccount;
    @NotNull
    private String action;
    private String secretKey;

    public RequestDto(@NotNull BigDecimal amount, @NotNull Integer accountId, @NotNull AccountType typeAccount, @NotNull String action, String secretKey) {
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

    public void setTypeAccount(AccountType typeAccount) {
        this.typeAccount = typeAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public AccountType getTypeAccount() {
        return typeAccount;
    }
}
