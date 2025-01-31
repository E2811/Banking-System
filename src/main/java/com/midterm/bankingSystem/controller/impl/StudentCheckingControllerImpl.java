package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.interfaces.StudentCheckingController;
import com.midterm.bankingSystem.model.Saving;
import com.midterm.bankingSystem.model.StudentChecking;
import com.midterm.bankingSystem.service.StudentCheckingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "StudentChecking Account Controller")
@RestController
@RequestMapping("/")
public class StudentCheckingControllerImpl implements StudentCheckingController {

    @Autowired
    private StudentCheckingService studentCheckingService;

    @GetMapping("/account/student-checkings")
    @ApiOperation(value = "Find all student-checking accounts",
            response = StudentChecking.class)
    @ResponseStatus(HttpStatus.OK)
    public List<StudentChecking> findAll() {
        return studentCheckingService.findAll();
    }

    @GetMapping("/account/student-checking/{id}")
    @ApiOperation(value = "Find a student-checking account by its id",
            response = StudentChecking.class)
    @ResponseStatus(HttpStatus.OK)
    public StudentChecking findById(@PathVariable Integer id) {
        return studentCheckingService.findById(id);
    }


}
