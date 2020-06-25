package com.midterm.bankingSystem.repository;

import com.midterm.bankingSystem.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

    @Query(value = "SELECT MAX(t.date_transaction) FROM transaction t WHERE sender_account_id= :id" ,nativeQuery = true)
    public LocalDateTime lastTransaction(@Param("id") Integer id);

    @Query(value =  "SELECT SUM(t.amount) FROM transaction t where CAST(t.date_transaction AS DATE) != CAST(:date AS DATE) AND sender_account_id=:id group by CAST(t.date_transaction AS DATE) ORDER BY SUM(t.amount) DESC limit 1" ,nativeQuery = true)
    public BigDecimal highestTransaction(@Param("date") LocalDateTime date, @Param("id") Integer id);

    @Query(value =  "SELECT SUM(t.amount) FROM transaction t  where CAST(t.date_transaction AS DATE)= CAST(:date AS DATE) AND sender_account_id=:id ORDER BY SUM(t.amount) DESC limit 1" ,nativeQuery = true)
    public BigDecimal highestTransactionOwner(@Param("date") LocalDateTime date, @Param("id") Integer id);


}
