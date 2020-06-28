package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.CreditCardDto;
import com.midterm.bankingSystem.controller.dto.RequestDto;
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


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountHolderService accountHolderService;

    private static final Logger LOGGER = LogManager.getLogger(CreditCardService.class);

    @Secured({"ROLE_ADMIN"})
    public List<CreditCard> findAll(){
        LOGGER.info("[INIT] -findAll credit Card accounts");
        return creditCardRepository.findAll();
    }

    public CreditCard findById(Integer id){
        LOGGER.info("[INIT] -find a credit Card account by its id");
        CreditCard creditCard = creditCardRepository.findById(id).orElseThrow(()-> new DataNotFoundException("CreditCard with id: "+id+" not found"));
        creditCard.check();
        return creditCard;
    }
    @Secured({"ROLE_ADMIN"})
    public AccountMV create(Optional<Integer> id, Optional<Integer> idSecondary, CreditCardDto creditCardDto){
        LOGGER.info("[INIT] -Create a new credit Card account");
        CreditCard creditCard = new CreditCard();
        AccountHolder accountHolder = creditCardDto.getPrimaryOwner();
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
        LOGGER.info("Owner of the account already checked");
        AccountHolder secondaryOwner = creditCardDto.getSecondaryOwner();
        AccountMV accountMV = new AccountMV(creditCardDto.getBalance(),accountHolder);
        // Check secondaryOwner
        LOGGER.info("Check Secondary Owner if present");
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
        LOGGER.info("Secondary Owner checked");
        LOGGER.info("Check interestRate and CreditLimit");
        if (creditCardDto.getInterestRate()== null){
            creditCard.setInterestRate(new BigDecimal("0.0025"));
        }else{
            creditCard.setInterestRate(creditCardDto.getInterestRate());
        }
        if (creditCardDto.getCreditLimit()== null){
           creditCard.setCreditLimit(new BigDecimal("100"));
        }else{
            creditCard.setCreditLimit(creditCardDto.getCreditLimit());
        }
        LOGGER.info("InterestRate and CreditLimit checked");
        creditCard.setBalance(creditCardDto.getBalance());
        creditCard.setSecretKey(creditCardDto.getSecretKey());
        creditCard.setPrimaryOwner(accountHolder);
        creditCard.setSecondaryOwner(secondaryOwner);
        accountMV.setId(creditCardRepository.save(creditCard).getId());
        LOGGER.info("[EXIT] -Create a new credit Card account finished");
        return accountMV;
    }

    public void changeBalance(User user,RequestDto requestDto){
        LOGGER.info("[INIT]- change balance creditCard Account");
        CreditCard creditCard = this.findById(requestDto.getAccountId());
        if (user.getRoles().size()==1 && user.getRoles().toString().contains("ROLE_THIRDPARTY")){
            if (requestDto.getSecretKey()==null){
                throw new NotEnoughData("The account secret key must be provided");
            }else if (!requestDto.getSecretKey().equals(creditCard.getSecretKey())){
                throw new InvalidAccountUser("Invalid secret key");
            }
        }
        switch (requestDto.getAction().toLowerCase()){
            case "credit":
                LOGGER.info("increase balance creditCard Account");
                creditCard.getBalance().increaseAmount(requestDto.getAmount());
                break;
            case "debit":
                LOGGER.info("decrease balance checking Account, check if the balance is enough");
                if (creditCard.getBalance().getAmount().compareTo(requestDto.getAmount())==1) {
                    LOGGER.info("decrease balance checking Account");
                    creditCard.getBalance().decreaseAmount(requestDto.getAmount());
                }else{
                    LOGGER.error("Balance not enough");
                    throw new LowBalance("Balance not enough");
                }
                break;
            default:
                throw new NotEnoughData("Invalid action: debit or credit balance");
        }
        transactionRepository.save(new Transaction(creditCard, requestDto.getAmount()));
        creditCardRepository.save(creditCard);
        LOGGER.info("Transaction made by "+user.getId()+" finished at "+ LocalDateTime.now());
        LOGGER.info("[EXIT]- change balance creditCard Account");
    }
}
