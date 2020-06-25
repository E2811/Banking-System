package com.midterm.bankingSystem.controller.interfaces;
import com.midterm.bankingSystem.model.AccountHolder;


import java.util.List;

public interface AccountHolderController {
    public List<AccountHolder> findAll();
    public AccountHolder findById(Integer id);
    public AccountHolder create(AccountHolder accountHolder);
}
