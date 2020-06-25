package com.midterm.bankingSystem.controller.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midterm.bankingSystem.controller.dto.CreditCardDto;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Address;
import com.midterm.bankingSystem.model.CreditCard;
import com.midterm.bankingSystem.model.Money;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.CreditCardRepository;
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
class CreditCardControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;


    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private AccountHolder accountHolder;
    private CreditCard creditCard;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolderRepository.save(accountHolder);
        creditCard = new CreditCard(new Money(new BigDecimal("700")),accountHolder,"987",new BigDecimal("100"), new BigDecimal("0.2"));
        creditCardRepository.save(creditCard);
    }

    @AfterEach
    void tearDown() {
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/credit-cards").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("987"));
    }

    @Test
    void findById() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/credit-card/"+creditCard.getId()).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("100"));
    }

    @Test
    void create() throws Exception {
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setBalance(new Money("300"));
        creditCardDto.setSecretKey("789");
        MvcResult result = mockMvc.perform(post("/account/credit-card?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(creditCardDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Madrid"));
    }
    @Test
    void create_wrongInterestRate_badRequest() throws Exception {
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setBalance(new Money("300"));
        creditCardDto.setSecretKey("789");
        creditCardDto.setInterestRate(new BigDecimal("0.05"));
        mockMvc.perform(post("/account/credit-card?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(creditCardDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void create_wrongCreditLimit_badRequest() throws Exception {
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setBalance(new Money("300"));
        creditCardDto.setSecretKey("789");
        creditCardDto.setCreditLimit(new BigDecimal("90"));
        mockMvc.perform(post("/account/credit-card?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(creditCardDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void create_newPrimaryOwner_setSecondaryOwner() throws Exception {
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setBalance(new Money("300"));
        creditCardDto.setSecretKey("789");
        creditCardDto.setCreditLimit(new BigDecimal("120"));
        AccountHolder accountHolder1 = new AccountHolder("Paula", LocalDate.of(1992,8,27),new Address("Ensanche de Vallecas","Spain","Madrid",20833),"sandia");
        creditCardDto.setPrimaryOwner(accountHolder1);
        creditCardDto.setSecondaryOwner(accountHolder);
        mockMvc.perform(post("/account/credit-card").with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(creditCardDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }
    @Test
    void create_badRequest_IdOwnerAndNewOwner() throws Exception {
        CreditCardDto creditCardDto = new CreditCardDto();
        creditCardDto.setBalance(new Money("300"));
        creditCardDto.setSecretKey("789");
        creditCardDto.setCreditLimit(new BigDecimal("120"));
        AccountHolder accountHolder1 = new AccountHolder("Lucas", LocalDate.of(1992,8,27),new Address("Ensanche de Vallecas","Spain","Madrid",20833),"sandia");
        creditCardDto.setPrimaryOwner(accountHolder1);
        mockMvc.perform(post("/account/credit-card?idOwner="+accountHolder).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(creditCardDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}