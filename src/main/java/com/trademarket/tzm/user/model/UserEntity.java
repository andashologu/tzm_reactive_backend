package com.trademarket.tzm.user.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import com.trademarket.tzm.user.validation.UniqueEmail;
import com.trademarket.tzm.user.validation.UniqueUsername;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Table("users")
public class UserEntity {
    @Id
    private Long id;

    @NotBlank(message = "Username cannot be blank")
    @Pattern(
        regexp = "^(?!\\d+$)[a-zA-Z0-9][a-zA-Z0-9_]{0,29}$",
        message = "Username can only include letters, numbers, and underscores, and must be 1-30 characters long."
    )
    @UniqueUsername
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be a valid format")
    @UniqueEmail
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
        message = "Password must be at least 8 characters long, include at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String password;

    private Boolean active;

    public UserEntity(){}

    public UserEntity(Long id, String username, String email, String password, Boolean active) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.active = active;
    }

    public UserEntity(Long id, String username, String email, Boolean active) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "UserEntity{" +
            "id=" + id +
            ", username='" + username + '\'' +
            ", email='" + email + '\'' +
            ", active=" + active +
            '}';
    }
}

