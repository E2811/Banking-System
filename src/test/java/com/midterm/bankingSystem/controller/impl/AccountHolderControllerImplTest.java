package com.midterm.bankingSystem.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.AccountUser;
import com.midterm.bankingSystem.model.Address;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.UserRepository;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
class AccountHolderControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private AccountHolder accountHolder;


    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolderRepository.save(accountHolder);
        userRepository.save(new AccountUser(accountHolder.getName(),accountHolder.getPassword()));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/account-holders").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("pepe"));
    }

    @Test
    void findById() throws Exception {
        MvcResult result = mockMvc.perform(get("/account-holder/"+accountHolder.getId()).with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("retiro"));
    }

    @Test
    void create() throws Exception {
        AccountHolder accountHolder1 = new AccountHolder("Jaime", LocalDate.of(1980,8,27),new Address("General Davila","Spain","Santander",20833),"perro");
        MvcResult result = mockMvc.perform(post("/account-holder").with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(accountHolder1)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Santander"));
    }

    @Test
    void create_badAuthorization() throws Exception {
        AccountHolder accountHolder1 = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        mockMvc.perform(post("/account-holder").with(user("sara").roles("THIRDPARTY")).content(objectMapper.writeValueAsString(accountHolder1)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isForbidden());
    }

    @Test
    void create_badRequest() throws Exception {
        AccountHolder accountHolder1 = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        mockMvc.perform(post("/account-holder").with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(accountHolder1)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}