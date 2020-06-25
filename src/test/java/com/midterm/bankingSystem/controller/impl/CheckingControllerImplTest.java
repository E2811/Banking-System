package com.midterm.bankingSystem.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midterm.bankingSystem.controller.dto.CheckingDto;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Address;
import com.midterm.bankingSystem.model.CheckingAccount;
import com.midterm.bankingSystem.model.Money;
import com.midterm.bankingSystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CheckingControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;


    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private AccountHolder accountHolder;
    private CheckingAccount checkingAccount;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolderRepository.save(accountHolder);
        checkingAccount = new CheckingAccount(new Money(new BigDecimal("300")),accountHolder,"234");
        checkingRepository.save(checkingAccount);
    }

    @AfterEach
    void tearDown() {
        studentCheckingRepository.deleteAll();
        checkingRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/checkings").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("234"));
    }

    @Test
    void findById() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/checking/"+checkingAccount.getId()).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("250"));
    }

    @Test
    void create() throws Exception {
        CheckingDto checkingDto = new CheckingDto();
        checkingDto.setBalance(new Money("300"));
        checkingDto.setSecretKey("9876");
        MvcResult result = mockMvc.perform(post("/account/checking?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(checkingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Madrid"));
    }

    @Test
    void create_new_primaryOwner() throws Exception {
        CheckingDto checkingDto = new CheckingDto();
        checkingDto.setBalance(new Money("300"));
        AccountHolder accountHolder = new AccountHolder("Lucas", LocalDate.of(1992,8,27),new Address("Ensanche de Vallecas","Spain","Madrid",20833),"sandia");
        checkingDto.setSecretKey("9876");
        checkingDto.setPrimaryOwner(accountHolder);
        MvcResult result = mockMvc.perform(post("/account/checking").with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(checkingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Lucas"));
    }

    @Test
    void create_badRequest_idNotFound() throws Exception {
        CheckingDto checkingDto = new CheckingDto();
        checkingDto.setBalance(new Money("300"));
        checkingDto.setSecretKey("9876");
        mockMvc.perform(post("/account/checking?idOwner="+accountHolder.getId()+1).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(checkingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void create_notFound_idAndPrimaryOwner() throws Exception {
        CheckingDto checkingDto = new CheckingDto();
        checkingDto.setBalance(new Money("300"));
        checkingDto.setSecretKey("9876");
        AccountHolder accountHolder1 = new AccountHolder("Lucas", LocalDate.of(1992,8,27),new Address("Ensanche de Vallecas","Spain","Madrid",20833),"sandia");
        checkingDto.setPrimaryOwner(accountHolder1);
        mockMvc.perform(post("/account/checking?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(checkingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
    }

    @Test
    void create_newSecondaryOwner() throws Exception {
        CheckingDto checkingDto = new CheckingDto();
        checkingDto.setBalance(new Money("300"));
        AccountHolder accountHolder1 = new AccountHolder("Marta", LocalDate.of(1992,8,27),new Address("Ensanche de Vallecas","Spain","Madrid",20833),"sandia");
        checkingDto.setSecretKey("9876");
        checkingDto.setSecondaryOwner(accountHolder1);
        MvcResult result = mockMvc.perform(post("/account/checking?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(checkingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Marta"));
    }
    @Test
    void create_studentCheking() throws Exception {
        CheckingDto checkingDto = new CheckingDto();
        checkingDto.setBalance(new Money("300"));
        AccountHolder accountHolder1 = new AccountHolder("Pablo", LocalDate.of(1998,8,27),new Address("Ensanche de Vallecas","Spain","Madrid",20833),"sandia");
        checkingDto.setSecretKey("9876");
        checkingDto.setPrimaryOwner(accountHolder1);
        MvcResult result = mockMvc.perform(post("/account/checking").with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(checkingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Pablo"));
    }
}