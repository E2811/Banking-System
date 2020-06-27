package com.midterm.bankingSystem.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.Entity;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
public class CreditCard extends Account {

    private BigDecimal creditLimit;

    private BigDecimal interestRate;

    private static final Logger LOGGER = LogManager.getLogger(CreditCard.class);

    public CreditCard() {
        this.updateDate = LocalDateTime.now();
    }

    public CreditCard(Money balance, AccountHolder primaryOwner,  String secretKey, BigDecimal creditLimit, BigDecimal interestRate) {
        super(balance, primaryOwner, secretKey);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.updateDate = LocalDateTime.now();
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

    public void check(){
        LOGGER.info("[INIT] -check interest rate on a creditCard account");
        int months =  (int) updateDate.until(LocalDateTime.now(), ChronoUnit.MONTHS);
        if (months > 0){
            LOGGER.info("interest added on a creditCard account");
            balance.increaseAmount(balance.getAmount().multiply(interestRate.divide(new BigDecimal("12"),8, RoundingMode.HALF_EVEN).add(new BigDecimal("1")).pow(months)));
            updateDate = updateDate.plusYears(Math.floorDiv(months, 12));
            updateDate = updateDate.plusMonths(months % 12);
        }
    }
}

