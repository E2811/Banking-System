package com.midterm.bankingSystem.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Money;

import javax.persistence.ManyToOne;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class SavingDto {
    @NotNull
    private Money balance;
    @Valid
    private AccountHolder primaryOwner;
    @Valid
    private  AccountHolder secondaryOwner;
    @NotNull
    private String secretKey;
    @DecimalMax("1000")
    @DecimalMin("100")
    private BigDecimal minimumBalance;
    @DecimalMax("0.5")
    private BigDecimal interestRate;

    public SavingDto() {
    }

    public SavingDto(@NotNull Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, @NotNull String secretKey, @DecimalMin("100") BigDecimal minimumBalance, @DecimalMax("0.5") BigDecimal interestRate) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
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

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
