package com.example.jwtauthenticator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Authentication request")
public record AuthRequest(
    @Schema(description = "Username for authentication", example = "john_doe")
    @NotBlank(message = "Username is required")
    String username,
    
    @Schema(description = "Password for authentication", example = "SecurePassword123!")
    @NotBlank(message = "Password is required")
    String password,
    
    @Schema(description = "Brand ID for multi-brand support", example = "brand1")
    String brandId
) {
    public AuthRequest(String username, String password) {
        this(username, password, null);
    }
}