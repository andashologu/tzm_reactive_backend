package com.trademarket.tzm.user.model;

import jakarta.validation.constraints.NotEmpty;

public class Address {
    @NotEmpty(message = "country cannot be empty")
    private String country, city;
    private String region; // Generalized to cover both 'state' and 'suburb' /*PDNE*/
    private String street, zip;

    public Address(){}

    public Address(String country, String city, String region, String street, String zip) {
        this.country = country;
        this.city = city;
        this.region = region;
        this.street = street;
        this.zip = zip;
    }

    public String getCountry() {  return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getZip() {  return zip; }
    public void setZip(String zip) {  this.zip = zip; }

    @Override
    public String toString() {
        return "Address{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", region='" + region + '\'' +
                ", street='" + street + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }
}