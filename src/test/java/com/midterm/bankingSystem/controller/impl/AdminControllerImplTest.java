package com.midterm.bankingSystem.controller.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;


import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;


import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AdminControllerImplTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private SavingRepository savingRepository;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();
    private AccountHolder accountHolder;
    private CheckingAccount checkingAccount;
    private Saving saving;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        ThirdPartyUser thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Marta");
        thirdPartyUser.setHashedKey(passwordEncoder.encode("123"));
        userRepository.save(thirdPartyUser);
        Role role = new Role("ROLE_THIRDPARTY",thirdPartyUser);
        roleRepository.save(role);
        accountHolder = new AccountHolder("pepe", LocalDate.of(1990,8,27),new Address("retiro","Spain","Madrid",20833),"pepe");
        accountHolderRepository.save(accountHolder);
        checkingAccount = new CheckingAccount(new Money(new BigDecimal("300")),accountHolder,"234");
        saving = new Saving(new Money(new BigDecimal("700")),accountHolder,"857",new BigDecimal("180"), new BigDecimal("0.2"));
        checkingRepository.save(checkingAccount);
        savingRepository.save(saving);
        Admin admin = new Admin();
        admin.setUsername("admin");
        //PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);
        Role role1= new Role("ROLE_ADMIN",admin);
        Role role2= new Role("ROLE_THIRDPARTY",admin);
        roleRepository.save(role1);
        roleRepository.save(role2);
    }

    @AfterEach
    void tearDown() {
        roleRepository.deleteAll();
        userRepository.deleteAll();
        checkingRepository.deleteAll();
        savingRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void create() throws Exception {
        ThirdPartyUser thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Susana");
        thirdPartyUser.setHashedKey("548");
        MvcResult result = mockMvc.perform(post("/third-party").with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(thirdPartyUser)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isCreated()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("548"));
    }

    @Test
    void create_usedName() throws Exception {
        ThirdPartyUser thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Marta");
        thirdPartyUser.setHashedKey("548");
        mockMvc.perform(post("/third-party").with(user("admin").roles("ADMIN")).content(objectMapper.writeValueAsString(thirdPartyUser)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    void findAll() throws Exception {
        MvcResult result = mockMvc.perform(get("/users").with(user("admin").roles("ADMIN"))).andExpect(status().isOk()).andReturn();
        assertTrue(result.getResponse().getContentAsString().contains("Marta"));
    }

    @Test
    void changeBalance_checkingAccount() throws Exception {
        RequestDto requestDto = new RequestDto(new BigDecimal("80"), checkingAccount.getId(), AccountType.CHECKING,"debit",null);
        mockMvc.perform(patch("/account/transaction").with(httpBasic("admin", "admin")).content(objectMapper.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
        assertEquals(new BigDecimal("220.00"),checkingRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    void changeBalance_creditChecking() throws Exception {
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), checkingAccount.getId(), AccountType.CHECKING,"credit",null);
        mockMvc.perform(patch("/account/transaction").with(httpBasic("admin", "admin")).content(objectMapper.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
        assertEquals(new BigDecimal("400.00"),checkingRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    void changeBalance_thirdPartyNonSecretKey_nonAuthorized() throws Exception {
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), checkingAccount.getId(), AccountType.CHECKING,"credit",null);
        mockMvc.perform(patch("/account/transaction").with(httpBasic("marta", "123")).content(objectMapper.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
    }

    @Test
    void changeBalance_thrirdPartyUser_Saving() throws Exception {
        RequestDto requestDto = new RequestDto(new BigDecimal("100"), saving.getId(), AccountType.SAVING,"credit","857");
        mockMvc.perform(patch("/account/transaction").with(httpBasic("admin", "admin")).content(objectMapper.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());
        assertEquals(new BigDecimal("800.00"),savingRepository.findAll().get(0).getBalance().getAmount());
    }

    @Test
    void changeBalance_thrirdPartyUser_Saving_notEnoughBalance() throws Exception {
        RequestDto requestDto = new RequestDto(new BigDecimal("900"), saving.getId(), AccountType.SAVING,"debit","857");
        mockMvc.perform(patch("/account/transaction").with(httpBasic("admin", "admin")).content(objectMapper.writeValueAsString(requestDto)).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
}