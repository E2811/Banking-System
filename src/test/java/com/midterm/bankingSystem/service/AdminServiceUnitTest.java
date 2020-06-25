package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.AccountUser;
import com.midterm.bankingSystem.model.ThirdPartyUser;
import com.midterm.bankingSystem.model.User;
import com.midterm.bankingSystem.repository.RoleRepository;
import com.midterm.bankingSystem.repository.UserRepository;
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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class AdminServiceUnitTest {

    @MockBean
    private UserRepository userRepo;
    @MockBean
    private RoleRepository roleRepository;
    @Autowired
    private  AdminService adminService;

    private ThirdPartyUser thirdPartyUser;
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @BeforeEach
    void setUp() {
        thirdPartyUser = new ThirdPartyUser();
        thirdPartyUser.setUsername("Marta");
        thirdPartyUser.setHashedKey(passwordEncoder.encode("123"));
        AccountUser accountUser = new AccountUser("Maria",passwordEncoder.encode("gato"));
        when(userRepo.findAll()).thenReturn(Arrays.asList(thirdPartyUser,accountUser));
        when(userRepo.save(Mockito.any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    }


    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void getAllUserAccounts() {
        assertEquals(2,adminService.getAllUserAccounts().size());
    }

    @Test
    @WithMockUser(username = "admin",roles = "ADMIN")
    void create() {
        ThirdPartyUser thirdPartyUser1 = adminService.create(thirdPartyUser);
        assertEquals("Marta",thirdPartyUser1.getUsername());
    }

}