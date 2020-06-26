package com.midterm.bankingSystem.model;

import com.midterm.bankingSystem.enums.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
public class CheckingAccount extends Account{
    private static final Logger LOGGER = LogManager.getLogger(CheckingAccount.class);
    @Enumerated(EnumType.STRING)
    private Status status;
    private final BigDecimal minimumBalance = new BigDecimal("250");
    private final BigDecimal monthlyMaintenanceFee= new BigDecimal("12");
    private boolean penalty;

    public CheckingAccount() {
        this.updateDate = LocalDateTime.now();
    }

    public CheckingAccount(Money balance, AccountHolder primaryOwner, String secretKey) {
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

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void check(){
        int months =  (int) updateDate.until(LocalDateTime.now(), ChronoUnit.MONTHS);
        if (months > 0 && balance.getAmount().compareTo(new BigDecimal("0"))==1){
            LOGGER.info("interest added on a check account");
            balance.increaseAmount(balance.getAmount().add(monthlyMaintenanceFee.multiply(new BigDecimal("months"))));
            updateDate = updateDate.plusYears(Math.floorDiv(months, 12));
            updateDate = updateDate.plusMonths(months % 12);
        }
        LOGGER.info("[INIT] -check minimum balance on a checking account");
        if (balance.getAmount().compareTo(minimumBalance)==-1 && penalty== false){
            balance.decreaseAmount(new Money(penaltyFee));
            penalty = true;
        }
        if (balance.getAmount().compareTo(minimumBalance)==1 && penalty== true){
            penalty = false;
        }
    }

}
