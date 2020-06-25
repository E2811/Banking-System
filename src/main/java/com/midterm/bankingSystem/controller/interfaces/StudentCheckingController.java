package com.midterm.bankingSystem.controller.interfaces;

import com.midterm.bankingSystem.model.StudentChecking;

import java.util.List;

public interface StudentCheckingController {
    public List<StudentChecking> findAll();
    public StudentChecking findById(Integer id);
}
