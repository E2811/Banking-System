package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.Util.CheckingOrStudent;
import com.midterm.bankingSystem.controller.dto.CheckingDto;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.interfaces.CheckingController;
import com.midterm.bankingSystem.model.CheckingAccount;
import com.midterm.bankingSystem.service.CheckingService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
@Api(tags = "Checking Controller")
@RestController
public class CheckingControllerImpl implements CheckingController {

    @Autowired
    private CheckingService checkingService;
    @Autowired
    private CheckingOrStudent checkingOrStudent;

    @GetMapping("/account/checkings")
    public List<CheckingAccount> findAll() {
        return checkingService.findAll();
    }

    @GetMapping("/account/checking/{id}")
    public CheckingAccount findById(@PathVariable Integer id) {
        return checkingService.findById(id);
    }

    @PostMapping("/account/checking")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountMV create(@RequestParam(name = "idOwner", required = false) Optional<Integer> id, @RequestParam(name = "idSecondary", required = false) Optional<Integer> idSecondary, @RequestBody @Valid CheckingDto checkingDto) {
        return checkingOrStudent.createCheckingAccount(id, idSecondary, checkingDto);
    }

}
