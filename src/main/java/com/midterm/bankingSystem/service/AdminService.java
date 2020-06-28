package com.midterm.bankingSystem.service;
import com.midterm.bankingSystem.controller.dto.AccountMV;
import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.DataNotFoundException;
import com.midterm.bankingSystem.exception.NotEnoughData;
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
  @Autowired
  private AccountRepository accountRepository;


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
    @Secured({"ROLE_ADMIN"})
    public void changeStatus(Integer accountId){
        LOGGER.info("[INIT] - change Status to active");
        Account account = accountRepository.findById(accountId).orElseThrow(()-> new DataNotFoundException("Account not found"));
        if(account instanceof CheckingAccount){
            ((CheckingAccount) account).check();
            LOGGER.info("Account Checking has been found and checked");
            if(((CheckingAccount) account).getStatus().equals(Status.ACTIVE)){
                LOGGER.error("Checking account status is already ACTIVE");
                throw new NotEnoughData("Checking account status is already ACTIVE");
            } else {
                ((CheckingAccount) account).setStatus(Status.ACTIVE);
                LOGGER.info("[END] - change Status to Active");
                accountRepository.save(account);
                return;
            }
        }
        if(account instanceof StudentChecking){
            LOGGER.info("Account Student has been found");
            if(((StudentChecking) account).getStatus().equals(Status.ACTIVE)){
                LOGGER.error("Student account status is already ACTIVE");
                throw new NotEnoughData("Student account status is already ACTIVE");
            } else {
                ((StudentChecking) account).setStatus(Status.ACTIVE);
                LOGGER.info("[END] - change Status to Active");
                accountRepository.save(account);
                return;
            }
        }
        if(account instanceof Saving){
            ((Saving) account).check();
            if(((Saving) account).getStatus().equals(Status.ACTIVE)){
                LOGGER.error("Saving account status is already ACTIVE");
                throw new NotEnoughData("Saving account status is already ACTIVE");
            } else {
                LOGGER.info("Account Saving has been found and checked");
                ((Saving) account).setStatus(Status.ACTIVE);
                LOGGER.info("[END] - change Status to Active");
                accountRepository.save(account);
                return;
            }
        }
        LOGGER.error("Fail update");
        throw new NotEnoughData("Update was not possible");
    }
}
