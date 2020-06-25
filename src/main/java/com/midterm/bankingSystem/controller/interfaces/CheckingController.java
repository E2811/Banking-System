package com.midterm.bankingSystem.controller.interfaces;

import com.midterm.bankingSystem.controller.dto.CheckingDto;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.model.CheckingAccount;

import java.util.List;
import java.util.Optional;

public interface CheckingController {

    public List<CheckingAccount> findAll();
    public CheckingAccount findById(Integer id);
    public AccountMV create(Optional<Integer> id, Optional<Integer>  idSecondary, CheckingDto checkingDto);
}
