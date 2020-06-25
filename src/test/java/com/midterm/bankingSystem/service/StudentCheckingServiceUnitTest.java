package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.FraudDetection;
import com.midterm.bankingSystem.exception.LowBalance;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.CheckingRepository;
import com.midterm.bankingSystem.repository.StudentCheckingRepository;
import com.midterm.bankingSystem.repository.TransactionRepository;
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
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class StudentCheckingServiceUnitTest {

    @MockBean
    private StudentCheckingRepository studentCheckingRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    @Autowired
    private StudentCheckingService studentCheckingService;

    private AccountHolder accountHolder;
    private StudentChecking studentChecking;
    private ThirdPartyUser thirdPartyUser;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        accountHolder = new AccountHolder("pepe", LocalDate.of(1999,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        studentChecking = new StudentChecking(new Money(new BigDecimal("500")),accountHolder,"293");
        thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Marta");
        thirdPartyUser.setHashedKey(passwordEncoder.encode("123"));

        when(studentCheckingRepository.findById(1)).thenReturn(java.util.Optional.of(studentChecking));
        when(studentCheckingRepository.findAll()).thenReturn(Arrays.asList(studentChecking));
        when(studentCheckingRepository.save(Mockito.any(StudentChecking.class))).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRepository.save(Mockito.any(Transaction.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findAll() {
        assertEquals(1,studentCheckingService.findAll().size());
    }

    @Test
    void findById() {
        assertEquals("293",studentCheckingService.findById(1).getSecretKey());
    }
    @Test
    void findById_idNotFound(){
        assertThrows(DataNotFoundException.class,()-> studentCheckingService.findById(3));
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void create() {
       StudentChecking studentCheckingt = studentCheckingService.create(new StudentChecking(new Money(new BigDecimal("400")),accountHolder,"521"));
        assertEquals("521",studentCheckingt.getSecretKey());
    }

    @Test
    void changeBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("50"), 1, AccountType.STUDENT_CHECKING,"credit","521");
        studentCheckingService.changeBalance(thirdPartyUser,requestDto);
        assertEquals(new BigDecimal("550.00"),studentChecking.getBalance().getAmount());
    }
    @Test
    void changeBalance_lowBalance() {
        RequestDto requestDto = new RequestDto(new BigDecimal("1000"), 1, AccountType.STUDENT_CHECKING,"debit","521");
        assertThrows(LowBalance.class,()->studentCheckingService.changeBalance(thirdPartyUser,requestDto));
    }
    @Test
    void changeBalance_FrozenAccount() {
       studentChecking.setStatus(Status.FROZEN);
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), 1, AccountType.STUDENT_CHECKING,"debit","521");
        assertThrows(FraudDetection.class,()->studentCheckingService.changeBalance(thirdPartyUser,requestDto));
    }
}