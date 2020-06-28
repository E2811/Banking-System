package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.*;
import com.midterm.bankingSystem.model.CheckingAccount;
import com.midterm.bankingSystem.model.Transaction;
import com.midterm.bankingSystem.model.User;
import com.midterm.bankingSystem.repository.CheckingRepository;
import com.midterm.bankingSystem.repository.StudentCheckingRepository;
import com.midterm.bankingSystem.repository.TransactionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CheckingService {

    @Autowired
    private CheckingRepository checkingRepository;

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private static final Logger LOGGER = LogManager.getLogger(CheckingService.class);

    @Secured({"ROLE_ADMIN"})
    public List<CheckingAccount> findAll(){
        LOGGER.info("[INIT] -findAll Checking accounts");
        return checkingRepository.findAll();
    }

    public CheckingAccount findById(Integer id){
        LOGGER.info("[INIT] -find a Checking account by its id");
        CheckingAccount checkingAccount = checkingRepository.findById(id).orElseThrow(()->new DataNotFoundException("Checking not found"));
        checkingAccount.check();
        return checkingAccount;
    }

    @Secured({"ROLE_ADMIN"})
    public CheckingAccount create(CheckingAccount checkingAccount){
        LOGGER.info("[INIT] -create a new Checking account");
        return checkingRepository.save(checkingAccount);
    }

    public void changeBalance(User user, RequestDto requestDto){
        LOGGER.info("[INIT]- change balance checking Account");
        CheckingAccount checkingAccount = findById(requestDto.getAccountId());
        if (user.getRoles().size()==1 && user.getRoles().toString().contains("ROLE_THIRDPARTY")){
            if (requestDto.getSecretKey()==null){
                throw new NotEnoughData("The account secret key must be provided");
            }else if (!requestDto.getSecretKey().equals(checkingAccount.getSecretKey())){
                throw new InvalidAccountUser("Invalid secret key");
            }
        }
        switch (requestDto.getAction().toLowerCase()){
            case "credit":
                LOGGER.info("increase balance checking Account");
                checkingAccount.getBalance().increaseAmount(requestDto.getAmount());
                break;
            case "debit":
                LOGGER.info("decrease balance checking Account, check if the balance is enough");
                if (checkingAccount.getBalance().getAmount().compareTo(requestDto.getAmount())==-1){
                    LOGGER.error("Balance not enough");
                    throw new LowBalance("Not enough balance");
                }else if (checkingAccount.getStatus()== Status.FROZEN){
                    LOGGER.error("Account Frozen");
                    throw new FraudDetection("The account is frozen");
                }else{
                    LOGGER.info("decrease balance checking Account");
                    checkingAccount.getBalance().decreaseAmount(requestDto.getAmount());
                }
                break;
            default:
                throw new NotEnoughData("Invalid action: debit or credit balance");
        }
        checkingRepository.save(checkingAccount);
        transactionRepository.save(new Transaction(checkingAccount, requestDto.getAmount()));
        LOGGER.info("Transaction made by "+user.getId()+" finished at "+ LocalDateTime.now());
        LOGGER.info("[EXIT]- change balance checking Account");
    }
}
