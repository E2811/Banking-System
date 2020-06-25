package com.midterm.bankingSystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.controller.dto.SavingDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.FraudDetection;
import com.midterm.bankingSystem.exception.InvalidAccountUser;
import com.midterm.bankingSystem.exception.LowBalance;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.CheckingRepository;
import com.midterm.bankingSystem.repository.SavingRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class SavingServiceUnitTest {


    @MockBean
    private SavingRepository savingRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private SavingService savingService;

    private AccountHolder accountHolder;
    private Saving saving;
    private ThirdPartyUser thirdPartyUser;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Marta");
        thirdPartyUser.setHashedKey(passwordEncoder.encode("123"));
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        saving = new Saving(new Money(new BigDecimal("700")),accountHolder,"857",new BigDecimal("180"), new BigDecimal("0.2"));

        when(savingRepository.findById(1)).thenReturn(java.util.Optional.of(saving));
        when(savingRepository.findAll()).thenReturn(Arrays.asList(saving));
        when(savingRepository.save(Mockito.any(Saving.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
        when(accountHolderRepository.findById(1)).thenReturn(java.util.Optional.of(accountHolder));

    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findAll() {
        assertEquals(1,savingService.findAll().size());
    }

    @Test
    void findById() {
        assertEquals("857",savingService.findById(1).getSecretKey());
    }

    @Test
    void findById_idNotFound(){
        assertThrows(DataNotFoundException.class,()-> savingService.findById(2));
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void create() {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("1220")));
        savingDto.setSecretKey("987");
        AccountMV accountMV = savingService.create(Optional.of(1),Optional.empty(),savingDto);
        assertEquals(new BigDecimal("1220.00"),accountMV.getBalance().getAmount());
    }

    @Test
    void changeBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), 1, AccountType.SAVING,"debit","857");
        savingService.changeBalance(thirdPartyUser,requestDto);
        assertEquals(new BigDecimal("600.00"),saving.getBalance().getAmount());
    }
    @Test
    void changeBalance_lowBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("10000"), 1, AccountType.SAVING,"debit","857");
        assertThrows(LowBalance.class,()->savingService.changeBalance(thirdPartyUser,requestDto));
    }

    @Test
    void changeBalance_FrozenAccount() {
       saving.setStatus(Status.FROZEN);
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), 1, AccountType.SAVING,"debit","857");
        assertThrows(FraudDetection.class,()->savingService.changeBalance(thirdPartyUser,requestDto));
    }
}