package com.midterm.bankingSystem.controller.impl;

import com.midterm.bankingSystem.controller.interfaces.StudentCheckingController;
import com.midterm.bankingSystem.model.StudentChecking;
import com.midterm.bankingSystem.service.StudentCheckingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentCheckingControllerImpl implements StudentCheckingController {

    @Autowired
    private StudentCheckingService studentCheckingService;

    @GetMapping("/account/student-checkings")
    public List<StudentChecking> findAll() {
        return studentCheckingService.findAll();
    }

    @GetMapping("/account/student-checking/{id}")
    public StudentChecking findById(@PathVariable Integer id) {
        return studentCheckingService.findById(id);
    }


}
