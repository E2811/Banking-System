package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.TransferDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.model.User;
import com.midterm.bankingSystem.service.AccountUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AccountUserControllerImpl {

    @Autowired
    private AccountUserService accountUserService;

    @PostMapping("/account/transfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody @Valid TransferDto transferDto, @AuthenticationPrincipal User accountUser){
        accountUserService.transfer(accountUser, transferDto);
    }

    @GetMapping("/account/{accountId}/{typeAccount}")
    public AccountMV findById(@PathVariable Integer accountId, @PathVariable AccountType typeAccount, @AuthenticationPrincipal User accountUser ){
        return accountUserService.findByIdOwnAccount(accountId, typeAccount, accountUser);
    }
}

