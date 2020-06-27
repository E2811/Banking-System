package com.midterm.bankingSystem.service;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.TransferDto;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.FraudDetection;
import com.midterm.bankingSystem.exception.InvalidAccountUser;
import com.midterm.bankingSystem.exception.LowBalance;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AccountUserService {

    @Autowired
    private CheckingService checkingService;
    @Autowired
    private CreditCardService creditCardService;
    @Autowired
    private SavingService savingService;
    @Autowired
    private StudentCheckingService studentCheckingService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private SavingRepository savingRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    private static final Logger LOGGER = LogManager.getLogger(AccountUserService.class);

    @Secured({"ROLE_ACCOUNTUSER"})
    @Transactional(noRollbackFor = FraudDetection.class)
    public void transfer(User accountUser, TransferDto transferDto) {
        LOGGER.info("[INIT] -transfer money between accounts");
        Transaction transaction = new Transaction();
        LOGGER.info("Search sender account");
        switch (transferDto.getTypeOwnAccount().toString()) {
            case "checking":
                CheckingAccount senderAccount = checkingService.findById(transferDto.getId());
                if (!accountUser.getUsername().equals(senderAccount.getPrimaryOwner().getName()) && senderAccount.getSecondaryOwner() != null && !accountUser.getUsername().equals(senderAccount.getSecondaryOwner().getName())) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (!accountUser.getUsername().equals(senderAccount.getPrimaryOwner().getName()) && senderAccount.getSecondaryOwner() == null) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (senderAccount.getBalance().getAmount().compareTo(transferDto.getAmount()) == -1) {
                    throw new LowBalance("Balance insufficient for the transaction");
                } else if (senderAccount.getStatus() == Status.FROZEN) {
                    throw new FraudDetection("The account is frozen");
                }
                if (fraudDetection(transferDto)) {
                    LOGGER.error("Account Frozen");
                    senderAccount.setStatus(Status.FROZEN);
                    checkingRepository.save(senderAccount);
                    throw new FraudDetection("Fraud Detected");
                }
                LOGGER.info("Remove balance from sender account");
                senderAccount.getBalance().decreaseAmount(transferDto.getAmount());
                senderAccount.check();
                checkingRepository.save(senderAccount);
                transaction.setSenderAccount(senderAccount);
                break;
            case "saving":
                Saving senderAccount1 = savingService.findById(transferDto.getId());
                if (!accountUser.getUsername().equals(senderAccount1.getPrimaryOwner().getName()) && senderAccount1.getSecondaryOwner() != null && !accountUser.getUsername().equals(senderAccount1.getSecondaryOwner().getName())) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (!accountUser.getUsername().equals(senderAccount1.getPrimaryOwner().getName()) && senderAccount1.getSecondaryOwner() == null) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (senderAccount1.getBalance().getAmount().compareTo(transferDto.getAmount()) == -1) {
                    throw new LowBalance("Balance insufficient for the transaction");
                } else if (senderAccount1.getStatus() == Status.FROZEN) {
                    throw new FraudDetection("The account is frozen");
                }
                if (fraudDetection(transferDto)) {
                    LOGGER.error("Account Frozen");
                    senderAccount1.setStatus(Status.FROZEN);
                    savingRepository.save(senderAccount1);
                    throw new FraudDetection("Fraud Detected");
                }
                LOGGER.info("Remove balance from sender account");
                senderAccount1.getBalance().decreaseAmount(transferDto.getAmount());
                senderAccount1.check();
                savingRepository.save(senderAccount1);
                transaction.setSenderAccount(senderAccount1);
                break;
            case "creditcard":
                CreditCard senderAccount2 = creditCardService.findById(transferDto.getId());
                if (!accountUser.getUsername().equals(senderAccount2.getPrimaryOwner().getName()) && senderAccount2.getSecondaryOwner() != null && !accountUser.getUsername().equals(senderAccount2.getSecondaryOwner().getName())) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (!accountUser.getUsername().equals(senderAccount2.getPrimaryOwner().getName()) && senderAccount2.getSecondaryOwner() == null) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (senderAccount2.getBalance().getAmount().compareTo(transferDto.getAmount()) == -1) {
                    throw new LowBalance("Balance insufficient for the transaction");
                }

                LOGGER.info("Remove balance from sender account");
                senderAccount2.getBalance().decreaseAmount(transferDto.getAmount());
                senderAccount2.check();
                creditCardRepository.save(senderAccount2);
                transaction.setSenderAccount(senderAccount2);
                break;
            case "studentchecking":
                StudentChecking senderAccount3 = studentCheckingService.findById(transferDto.getId());
                if (!accountUser.getUsername().equals(senderAccount3.getPrimaryOwner().getName()) && senderAccount3.getSecondaryOwner() != null && !accountUser.getUsername().equals(senderAccount3.getSecondaryOwner().getName())) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (!accountUser.getUsername().equals(senderAccount3.getPrimaryOwner().getName()) && senderAccount3.getSecondaryOwner() == null) {
                    throw new InvalidAccountUser("Account not authorized to this user");
                } else if (senderAccount3.getBalance().getAmount().compareTo(transferDto.getAmount()) == -1) {
                    throw new LowBalance("Balance insufficient for the transaction");
                } else if (senderAccount3.getStatus() == Status.FROZEN) {
                    throw new FraudDetection("The account is frozen");
                }
                if (fraudDetection(transferDto)) {
                    LOGGER.error("Account Frozen");
                    senderAccount3.setStatus(Status.FROZEN);
                    studentCheckingRepository.save(senderAccount3);
                    throw new FraudDetection("Fraud Detected");
                }
                LOGGER.info("Remove balance from sender account");
                senderAccount3.getBalance().decreaseAmount(transferDto.getAmount());
                studentCheckingRepository.save(senderAccount3);
                transaction.setSenderAccount(senderAccount3);
                break;
        }

        LOGGER.info("Search receipt account");
        switch (transferDto.getTypeReceiptAccount().toString()) {
            case "checking":
                LOGGER.info("Increase balance from receipt account");
                CheckingAccount receiptAccount = checkingService.findById(transferDto.getAccountId());
                receiptAccount.getBalance().increaseAmount(transferDto.getAmount());
                receiptAccount.check();
                checkingRepository.save(receiptAccount);
                transaction.setReceiptAccount(receiptAccount);
                break;
            case "saving":
                LOGGER.info("Increase balance from receipt account");
                Saving receiptAccount2 = savingService.findById(transferDto.getAccountId());
                receiptAccount2.getBalance().increaseAmount(transferDto.getAmount());
                receiptAccount2.check();
                savingRepository.save(receiptAccount2);
                transaction.setReceiptAccount(receiptAccount2);
                break;
            case "creditcard":
                LOGGER.info("Increase balance from receipt account");
                CreditCard receiptAccount3 = creditCardService.findById(transferDto.getAccountId());
                receiptAccount3.getBalance().increaseAmount(transferDto.getAmount());
                receiptAccount3.check();
                creditCardRepository.save(receiptAccount3);
                transaction.setReceiptAccount(receiptAccount3);
                break;
            case "studentchecking":
                LOGGER.info("Increase balance from receipt account");
                StudentChecking receiptAccount1 = studentCheckingService.findById(transferDto.getAccountId());
                receiptAccount1.getBalance().increaseAmount(transferDto.getAmount());
                studentCheckingRepository.save(receiptAccount1);
                transaction.setReceiptAccount(receiptAccount1);
                break;
        }
        transaction.setAmount(transferDto.getAmount());
        transactionRepository.save(transaction);
        LOGGER.info("Transaction by: "+transaction.getSenderAccount()+" saved " + LocalDateTime.now());
        LOGGER.info("[Finished] -transfer money between accounts");
    }

    public boolean fraudDetection(TransferDto transferDto) {
        boolean fraud = false;
        BigDecimal highestTransaction = transactionRepository.highestTransaction(LocalDateTime.now(), transferDto.getId());
        LocalDateTime lastTransaction = transactionRepository.lastTransaction(transferDto.getId());
        BigDecimal senderTransaction = transactionRepository.highestTransactionOwner(LocalDateTime.now(), transferDto.getId());
        if (senderTransaction == null) {
            senderTransaction = new BigDecimal("0");
        }
        if ((lastTransaction != null) &&(Duration.between(lastTransaction, LocalDateTime.now()).getSeconds() < 1)) {
            LOGGER.error("Fraud detected: More than 2 transactions occurring within a 1 second");
            fraud = true;
        }else if ((highestTransaction!=null) &&(highestTransaction.multiply(new BigDecimal("2.50")).compareTo(senderTransaction.add(transferDto.getAmount())) == -1)){
            LOGGER.error("Transactions made in 24 hours that total to more than 150% of the customers highest daily total transactions in any other 24 hour period.");
            fraud = true;
        }
        return fraud;
    }

    @Secured({"ROLE_ACCOUNTUSER"})
    public AccountMV findByIdOwnAccount(Integer accountId, User accountUser){
        LOGGER.info("[INIT] - findByIdOwnAccount");
        AccountHolder accountHolder = accountHolderRepository.findByName(accountUser.getUsername());
        Account account = accountRepository.findAccountById(accountHolder.getId() , accountId);
        AccountMV accountMV;
        if(account instanceof CheckingAccount){
            ((CheckingAccount) account).check();
            LOGGER.info("Account Checking has been found and checked");
            accountMV = new AccountMV(account.getBalance(),account.getPrimaryOwner());
            accountMV.setId(account.getId());
            LOGGER.info("[END] - findByIdOwnAccount");
            accountRepository.save(account);
            return accountMV;
        }
        if(account instanceof StudentChecking){
            LOGGER.info("Account Student found");
            accountMV = new AccountMV(account.getBalance(),account.getPrimaryOwner());
            accountMV.setId(account.getId());
            LOGGER.info("[END] - findByIdOwnAccount");
            return accountMV;
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            accountRepository.save(account);
            LOGGER.info("Account Saving has been found and checked");
            accountMV = new AccountMV(account.getBalance(),account.getPrimaryOwner());
            accountMV.setId(account.getId());
            LOGGER.info("[END] - findByIdOwnAccount");
            return accountMV;
        }
        if(account instanceof CreditCard){
            ((CreditCard) account).check();
            accountRepository.save(account);
            LOGGER.info("Account CreditCard has been found and checked");
            accountMV = new AccountMV(account.getBalance(),account.getPrimaryOwner());
            accountMV.setId(account.getId());
            LOGGER.info("[END] - findByIdOwnAccount");
            return accountMV;
        }
        throw new DataNotFoundException("Account not found or unAuthorized for this user");
    }

    @Secured({"ROLE_ACCOUNTUSER"})
    public List<Account> findAll(User accountUser){
        LOGGER.info("[INIT] -find All accounts of a particular owner");
        AccountHolder accountHolder = accountHolderRepository.findByName(accountUser.getUsername());
        LOGGER.info("[EXIT] -find All accounts of a particular owner");
        return accountRepository.findByPrimaryOwner(accountHolder);
    }

}