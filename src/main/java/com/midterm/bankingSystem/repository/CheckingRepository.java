package com.midterm.bankingSystem.repository;

import com.midterm.bankingSystem.model.CheckingAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckingRepository extends JpaRepository<CheckingAccount, Integer> {
}
