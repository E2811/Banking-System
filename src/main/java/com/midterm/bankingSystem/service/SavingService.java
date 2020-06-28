package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.controller.dto.SavingDto;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.*;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SavingService {

    @Autowired
    private SavingRepository savingRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountHolderService accountHolderService;


    private static final Logger LOGGER = LogManager.getLogger(SavingService.class);
    @Secured({"ROLE_ADMIN"})
    public List<Saving> findAll(){
        LOGGER.info("[INIT] -findAll saving accounts");
        return savingRepository.findAll();
    }

    public Saving findById(Integer id){
        LOGGER.info("[INIT] -find a saving account by its id");
        Saving saving = savingRepository.findById(id).orElseThrow(()-> new DataNotFoundException("Saving account with id: "+id +" not found"));
        saving.check();
        return saving;
    }
    @Secured({"ROLE_ADMIN"})
    @Transactional
    public AccountMV create(Optional<Integer> id, Optional<Integer> idSecondary, SavingDto savingDto){
        LOGGER.info("[INIT] -create a new saving account");
        Saving saving = new Saving();
        AccountHolder accountHolder = savingDto.getPrimaryOwner();
        LOGGER.info("Check Owner of the account");
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
        AccountHolder secondaryOwner = savingDto.getSecondaryOwner();
        AccountMV accountMV = new AccountMV(savingDto.getBalance(),accountHolder);
        LOGGER.info("Check Secondary Owner of the account");
        // Check secondaryOwner
        if (!idSecondary.isPresent() && secondaryOwner!=null){
            secondaryOwner = accountHolderService.create(secondaryOwner);
        } else if (idSecondary.isPresent() && accountHolderRepository.findById(idSecondary.get()).isPresent() && secondaryOwner!=null){
            throw new DataNotFoundException("Provide either a valid id or an accountHolder but not both.");
        } else if (idSecondary.isPresent() && accountHolderRepository.findById(idSecondary.get()).isPresent()){
            secondaryOwner = accountHolderRepository.findById(idSecondary.get()).get();
        }else if (idSecondary.isPresent() && !accountHolderRepository.findById(idSecondary.get()).isPresent()){
            throw new DataNotFoundException("Secondary AccountHolder with id: "+idSecondary.get()+" not found");
        }
        accountMV.setSecondaryOwner(secondaryOwner);
        LOGGER.info("Check interestRate and minimumBalance of the account");
        if (savingDto.getInterestRate()== null){
            saving.setInterestRate(new BigDecimal("0.0025"));
        }else{
            saving.setInterestRate(savingDto.getInterestRate());
        }
        if (savingDto.getMinimumBalance()== null){
            saving.setMinimumBalance(new BigDecimal("1000"));
        }else{
            saving.setMinimumBalance(savingDto.getMinimumBalance());
        }
        if (savingDto.getBalance().getAmount().compareTo(saving.getMinimumBalance())==-1){
            throw new LowBalance("Balance less than the minimum allowed "+ saving.getMinimumBalance());
        }
        saving.setBalance(savingDto.getBalance());
        saving.setSecretKey(savingDto.getSecretKey());
        saving.setPrimaryOwner(accountHolder);
        saving.setSecondaryOwner(secondaryOwner);
        Saving saving1 = savingRepository.save(saving);
        accountMV.setId(saving1.getId());
        LOGGER.info("[EXIT] -  create a new saving account finished");
        return accountMV;
    }
    @Transactional
    public void changeBalance(User user,RequestDto requestDto){
        LOGGER.info("[INIT]- change balance saving Account");
        Saving saving = findById(requestDto.getAccountId());
        if (user.getRoles().size()==1 && user.getRoles().toString().contains("ROLE_THIRDPARTY")){
            if (requestDto.getSecretKey()==null){
                throw new NotEnoughData("The account secret key must be provided");
            }else if (!requestDto.getSecretKey().equals(saving.getSecretKey())){
                throw new InvalidAccountUser("Invalid secret key");
            }
        }
        switch (requestDto.getAction().toLowerCase()){
            case "credit":
                LOGGER.info("increase balance saving Account");
                saving.getBalance().increaseAmount(requestDto.getAmount());
                break;
            case "debit":
                LOGGER.info("decrease balance checking Account, check if the balance is enough");
                if (saving.getBalance().getAmount().compareTo(requestDto.getAmount())==-1){
                    LOGGER.error("Balance not enough");
                    throw new LowBalance("Not enough balance");
                }else if (saving.getStatus()== Status.FROZEN){
                    LOGGER.error("Account Frozen");
                    throw new FraudDetection("The account is frozen");
                }else{
                    LOGGER.info("decrease balance checking Account");
                    saving.getBalance().decreaseAmount(requestDto.getAmount());
                }
                break;
            default:
                throw new NotEnoughData("Invalid action: debit or credit balance");
        }
        transactionRepository.save(new Transaction(saving, requestDto.getAmount()));
        savingRepository.save(saving);
        LOGGER.info("Transaction made by "+user.getId()+" finished at "+ LocalDateTime.now());
        LOGGER.info("[EXIT]- change balance saving Account");
    }

}
