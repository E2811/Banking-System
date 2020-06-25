package com.midterm.bankingSystem.model;

import com.midterm.bankingSystem.enums.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Entity
public class CheckingAccount extends Account{

    @Enumerated(EnumType.STRING)
    private Status status;
    private final BigDecimal minimumBalance = new BigDecimal("250");
    private final BigDecimal monthlyMaintenanceFee= new BigDecimal("12");
    private boolean penalty;
    //private final Logger LOGGER = LogManager.getLogger(CheckingAccount.class);

    public CheckingAccount() {
    }

    public CheckingAccount(Money balance, AccountHolder primaryOwner, String secretKey) {
        super(balance, primaryOwner, secretKey);
        this.status = Status.ACTIVE;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void check(){
        //LOGGER.info("[INIT] -check minimum balance on a checking account");
        if (balance.getAmount().compareTo(minimumBalance)==-1 && penalty== false){
            balance.decreaseAmount(new Money(penaltyFee));
            penalty = true;
        }
        if (balance.getAmount().compareTo(minimumBalance)==1 && penalty== true){
            penalty = false;
        }
    }

}
