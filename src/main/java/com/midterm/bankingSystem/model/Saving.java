package com.midterm.bankingSystem.model;

import com.midterm.bankingSystem.enums.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
public class Saving extends Account {

    @Enumerated(EnumType.STRING)
    private Status status;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    private boolean penalty;
    private static final Logger LOGGER = LogManager.getLogger(Saving.class);

    public Saving() {
        this.status =  Status.ACTIVE;
        this.updateDate = LocalDateTime.now();
    }

    public Saving(Money balance, AccountHolder primaryOwner, @NotNull String secretKey, BigDecimal minimumBalance, @DecimalMin("100") BigDecimal interestRate) {
        super(balance, primaryOwner, secretKey);
        this.status =  Status.ACTIVE;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        this.penalty = false;
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

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public void check(){
        LOGGER.info("[INIT] -check minimum balance on a saving account");
        if (balance.getAmount().compareTo(minimumBalance)==-1 && penalty== false){
            balance.decreaseAmount(new Money(penaltyFee));
            penalty = true;
        }
        if (balance.getAmount().compareTo(minimumBalance)==1 && penalty== true){
            penalty = false;
        }
        LOGGER.info("[INIT] -check interest rate on a saving account");
        int years = (int) updateDate.until(LocalDateTime.now(), ChronoUnit.YEARS);
        if (years >= 1 && balance.getAmount().compareTo(new BigDecimal("0"))==1){
            LOGGER.info("interest added on a saving account");
            balance.increaseAmount(balance.getAmount().multiply(interestRate.add(new BigDecimal("1").pow(years))));
            updateDate = updateDate.plusYears(years);
        }
    }
}
