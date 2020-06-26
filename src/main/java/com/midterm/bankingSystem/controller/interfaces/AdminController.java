package com.midterm.bankingSystem.controller.interfaces;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.model.ThirdPartyUser;
import com.midterm.bankingSystem.model.User;


public interface AdminController {
    public ThirdPartyUser create(ThirdPartyUser thirdPartyUser);
    public void changeBalance(User user, RequestDto requestDto);
    public void changeStatus(Integer accountId);
}


