package com.midterm.bankingSystem.controller.interfaces;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.SavingDto;
import com.midterm.bankingSystem.model.Saving;

import java.util.List;
import java.util.Optional;

public interface SavingController {
    public List<Saving> findAll();
    public Saving findById(Integer id);
    public AccountMV create(Optional<Integer> id, Optional<Integer> idSecondary, SavingDto savingDto);
}
