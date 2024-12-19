package com.trademarket.tzm.user.model;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Address {

    @Pattern(
        regexp = "^[A-Za-z]+(?:[\\s-][A-Za-z]+)*$", 
        message = "Country must only contain letters, spaces, or hyphens"
    )
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Pattern(
        regexp = "^[A-Za-z]+(?:[\\s-][A-Za-z]+)*$", 
        message = "City must only contain letters, spaces, or hyphens"
    )
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;

    @Pattern(
        regexp = "^[A-Za-z]+(?:[\\s-][A-Za-z]+)*$", 
        message = "Region must only contain letters, spaces, or hyphens"
    )
    @Size(max = 100, message = "Region must not exceed 100 characters")
    private String region; // Generalized to cover both 'state' and 'suburb' /*PDNE*/

    @Pattern(
        regexp = "^[A-Za-z0-9]+(?:[\\s,-/][A-Za-z0-9]+)*$", 
        message = "Street must only contain letters, numbers, spaces, commas, hyphens, or slashes"
    )
    @Size(max = 200, message = "Street must not exceed 200 characters")
    private String street;

    @Pattern(
        regexp = "^[A-Za-z0-9-]+$", 
        message = "Zip must only contain letters, numbers, or hyphens"
    )
    @Size(max = 20, message = "Zip must not exceed 20 characters")
    private String zip;

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