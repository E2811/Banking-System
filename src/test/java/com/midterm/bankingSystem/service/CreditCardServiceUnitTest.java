package com.midterm.bankingSystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.CreditCardDto;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.FraudDetection;
import com.midterm.bankingSystem.exception.LowBalance;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.CheckingRepository;
import com.midterm.bankingSystem.repository.CreditCardRepository;
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
class CreditCardServiceUnitTest {

    @MockBean
    private CreditCardRepository creditCardRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @MockBean
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CreditCardService creditCardService;

    private AccountHolder accountHolder;
    private CreditCard creditCard;
    private ThirdPartyUser thirdPartyUser;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Marta");
        thirdPartyUser.setHashedKey(passwordEncoder.encode("123"));
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        creditCard =new CreditCard(new Money(new BigDecimal("700")),accountHolder,"987",new BigDecimal("100"), new BigDecimal("0.2"));
        when(accountHolderRepository.findById(1)).thenReturn(java.util.Optional.of(accountHolder));
        when(creditCardRepository.findById(1)).thenReturn(java.util.Optional.of(creditCard));
        when(creditCardRepository.findAll()).thenReturn(Arrays.asList(creditCard));
        when(creditCardRepository.save(Mockito.any(CreditCard.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
    }


    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findAll() {
        assertEquals(1,creditCardService.findAll().size());
    }

    @Test
    void findById() {
        assertEquals("987",creditCardService.findById(1).getSecretKey());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void create() {
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setBalance(new Money("300"));
        creditCardDto.setSecretKey("789");
        AccountMV accountMV = creditCardService.create(Optional.of(1),Optional.empty(),creditCardDto);
        assertEquals(new BigDecimal("300"),accountMV.getBalance().getAmount());
    }
    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void create_IdNotFound() {
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setBalance(new Money("300"));
        creditCardDto.setSecretKey("789");
        assertThrows(DataNotFoundException.class,()->creditCardService.create(Optional.of(2),Optional.empty(),creditCardDto));
    }

    @Test
    @WithMockUser(username = "Marta",roles = "THIRDPARTY")
    void changeBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), 1, AccountType.CREDIT_CARD,"credit","987");
        creditCardService.changeBalance(thirdPartyUser,requestDto);
        assertEquals(new BigDecimal("800.00"),creditCard.getBalance().getAmount());
    }

    @Test
    void changeBalance_Admin() {
        Admin admin = new Admin();
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setUsername("admin");
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), 1, AccountType.CREDIT_CARD,"credit",null);
        creditCardService.changeBalance(new Admin(),requestDto);
        assertEquals(new BigDecimal("800.00"),creditCard.getBalance().getAmount());
    }
    @Test
    void changeBalance_lowBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("1000"), 1, AccountType.CHECKING,"debit","234");
        assertThrows(LowBalance.class,()->creditCardService.changeBalance(thirdPartyUser,requestDto));
    }

}