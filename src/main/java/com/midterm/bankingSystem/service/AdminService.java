package com.midterm.bankingSystem.service;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.UserWithNameUsed;
import com.midterm.bankingSystem.model.*;
import com.midterm.bankingSystem.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

  @Autowired
  private UserRepository userRepo;
  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private CheckingService checkingService;
  @Autowired
  private CreditCardService creditCardService;
  @Autowired
  private SavingService savingService;
  @Autowired
  private StudentCheckingService studentCheckingService;

  private final Logger LOGGER = LogManager.getLogger(AdminService.class);

  
  @Secured({"ROLE_ADMIN"})
  public List<User> getAllUserAccounts () {
    return userRepo.findAll();
  }

  @Secured({"ROLE_ADMIN"})
  public ThirdPartyUser create(ThirdPartyUser thirdPartyUser){
      LOGGER.info("[INIT]- create third-party user");
      PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
      if (userRepo.findByUsername(thirdPartyUser.getUsername())!=null){
          LOGGER.error("username already used");
          throw new UserWithNameUsed("Username "+thirdPartyUser.getUsername()+ " already used");
      }
      thirdPartyUser.setPassword(passwordEncoder.encode(thirdPartyUser.getHashedKey()));
      ThirdPartyUser thirdPartyUser1 = userRepo.save(thirdPartyUser);
      Role role = new Role("ROLE_THIRDPARTY",thirdPartyUser);
      roleRepository.save(role);
      LOGGER.info("[EXIT]- create third-party user");
      return thirdPartyUser1;
  }

  @Secured({"ROLE_ADMIN","ROLE_THIRDPARTY"})
  public void changeBalance(User user,RequestDto requestDto){
      LOGGER.info("[INIT]- change balance, search account");

      switch (requestDto.getTypeAccount().toString()){
          case "checking":
              checkingService.changeBalance(user,requestDto);
              break;
          case "saving":
              savingService.changeBalance(user,requestDto);
              break;
          case "creditcard":
              creditCardService.changeBalance(user,requestDto);
              break;
          case "studentchecking":
              studentCheckingService.changeBalance(user,requestDto);
              break;
      }
  }

}
