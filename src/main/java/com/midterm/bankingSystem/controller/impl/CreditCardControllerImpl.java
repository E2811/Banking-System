package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.CreditCardDto;
import com.midterm.bankingSystem.controller.interfaces.CreditCardController;
import com.midterm.bankingSystem.model.CreditCard;
import com.midterm.bankingSystem.service.CreditCardService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@Api(tags = "CreditCard Controller")
@RestController
public class CreditCardControllerImpl implements CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @GetMapping("/account/credit-cards")
    public List<CreditCard> findAll() {
        return creditCardService.findAll();
    }

    @GetMapping("/account/credit-card/{id}")
    public CreditCard findById(@PathVariable Integer id) {
        return creditCardService.findById(id);
    }

    @PostMapping("/account/credit-card")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountMV create(@RequestParam(name = "idOwner", required = false) Optional<Integer> id, @RequestParam(name = "idSecondary", required = false) Optional<Integer> idSecondary, @RequestBody @Valid CreditCardDto creditCardDto) {
        return creditCardService.create(id, idSecondary,creditCardDto);
    }

}
