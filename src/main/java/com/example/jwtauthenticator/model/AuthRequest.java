package com.example.jwtauthenticator.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Authentication request")
public class AuthRequest implements Serializable {

    private static final long serialVersionUID = 59264685835158092L;

    @Schema(description = "Username for authentication", example = "john_doe")
    private String username;
    
    @Schema(description = "Password for authentication", example = "SecurePassword123!")
    private String password;
    
    @Schema(description = "Tenant ID for multi-tenant support", example = "tenant1")
    private String tenantId;

    public AuthRequest() {
    }

    public AuthRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}