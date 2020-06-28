package com.midterm.bankingSystem.service;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.UserWithNameUsed;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.model.AccountUser;
import com.midterm.bankingSystem.model.Role;
import com.midterm.bankingSystem.repository.AccountHolderRepository;
import com.midterm.bankingSystem.repository.RoleRepository;
import com.midterm.bankingSystem.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepository;

    private final Logger LOGGER = LogManager.getLogger(AccountHolderService.class);

    @Secured({"ROLE_ADMIN"})
    public List<AccountHolder> findAll(){
        LOGGER.info("[INIT] -findAll accountHolders");
        return accountHolderRepository.findAll();
    }
    @Secured({"ROLE_ADMIN"})
    public AccountHolder findById(Integer id){
        LOGGER.info("[INIT] -find an accountHolder by its id");
        return accountHolderRepository.findById(id).orElseThrow(()-> new DataNotFoundException("AccountHolder with id: "+ id+ " not found"));
    }

    @Secured({"ROLE_ADMIN"})
    public AccountHolder create(AccountHolder accountHolder){
        LOGGER.info("[INIT] -Create a new accountHolder");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (userRepo.findByUsername(accountHolder.getName())!=null){
            throw new UserWithNameUsed("Username already used");
        }
        AccountUser accountUser = new AccountUser(accountHolder.getName(),passwordEncoder.encode(accountHolder.getPassword()));
        userRepo.save(accountUser);
        Role role = new Role("ROLE_ACCOUNTUSER",accountUser);
        roleRepository.save(role);
        accountHolder.setPassword(passwordEncoder.encode(accountHolder.getPassword()));
        return accountHolderRepository.save(accountHolder);
    }

}
