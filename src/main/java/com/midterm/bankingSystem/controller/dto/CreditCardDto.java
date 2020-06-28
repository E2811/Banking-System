package com.midterm.bankingSystem.controller.dto;

import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Money;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CreditCardDto {

    @NotNull
    @Valid
    private Money balance;
    @Valid
    private AccountHolder primaryOwner;
    @Valid
    private  AccountHolder secondaryOwner;
    @NotNull
    private String secretKey;

    @DecimalMax("100000")
    @DecimalMin("100")
    private BigDecimal creditLimit;

    @DecimalMin("0.1")
    @DecimalMax("0.2")
    private BigDecimal interestRate;

    public CreditCardDto() {
    }

    public CreditCardDto(@NotNull @Valid Money balance, AccountHolder primaryOwner, AccountHolder secondaryOwner, @NotNull String secretKey, @DecimalMax("100000") @DecimalMin("100") BigDecimal creditLimit, @DecimalMin("0.1") @DecimalMax("0.2") BigDecimal interestRate) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.secretKey = secretKey;
        this.creditLimit = creditLimit;
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

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
