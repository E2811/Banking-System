package com.midterm.bankingSystem.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Address;
import com.midterm.bankingSystem.model.Money;
import com.midterm.bankingSystem.model.StudentChecking;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.StudentCheckingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
class StudentCheckingControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private AccountHolder accountHolder;
    private StudentChecking studentChecking;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        accountHolder = new AccountHolder("Marta", LocalDate.of(1998,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolderRepository.save(accountHolder);
        studentChecking = new StudentChecking(new Money(new BigDecimal("700")),accountHolder,"293");
        studentCheckingRepository.save(studentChecking);
    }

    @AfterEach
    void tearDown() {
        studentCheckingRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/student-checkings").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("293"));
    }

    @Test
    void findById() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/student-checking/"+studentChecking.getId()).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("700"));
    }
}