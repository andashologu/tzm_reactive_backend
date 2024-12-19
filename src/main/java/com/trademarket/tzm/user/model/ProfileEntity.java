package com.trademarket.tzm.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.trademarket.tzm.user.validation.UniqueUserId;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Table("profiles")
public class ProfileEntity {

    @Id
    private Long id;

    @NotNull(message = "User ID is required")
    @UniqueUserId
    private Long userId;

    @Size(max = 50, message = "Fist name must be at most 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "First name must only contain letters")
    private String firstname;
    
    @Pattern(regexp = "^[A-Za-z]+$", message = "Last name must only contain letters")
    @Size(max = 50, message = "Last name must be at most 50 characters")
    @Nullable
    private String lastname;

    @Column("profile_picture")
    @Size(max = 100, message = "Url must be at most 100 characters")
    String profilePicture;
    
    @Size(max = 255, message = "Bio must not exceed 255 characters")
    String bio; 
    
    @Pattern(regexp = "^\\+?[0-9\\-\\(\\)\\s]+$", message = "Phone number can only contain digits, spaces, dashes, parentheses, and an optional leading '+'.")
    String phone;
   
    @Pattern(
        regexp = "^(https?:\\/\\/)?([\\w\\-]+\\.)+[\\w\\-]+(/[\\w\\-./?%&=]*)?$", 
        message = "Website must be a valid URL"
    )
    @Size(max = 250, message = "Website must not exceed 250 characters")
    String website;

    @Valid
    private Address address;

    @Valid
    private Preferences preferences;

    @Valid
    private Settings settings;

    private LocalDate createdAt, updatedAt;

    public ProfileEntity() {
        this.updatedAt = LocalDate.now();
    }

    public ProfileEntity(Long id, Long userId, String firstname, String lastname, String profilePicture, String bio,
                         String phone, String website, Address address, Preferences preferences, 
                         Settings settings) {
        this.id = id;
        this.userId = userId;
        this.firstname = firstname;
        this.lastname = lastname;
        this.profilePicture = profilePicture;
        this.bio = bio;
        this.phone = phone;
        this.website = website;
        this.address = address;
        this.preferences = preferences;
        this.settings = settings;
        this.updatedAt = LocalDate.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }

    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    public Preferences getPreferences() { return preferences; }
    public void setPreferences(Preferences preferences) { this.preferences = preferences; }

    public Settings getSettings() { return settings; }
    public void setSettings(Settings settings) { this.settings = settings; }

    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }

    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "ProfileEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", firstname='" + firstname +
                ", lastname='" + lastname +
                ", profilePicture='" + profilePicture +
                ", bio='" + bio +
                ", phone='" + phone +
                ", website='" + website +
                ", address=" + address +
                ", preferences=" + preferences +
                ", settings=" + settings +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
