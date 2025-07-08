package com.example.jwtauthenticator.model;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Schema(description = "User registration request")
public class RegisterRequest implements Serializable {
    
    @Schema(description = "Username for the new account", example = "john_doe")
    @NotBlank
    private String username;
    
    @Schema(description = "Password for the new account", example = "SecurePassword123!")
    @NotBlank
    private String password;
    
    @Schema(description = "Email address for the new account", example = "john.doe@example.com")
    @Email
    @NotBlank
    private String email;
    
    @Schema(description = "User's location (optional)", example = "New York, USA")
    private String location;
    
    @Schema(description = "Tenant ID for multi-tenant support", example = "tenant1")
    @NotBlank
    private String tenantId;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}