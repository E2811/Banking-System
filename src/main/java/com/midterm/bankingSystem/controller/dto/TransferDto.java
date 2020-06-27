package com.midterm.bankingSystem.controller.dto;

import com.midterm.bankingSystem.enums.AccountType;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class TransferDto {
    @NotNull
    private Integer Id;
    @NotNull
    private String name;
    @NotNull
    private Integer accountId;
    @DecimalMin("0")
    private BigDecimal amount;
    @NotNull
    private AccountType typeOwnAccount;
    @NotNull
    private AccountType typeReceiptAccount;

    public TransferDto(@NotNull Integer id, @NotNull String name, @NotNull Integer accountId, @DecimalMin("0") BigDecimal amount, @NotNull AccountType typeOwnAccount, @NotNull AccountType typeReceiptAccount) {
        this.Id = id;
        this.name = name;
        this.accountId = accountId;
        this.amount = amount;
        this.typeOwnAccount = typeOwnAccount;
        this.typeReceiptAccount = typeReceiptAccount;
    }

    public AccountType getTypeOwnAccount() {
        return typeOwnAccount;
    }

    public AccountType getTypeReceiptAccount() {
        return typeReceiptAccount;
    }

    public String getName() {
        return name;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getId() {
        return Id;
    }

}
