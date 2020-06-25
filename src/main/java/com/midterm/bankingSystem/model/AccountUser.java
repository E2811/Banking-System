package com.midterm.bankingSystem.model;

import javax.persistence.Entity;

@Entity
public class AccountUser extends User {
    public AccountUser() {
    }

    public AccountUser(String username, String password) {
        super(username, password);
    }
}
