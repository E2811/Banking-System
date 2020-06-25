package com.midterm.bankingSystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.FraudDetection;
import com.midterm.bankingSystem.exception.LowBalance;
import com.midterm.bankingSystem.exception.NotEnoughData;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.CheckingRepository;
import com.midterm.bankingSystem.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CheckingServiceUnitTest {

    @MockBean
    private CheckingRepository checkingRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @Autowired
    private CheckingService checkingService;

    private AccountHolder accountHolder;
    private CheckingAccount checkingAccount;
    private ThirdPartyUser thirdPartyUser;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        checkingAccount = new CheckingAccount(new Money(new BigDecimal("300")),accountHolder,"234");
        thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Marta");
        thirdPartyUser.setHashedKey(passwordEncoder.encode("123"));

        when(checkingRepository.findById(1)).thenReturn(java.util.Optional.of(checkingAccount));
        when(checkingRepository.findAll()).thenReturn(Arrays.asList(checkingAccount));
        when(checkingRepository.save(Mockito.any(CheckingAccount.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
    }


    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findAll() {
        assertEquals(1,checkingService.findAll().size());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findById() {
        CheckingAccount checkingAccount = checkingService.findById(1);
        assertEquals("234",checkingAccount.getSecretKey());
    }

    @Test
    void findById_idNotFound(){
        assertThrows(DataNotFoundException.class,()-> checkingService.findById(3));
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void create() {
        CheckingAccount checkingAccount = checkingService.create(new CheckingAccount(new Money(new BigDecimal("400")),accountHolder,"521"));
        assertEquals("521",checkingAccount.getSecretKey());
    }

    @Test
    @WithMockUser(username = "Marta",roles = "THIRDPARTY")
    void changeBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), 1, AccountType.CHECKING,"credit","234");
        checkingService.changeBalance(thirdPartyUser,requestDto);
        assertEquals(new BigDecimal("400.00"),checkingAccount.getBalance().getAmount());

    }
    @Test
    @WithMockUser(username = "Marta",roles = "THIRDPARTY")
    void changeBalance_lowBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("1000"), 1, AccountType.CHECKING,"debit","234");
        assertThrows(LowBalance.class,()->checkingService.changeBalance(thirdPartyUser,requestDto));
    }

    @Test
    @WithMockUser(username = "Marta",roles = "THIRDPARTY")
    void changeBalance_FrozenAccount() {
        checkingAccount.setStatus(Status.FROZEN);
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), 1, AccountType.CHECKING,"debit","234");
        assertThrows(FraudDetection.class,()->checkingService.changeBalance(thirdPartyUser,requestDto));
    }
}