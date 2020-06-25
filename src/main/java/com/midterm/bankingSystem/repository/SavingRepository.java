package com.midterm.bankingSystem.repository;

import com.midterm.bankingSystem.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavingRepository extends JpaRepository<Saving, Integer> {
}
