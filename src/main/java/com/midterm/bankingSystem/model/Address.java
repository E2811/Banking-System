package com.midterm.bankingSystem.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class Address {

    @NotNull
    private String street;
    @NotNull
    private String country;
    @NotNull
    private String city;
    @NotNull
    private Integer postalCode;

    public Address() {
    }

    public Address(@NotNull String street, @NotNull String country, @NotNull String city, @NotNull Integer postalCode) {
        this.street = street;
        this.country = country;
        this.city = city;
        this.postalCode = postalCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postalCode) {
        this.postalCode = postalCode;
    }
}
