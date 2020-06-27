package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.CreditCardDto;
import com.midterm.bankingSystem.controller.interfaces.CreditCardController;
import com.midterm.bankingSystem.model.CheckingAccount;
import com.midterm.bankingSystem.model.CreditCard;
import com.midterm.bankingSystem.service.CreditCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@Api(tags = "CreditCard Controller")
@RestController
@RequestMapping("/")
public class CreditCardControllerImpl implements CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping("/account/credit-cards")
    @ApiOperation(value = "Find all credit-cards",
            response = CreditCard.class)
    @ResponseStatus(HttpStatus.OK)
    public List<CreditCard> findAll() {
        return creditCardService.findAll();
    }

    @GetMapping("/account/credit-card/{id}")
    @ApiOperation(value = "Find a credit-card by its id",
            response = CreditCard.class)
    @ResponseStatus(HttpStatus.OK)
    public CreditCard findById(@PathVariable Integer id) {
        return creditCardService.findById(id);
    }

    @PostMapping("/account/credit-card")
    @ApiOperation(value = "Create a new credit-card",
            response = CreditCard.class)
    @ResponseStatus(HttpStatus.CREATED)
    public AccountMV create(@RequestParam(name = "idOwner", required = false) Optional<Integer> id, @RequestParam(name = "idSecondary", required = false) Optional<Integer> idSecondary, @RequestBody @Valid CreditCardDto creditCardDto) {
        return creditCardService.create(id, idSecondary,creditCardDto);
    }

}
