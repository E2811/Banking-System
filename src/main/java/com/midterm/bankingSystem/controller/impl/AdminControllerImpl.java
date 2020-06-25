package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.controller.interfaces.AdminController;
import com.midterm.bankingSystem.model.AccountUser;
import com.midterm.bankingSystem.model.ThirdPartyUser;
import com.midterm.bankingSystem.model.User;
import com.midterm.bankingSystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AdminControllerImpl  implements AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/third-party")
    @ResponseStatus(HttpStatus.CREATED)
    public ThirdPartyUser create(@RequestBody @Valid ThirdPartyUser thirdPartyUser) {
        return adminService.create(thirdPartyUser);
    }

    @GetMapping("/users")
    public List<User> findAll(){
        return adminService.getAllUserAccounts();
    }

    @PatchMapping("/account/transaction")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeBalance(@AuthenticationPrincipal User user, @RequestBody @Valid RequestDto requestDto) {
        adminService.changeBalance(user, requestDto);
    }
}
