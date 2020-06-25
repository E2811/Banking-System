package com.midterm.bankingSystem.controller.dto;

import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Money;
import java.math.BigDecimal;

public class AccountMV {

    protected Integer id;
    protected Money balance;
    protected AccountHolder primaryOwner;
    protected  AccountHolder secondaryOwner;

    public AccountMV() {
    }

    public AccountMV(Money balance, AccountHolder primaryOwner) {
        this.balance = balance;
        this.primaryOwner = primaryOwner;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

}
