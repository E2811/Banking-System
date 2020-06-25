package com.midterm.bankingSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JsonIgnore
    private Account senderAccount;
    @ManyToOne
    @JsonIgnore
    private Account receiptAccount;
    private LocalDateTime dateTransaction;
    private BigDecimal amount;

    public Transaction() {
        this.dateTransaction = LocalDateTime.now();
    }

    public Transaction(Account senderAccount, BigDecimal amount) {
        this.senderAccount = senderAccount;
        this.dateTransaction = LocalDateTime.now();
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccount) {
        this.senderAccount = senderAccount;
    }

    public Account getReceiptAccount() {
        return receiptAccount;
    }

    public void setReceiptAccount(Account receiptAccount) {
        this.receiptAccount = receiptAccount;
    }

    public LocalDateTime getDateTransaction() {
        return dateTransaction;
    }

    public void setDateTransaction(LocalDateTime dateTransaction) {
        this.dateTransaction = dateTransaction;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
