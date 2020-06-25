package com.midterm.bankingSystem.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midterm.bankingSystem.controller.dto.TransferDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AccountUserControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private AccountHolder accountHolder;
    private AccountHolder accountHolder1;
    private AccountUser accountUser;
    private CheckingAccount checkingAccount;
    private CreditCard creditCard;
    private Transaction transaction;
    private Transaction transaction1;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        accountHolder = new AccountHolder("Lucia", LocalDate.of(1990, 8, 27), new Address("retiro", "Spain", "Madrid", 20833), "playa");
        accountHolder1 = new AccountHolder("Pablo", LocalDate.of(1997, 8, 27), new Address("barceloneta", "Spain", "Barcelona", 20833), "montaña");
        accountHolderRepository.save(accountHolder);
        accountHolderRepository.save(accountHolder1);
        checkingAccount = new CheckingAccount(new Money(new BigDecimal("3500")), accountHolder, "234");
        checkingRepository.save(checkingAccount);
        creditCard = new CreditCard(new Money(new BigDecimal("700")), accountHolder1, "987", new BigDecimal("100"), new BigDecimal("0.2"));
        creditCardRepository.save(creditCard);
        accountUser = new AccountUser(accountHolder.getName(), passwordEncoder.encode(accountHolder.getPassword()));
        userRepository.save(accountUser);
        Role role = new Role("ROLE_ACCOUNTUSER", accountUser);
        roleRepository.save(role);
        transaction1 = new Transaction(checkingAccount, new BigDecimal("50"));
        transaction1.setReceiptAccount(creditCard);
        transaction1.setDateTransaction(LocalDateTime.now().minusMinutes(60));
        transactionRepository.save(transaction1);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        checkingRepository.deleteAll();
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void transfer() throws Exception {
        TransferDto transferDto = new TransferDto(checkingAccount.getId(), accountHolder1.getName(), creditCard.getId(), new BigDecimal("50"), AccountType.CHECKING, AccountType.CREDIT_CARD);
        mockMvc.perform(post("/account/transfer").with(httpBasic("Lucia", "playa")).content(objectMapper.writeValueAsString(transferDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
        assertEquals(new BigDecimal("3450.00"), checkingRepository.findAll().get(0).getBalance().getAmount());
        assertEquals(new BigDecimal("750.00"), creditCardRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    void transfer_notFoundId_badRequest() throws Exception {
        TransferDto transferDto = new TransferDto(checkingAccount.getId(),accountHolder1.getName(),creditCard.getId(),new BigDecimal("50"), AccountType.SAVING, AccountType.CREDIT_CARD);
        mockMvc.perform(post("/account/transfer").with(httpBasic("Lucia", "playa")).content(objectMapper.writeValueAsString(transferDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }
    @Test
    void transfer_userNotAccountHolder_nonAuthorized() throws Exception {
        TransferDto transferDto = new TransferDto(checkingAccount.getId(),accountHolder1.getName(),creditCard.getId(),new BigDecimal("50"), AccountType.CHECKING, AccountType.CREDIT_CARD);
        mockMvc.perform(post("/account/transfer").with(httpBasic("Pablo", "montaña")).content(objectMapper.writeValueAsString(transferDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    void transfer_fraudDetectionOneSec_badRequest() throws Exception {
        transaction = new Transaction(checkingAccount, new BigDecimal("100"));
        transaction.setReceiptAccount(creditCard);
        transactionRepository.save(transaction);
        TransferDto transferDto = new TransferDto(checkingAccount.getId(), accountHolder1.getName(), creditCard.getId(), new BigDecimal("50"), AccountType.CHECKING, AccountType.CREDIT_CARD);
        mockMvc.perform(post("/account/transfer").with(httpBasic("Lucia", "playa")).content(objectMapper.writeValueAsString(transferDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        assertEquals(Status.FROZEN, checkingRepository.findAll().get(0).getStatus());
    }
    @Test
    void transfer_fraudDetectionHighAmount_badRequest() throws Exception {
        transaction = new Transaction(checkingAccount, new BigDecimal("100"));
        transaction.setReceiptAccount(creditCard);
        transaction.setDateTransaction(LocalDateTime.of(2019,11,8,9,5,23));
        transactionRepository.save(transaction);
        TransferDto transferDto = new TransferDto(checkingAccount.getId(), accountHolder1.getName(), creditCard.getId(), new BigDecimal("3500"), AccountType.CHECKING, AccountType.CREDIT_CARD);
        mockMvc.perform(post("/account/transfer").with(httpBasic("Lucia", "playa")).content(objectMapper.writeValueAsString(transferDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        assertEquals(Status.FROZEN, checkingRepository.findAll().get(0).getStatus());
    }

    @Test
    void findById() {
    }
}

