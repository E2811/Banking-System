package com.midterm.bankingSystem.Util;

import com.midterm.bankingSystem.controller.dto.CheckingDto;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.LowBalance;
import com.midterm.bankingSystem.exception.UserWithNameUsed;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.RoleRepository;
import com.midterm.bankingSystem.repository.UserRepository;
import com.midterm.bankingSystem.service.AccountHolderService;
import com.midterm.bankingSystem.service.CheckingService;
import com.midterm.bankingSystem.service.StudentCheckingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Component
public class CheckingOrStudent {
    @Autowired
    private CheckingService checkingService;
    @Autowired
    private StudentCheckingService studentCheckingService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private AccountHolderService accountHolderService;

    private final Logger LOGGER = LogManager.getLogger(CheckingOrStudent.class);

    @Transactional
    public AccountMV createCheckingAccount(Optional<Integer> id, Optional<Integer> idSecondary,CheckingDto checkingDto) {
        // Check primaryOwner
        LOGGER.info("[INIT] -create a Checking or Student account");
        AccountHolder accountHolder = checkingDto.getPrimaryOwner();
        LOGGER.info("Check primaryOwner");
        if (!id.isPresent() && accountHolder!=null){
            accountHolder = accountHolderService.create(accountHolder);
        }else if (!id.isPresent() && accountHolder==null){
            throw new DataNotFoundException("you must provide a valid id or a valid new accountHolder.");
        }else if (id.isPresent() && accountHolderRepository.findById(id.get()).isPresent() && accountHolder!=null) {
            throw new DataNotFoundException("Provide either a valid id or an accountholder but not both.");
        } else if (id.isPresent() && accountHolderRepository.findById(id.get()).isPresent()){
            accountHolder = accountHolderRepository.findById(id.get()).get();
        } else{
            throw new DataNotFoundException("AccountHolder with id: "+id.get()+" not found");
        }
        LOGGER.info("Check secondaryOwner");
        AccountHolder secondaryOwner = checkingDto.getSecondaryOwner();
        AccountMV accountMV = new AccountMV(checkingDto.getBalance(), accountHolder);
        // Check secondaryOwner
        if (!idSecondary.isPresent() && secondaryOwner!=null){
            secondaryOwner = accountHolderService.create(secondaryOwner);
        } else if (idSecondary.isPresent() && accountHolderRepository.findById(idSecondary.get()).isPresent() && secondaryOwner!=null){
            throw new DataNotFoundException("Provide either a valid id or an accountholder but not both.");
        } else if (idSecondary.isPresent() && accountHolderRepository.findById(idSecondary.get()).isPresent() && secondaryOwner==null){
            secondaryOwner = accountHolderRepository.findById(idSecondary.get()).get();
        }else if (idSecondary.isPresent() && !accountHolderRepository.findById(idSecondary.get()).isPresent()){
            throw new DataNotFoundException("Secondary AccountHolder with id: "+idSecondary.get()+" not found");
        }
        accountMV.setSecondaryOwner(secondaryOwner);
        LOGGER.info("Check age of Owner");
        // Check age to determine if the account is for a student or not.
        if (Period.between(accountHolder.getDateBirth(), LocalDate.now()).getYears() < 24) {
            StudentChecking studentChecking = new StudentChecking(checkingDto.getBalance(), accountHolder, checkingDto.getSecretKey());
            // Check if there is a secondaryOwner in the request body
            studentChecking.setSecondaryOwner(secondaryOwner);
            StudentChecking studentChecking1 = studentCheckingService.create(studentChecking);
            accountMV.setId(studentChecking1.getId());
        } else {
            if (checkingDto.getBalance().getAmount().compareTo(new BigDecimal("250"))==-1){
                throw new LowBalance("Balance less than the minimum allowed (250)");
            }
            CheckingAccount checkingAccount = new CheckingAccount(checkingDto.getBalance(), accountHolder, checkingDto.getSecretKey());
            checkingAccount.setSecondaryOwner(secondaryOwner);
            CheckingAccount checkingAccount1 = checkingService.create(checkingAccount);
            accountMV.setId(checkingAccount1.getId());
        }
        LOGGER.info("[EXIT] -create a Checking or Student account finished");
        return accountMV;
    }
}

