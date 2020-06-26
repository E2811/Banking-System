package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.interfaces.AccountHolderController;
import com.midterm.bankingSystem.model.AccountHolder;
import com.midterm.bankingSystem.service.AccountHolderService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@Api(tags ="AccountHolder controller")
@RestController
public class AccountHolderControllerImpl implements AccountHolderController {

    @Autowired
    private AccountHolderService accountHolderService;


    @GetMapping("/account-holders")
    public List<AccountHolder> findAll() {
        return accountHolderService.findAll();
    }

    @GetMapping("/account-holder/{id}")
    public AccountHolder findById(@PathVariable Integer id) {
        return accountHolderService.findById(id);
    }

    @PostMapping("/account-holder")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder create(@RequestBody @Valid AccountHolder accountHolder) {
        return accountHolderService.create(accountHolder);
    }

}
