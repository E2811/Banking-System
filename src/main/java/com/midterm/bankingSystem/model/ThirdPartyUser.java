package com.midterm.bankingSystem.model;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

@Entity
public class ThirdPartyUser extends User {

    @NotNull
    private String hashedKey;

    public String getHashedKey() {
        return hashedKey;
    }

    public void setHashedKey(String hashedKey) {
        this.hashedKey = hashedKey;
    }
}


