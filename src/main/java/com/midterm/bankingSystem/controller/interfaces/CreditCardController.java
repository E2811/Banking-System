package com.midterm.bankingSystem.controller.interfaces;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.CreditCardDto;
import com.midterm.bankingSystem.model.CreditCard;

import java.util.List;
import java.util.Optional;

public interface CreditCardController {
    public List<CreditCard> findAll();
    public CreditCard findById(Integer id);
    public AccountMV create(Optional<Integer> id, Optional<Integer>  idSecondary, CreditCardDto creditCardDto);
}
