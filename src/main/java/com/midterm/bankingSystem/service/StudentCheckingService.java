package com.midterm.bankingSystem.service;

import com.midterm.bankingSystem.controller.dto.RequestDto;
import com.midterm.bankingSystem.enums.Status;
import com.midterm.bankingSystem.exception.*;
import com.midterm.bankingSystem.model.StudentChecking;
import com.midterm.bankingSystem.model.Transaction;
import com.midterm.bankingSystem.model.User;
import com.midterm.bankingSystem.repository.StudentCheckingRepository;
import com.midterm.bankingSystem.repository.TransactionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentCheckingService {

    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    private static final Logger LOGGER = LogManager.getLogger(StudentCheckingService.class);
    @Secured({"ROLE_ADMIN"})
    public List<StudentChecking> findAll(){
        LOGGER.info("[INIT] -findAll student checking accounts");
        return studentCheckingRepository.findAll();
    }

    public StudentChecking findById(Integer id){
        LOGGER.info("[INIT] -find a student checking account by id");
        return studentCheckingRepository.findById(id).orElseThrow(()->new DataNotFoundException("StudentChecking account not found"));
    }

    public StudentChecking create(StudentChecking studentChecking){
        LOGGER.info("[INIT] -create a student checking account");
        return studentCheckingRepository.save(studentChecking);
    }

    public void changeBalance(User user, RequestDto requestDto){
        LOGGER.info("[INIT]- change balance student checking Account");
        StudentChecking studentChecking = findById(requestDto.getAccountId());
        if (user.getRoles().size()==1 && user.getRoles().toString().contains("ROLE_THIRDPARTY")){
            if (requestDto.getSecretKey()==null){
                throw new NotEnoughData("The account secret key must be provided");
            }else if (!requestDto.getSecretKey().equals(studentChecking.getSecretKey())){
                throw new InvalidAccountUser("Invalid secret key");
            }
        }
        switch (requestDto.getAction().toLowerCase()){
            case "credit":
                LOGGER.info("increase balance student checking Account");
                studentChecking.getBalance().increaseAmount(requestDto.getAmount());
                break;
            case "debit":
                LOGGER.info("decrease balance checking Account, check if the balance is enough");
                if (studentChecking.getBalance().getAmount().compareTo(requestDto.getAmount())==-1) {
                    LOGGER.error("Balance not enough");
                    throw new LowBalance("Balance not enough");
                }else if (studentChecking.getStatus()== Status.FROZEN){
                    LOGGER.error("Account frozen");
                    throw new FraudDetection("The account is frozen");
                }else{
                    LOGGER.info("decrease balance checking Account");
                    studentChecking.getBalance().decreaseAmount(requestDto.getAmount());
                }
                break;
        }
        transactionRepository.save(new Transaction(studentChecking, requestDto.getAmount()));
        studentCheckingRepository.save(studentChecking);
        LOGGER.info("Transaction made by "+user.getId()+" finished at "+ LocalDateTime.now());
        LOGGER.info("[EXIT]- change balance student checking Account");
    }
}
