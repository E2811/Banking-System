package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.TransferDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.FraudDetection;
import com.midterm.bankingSystem.exception.InvalidAccountUser;
import com.midterm.bankingSystem.exception.LowBalance;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccountUserServiceTest {

    @MockBean
    private AccountHolderRepository accountHolderRepository;
    @MockBean
    private CreditCardService creditCardService;
    @MockBean
    private SavingService savingService;
    @MockBean
    private CreditCardRepository creditCardRepository;
    @MockBean
    private SavingRepository savingRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private AccountRepository accountRepository;
    @Autowired
    private AccountUserService accountUserService;


    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private CreditCard creditCard;
    private Saving saving;
    private AccountUser accountUser;
    private AccountHolder accountHolder;
    private AccountUser accountUser1;
    private AccountHolder accountHolder1;

    @BeforeEach
    void setUp() {
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolder1 = new AccountHolder("lucia", LocalDate.of(1983,10,17),new Address("pio XII","Spain","Madrid",20833),"lucia");
        accountUser = new AccountUser(accountHolder.getName(),passwordEncoder.encode(accountHolder.getPassword()));
        accountUser1 = new AccountUser(accountHolder1.getName(),passwordEncoder.encode(accountHolder1.getPassword()));
        creditCard =new CreditCard(new Money(new BigDecimal("700")),accountHolder,"987",new BigDecimal("100"), new BigDecimal("0.2"));
        when(creditCardRepository.findById(1)).thenReturn(java.util.Optional.of(creditCard));
        saving = new Saving(new Money(new BigDecimal("700")),accountHolder1,"857",new BigDecimal("180"), new BigDecimal("0.2"));
        when(savingService.findById(1)).thenReturn(saving);
        when(savingRepository.save(Mockito.any(Saving.class))).thenAnswer(i -> i.getArguments()[0]);
        when(creditCardService.findById(1)).thenReturn(creditCard);
        when(creditCardRepository.save(Mockito.any(CreditCard.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountHolderRepository.findByName("pepe")).thenReturn(accountHolder);
        when(accountHolderRepository.findByName("lucia")).thenReturn(accountHolder1);
        when(accountRepository.findByPrimaryOwner(accountHolder)).thenReturn(Arrays.asList(creditCard));
        when(accountRepository.findAccountById(1,1)).thenReturn(creditCard);
        when(accountRepository.findAccountById(2,2)).thenReturn(saving);
    }

    @Test
    @WithMockUser(username = "pepe",roles = "ACCOUNTUSER")
    void transfer_creditCardToSaving() {
        TransferDto transferDto = new TransferDto(1, accountHolder1.getName(), 1, new BigDecimal("50"), AccountType.CREDIT_CARD, AccountType.SAVING);
        accountUserService.transfer(accountUser,transferDto);
        assertEquals(new BigDecimal("650.00"),creditCard.getBalance().getAmount());
    }

    @Test
    @WithMockUser(username = "pepe",roles = "ACCOUNTUSER")
    void transfer_SavingToCreditCard() {
        TransferDto transferDto = new TransferDto(1, accountHolder.getName(), 1, new BigDecimal("50"), AccountType.SAVING, AccountType.CREDIT_CARD);
        accountUserService.transfer(accountUser1,transferDto);
        assertEquals(new BigDecimal("750.00"),creditCard.getBalance().getAmount());
    }

    @Test
    @WithMockUser(username = "lucia",roles = "ACCOUNTUSER")
    void transfer_badUser_unAuthorized() {
        TransferDto transferDto = new TransferDto(1, accountHolder.getName(), 1, new BigDecimal("50"), AccountType.SAVING, AccountType.CREDIT_CARD);
        assertThrows(InvalidAccountUser.class,()->accountUserService.transfer(accountUser,transferDto));
    }
    @Test
    @WithMockUser(username = "lucia",roles = "ACCOUNTUSER")
    void transfer_lowBalance() {
        TransferDto transferDto = new TransferDto(1, accountHolder.getName(), 1, new BigDecimal("800"), AccountType.SAVING, AccountType.CREDIT_CARD);
        assertThrows(LowBalance.class,()->accountUserService.transfer(accountUser1,transferDto));
    }
    @Test
    @WithMockUser(username = "lucia",roles = "ACCOUNTUSER")
    void transfer_accountFrozen() {
        saving.setStatus(Status.FROZEN);
        TransferDto transferDto = new TransferDto(1, accountHolder.getName(), 1, new BigDecimal("80"), AccountType.SAVING, AccountType.CREDIT_CARD);
        assertThrows(FraudDetection.class,()->accountUserService.transfer(accountUser1,transferDto));
    }
    @Test
    @WithMockUser(username = "pepe",roles = "ACCOUNTUSER")
    void transfer_CreditCardLowBalance() {
        TransferDto transferDto = new TransferDto(1, accountHolder1.getName(), 1, new BigDecimal("800"), AccountType.CREDIT_CARD, AccountType.SAVING);
        assertThrows(LowBalance.class,()->accountUserService.transfer(accountUser,transferDto));
    }

    @Test
    @WithMockUser(username = "pepe",roles = "ACCOUNTUSER")
    void findByIdOwnAccount_creditCard() {
        accountHolder.setId(1);
        AccountMV accountMV = accountUserService.findByIdOwnAccount(1,accountUser);
        assertEquals(accountHolder.getName(),accountMV.getPrimaryOwner().getName());
    }
    @Test
    @WithMockUser(username = "lucia",roles = "ACCOUNTUSER")
    void findByIdOwnAccount_Saving() {
        accountHolder1.setId(2);
        AccountMV accountMV = accountUserService.findByIdOwnAccount(2,accountUser1);
        assertEquals(accountHolder1.getName(),accountMV.getPrimaryOwner().getName());
    }
    @Test
    @WithMockUser(username = "pepe",roles = "ACCOUNTUSER")
    void findByIdOwnAccount_badUser() {
        assertThrows(DataNotFoundException.class,()->accountUserService.findByIdOwnAccount(1,accountUser1));
    }
    @Test
    @WithMockUser(username = "pepe",roles = "ACCOUNTUSER")
    void findAll() {
        assertEquals(1,accountUserService.findAll(accountUser).size());
    }
}