package com.midterm.bankingSystem.model;

import com.midterm.bankingSystem.enums.Status;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class StudentChecking extends Account{

    @Enumerated(EnumType.STRING)
    private Status status;

    public StudentChecking() {
        this.updateDate = LocalDateTime.now();
    }

    public StudentChecking(Money balance, AccountHolder primaryOwner,  String secretKey) {
        super(balance, primaryOwner, secretKey);
        this.status = Status.ACTIVE;
        this.updateDate = LocalDateTime.now();
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
