package com.midterm.bankingSystem.controller.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.midterm.bankingSystem.controller.dto.CreditCardDto;
import com.midterm.bankingSystem.controller.dto.SavingDto;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.Address;
import com.midterm.bankingSystem.model.Money;
import com.midterm.bankingSystem.model.Saving;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.SavingRepository;
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
class SavingControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private SavingRepository savingRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private AccountHolder accountHolder;
    private AccountHolder accountHolder1;
    private Saving saving;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolderRepository.save(accountHolder);
        accountHolder1 = new AccountHolder("Lucas", LocalDate.of(1992,8,27),new Address("Ensanche de Vallecas","Spain","Madrid",20833),"sandia");
        saving = new Saving(new Money(new BigDecimal("700")),accountHolder,"857",new BigDecimal("180"), new BigDecimal("0.2"));
        savingRepository.save(saving);
    }

    @AfterEach
    void tearDown() {
        savingRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/savings").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("857"));
    }

    @Test
    void findById() throws Exception {
        MvcResult result = mockMvc.perform(get("/account/saving/"+ saving.getId()).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("180"));
    }

    @Test
    void create_defaultValues() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("1220")));
        savingDto.setSecretKey("987");
        MvcResult result = mockMvc.perform(post("/account/saving?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(savingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("1220"));
    }
    @Test
    void create_notEnoughDate_badRequest() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("1220")));
        mockMvc.perform(post("/account/saving?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(savingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void create_badInterestRate_badRequest() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("1220")));
        savingDto.setSecretKey("987");
        savingDto.setInterestRate(new BigDecimal("0.7"));
        mockMvc.perform(post("/account/saving?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(savingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void create_badBalance_badRequest() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("90")));
        savingDto.setSecretKey("987");
        savingDto.setMinimumBalance(new BigDecimal("800"));
        mockMvc.perform(post("/account/saving?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(savingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void create_badMinimumBalance_badRequest() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("900")));
        savingDto.setSecretKey("987");
        savingDto.setMinimumBalance(new BigDecimal("80"));
        mockMvc.perform(post("/account/saving?idOwner="+accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(savingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
    @Test
    void create_PrimaryOwner_setSecondaryOwner() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("9000")));
        savingDto.setSecretKey("987");
        savingDto.setPrimaryOwner(accountHolder1);
        mockMvc.perform(post("/account/saving?idSecondary="+ accountHolder.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(savingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated());
    }
    @Test
    void create_idAndPrimaryOwner_badRequest() throws Exception {
        SavingDto savingDto = new SavingDto();
        savingDto.setBalance(new Money(new BigDecimal("900")));
        savingDto.setSecretKey("987");
        savingDto.setPrimaryOwner(accountHolder);
        mockMvc.perform(post("/account/saving?idOwner="+accountHolder1.getId()).with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(savingDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}