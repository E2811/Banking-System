package com.midterm.bankingSystem.controller.interfaces;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.TransferDto;
import com.midterm.bankingSystem.enums.AccountType;
import com.midterm.bankingSystem.model.Account;
import com.midterm.bankingSystem.model.User;

import java.util.List;

public interface AccountUserController {
    void transfer(TransferDto transferDto,User accountUser);
    AccountMV findById(Integer accountId,User accountUser);
    List<Account> findAll(User accountUser);
}
