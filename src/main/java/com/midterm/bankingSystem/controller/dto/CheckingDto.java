package com.midterm.bankingSystem.controller.dto;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Money;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CheckingDto {
    @NotNull
    private Money balance;
    @Valid
    private AccountHolder primaryOwner;
    @Valid
    private  AccountHolder secondaryOwner;
    @NotNull
    private String secretKey;


    public CheckingDto() {
    }

    public CheckingDto(@NotNull String balance, AccountHolder primaryOwner, AccountHolder secondaryOwner,  @NotNull String secretKey) {
        this.balance = new Money(new BigDecimal(balance));
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.secretKey = secretKey;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }

    public AccountHolder getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public AccountHolder getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
