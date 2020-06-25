package com.midterm.bankingSystem.model;

import com.midterm.bankingSystem.enums.Status;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Entity
public class StudentChecking extends Account{

    @Enumerated(EnumType.STRING)
    private Status status;

    public StudentChecking() {
    }

    public StudentChecking(Money balance, AccountHolder primaryOwner,  String secretKey) {
        super(balance, primaryOwner, secretKey);
        this.status = Status.ACTIVE;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
