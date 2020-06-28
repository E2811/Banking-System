package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Address;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AccountHolderServiceUnitTest {


    @MockBean
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private AccountHolderService accountHolderService;

    private AccountHolder accountHolder;
    private AccountHolder accountHolder1;

    @BeforeEach
    void setUp() {

        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolder1 = new AccountHolder("lucia", LocalDate.of(1983,10,17),new Address("pio XII","Spain","Madrid",20833),"lucia");

        when(accountHolderRepository.findById(1)).thenReturn(java.util.Optional.of(accountHolder));
        when(accountHolderRepository.findAll()).thenReturn(Arrays.asList(accountHolder,accountHolder1));
        when(accountHolderRepository.save(Mockito.any(AccountHolder.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findAll(){
        assertEquals(2,accountHolderService.findAll().size());
    }
    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findById(){
        AccountHolder accountHolder = accountHolderService.findById(1);
        assertEquals("pepe",accountHolder.getName());
    }
    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void findById_idNotFound(){
        assertThrows(DataNotFoundException.class,()-> accountHolderService.findById(3));
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void create(){
        AccountHolder accountHolder = accountHolderService.create(accountHolder1);
        assertEquals("lucia",accountHolder.getName());
    }

}