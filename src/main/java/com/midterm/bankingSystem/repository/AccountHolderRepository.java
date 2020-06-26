package com.midterm.bankingSystem.repository;

import com.midterm.bankingSystem.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountHolderRepository extends JpaRepository<AccountHolder, Integer> {
    AccountHolder findByName(String name);
}
