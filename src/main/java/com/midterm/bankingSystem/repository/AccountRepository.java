package com.midterm.bankingSystem.repository;

import com.midterm.bankingSystem.model.Account;
import com.midterm.bankingSystem.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {
    List<Account> findByPrimaryOwner(AccountHolder accountHolder);
    @Query("SELECT a FROM Account a WHERE (primary_owner_id =:owner OR secondary_owner_id =:owner) AND a.id =:idAc")
    public Account findAccountById(@Param("owner") Integer ownerId, @Param("idAc") Integer accountId);
}
