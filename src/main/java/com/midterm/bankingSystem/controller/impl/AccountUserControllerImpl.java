package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.TransferDto;
import com.midterm.bankingSystem.controller.interfaces.AccountUserController;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.model.Account;
import com.midterm.bankingSystem.model.User;
import com.midterm.bankingSystem.service.AccountUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@Api(tags="AccountUser controller")
@RestController
@RequestMapping("/")
public class AccountUserControllerImpl implements AccountUserController {

    @Autowired
    private AccountUserService accountUserService;

    @PostMapping("/account/transfer")
    @ApiOperation(value = "Make a transference from any of user's account to any other account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@RequestBody @Valid TransferDto transferDto, @AuthenticationPrincipal User accountUser){
        accountUserService.transfer(accountUser, transferDto);
    }

    @GetMapping("/user/account/{accountId}")
    @ApiOperation(value = "An account holder can find a personal account by its id")
    public AccountMV findById(@PathVariable Integer accountId, @AuthenticationPrincipal User accountUser ){
        return accountUserService.findByIdOwnAccount(accountId, accountUser);
    }

    @GetMapping("accounts")
    @ApiOperation(value = "An accountHolder can find all its personal account")
    public List<Account> findAll(@AuthenticationPrincipal User accountUser){
        return accountUserService.findAll(accountUser);
    }
}

