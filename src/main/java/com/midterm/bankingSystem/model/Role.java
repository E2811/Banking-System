package com.midterm.bankingSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String role;

    @ManyToOne
    @JsonIgnore
    private User user;

    public Role() {
    }

    public Role(String role, User user) {
        this.role = role;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", authority='" + role + '\'';
    }
}