package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.SavingDto;
import com.midterm.bankingSystem.controller.interfaces.SavingController;
import com.midterm.bankingSystem.model.CreditCard;
import com.midterm.bankingSystem.model.Saving;
import com.midterm.bankingSystem.service.SavingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@Api(tags = "Saving account Controller")
@RestController
@RequestMapping("/")
public class SavingControllerImpl implements SavingController {

    @Autowired
    private SavingService savingService;

    @GetMapping("/account/savings")
    @ApiOperation(value = "Find all saving accounts",
            response = Saving.class)
    @ResponseStatus(HttpStatus.OK)
    public List<Saving> findAll() {
        return savingService.findAll();
    }

    @GetMapping("/account/saving/{id}")
    @ApiOperation(value = "Find a saving account by its id",
            response = Saving.class)
    @ResponseStatus(HttpStatus.OK)
    public Saving findById(@PathVariable Integer id) {
        return savingService.findById(id);
    }

    @PostMapping("/account/saving")
    @ApiOperation(value = "Create a new saving account",
            response = Saving.class)
    @ResponseStatus(HttpStatus.CREATED)
    public AccountMV create(@RequestParam(name = "idOwner", required = false) Optional<Integer> id, @RequestParam(name = "idSecondary", required = false) Optional<Integer> idSecondary,@RequestBody @Valid SavingDto savingDto) {
        return savingService.create(id, idSecondary, savingDto);
    }
}
