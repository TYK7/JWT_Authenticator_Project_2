package com.example.jwtauthenticator.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "User registration request")
public record RegisterRequest(
    @Schema(description = "Username for the new account", example = "john_doe")
    @NotBlank(message = "Username is required")
    String username,
    
    @Schema(description = "Password for the new account", example = "SecurePassword123!")
    @NotBlank(message = "Password is required")
    String password,
    
    @Schema(description = "Email address for the new account", example = "john.doe@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    @Schema(description = "User's first name", example = "John")
    String firstName,
    
    @Schema(description = "User's last name", example = "Doe")
    String lastName,
    
    @Schema(description = "User's phone number", example = "+1234567890")
    String phoneNumber,
    
    @Schema(description = "User's location (optional)", example = "New York, USA")
    String location,
    
    @Schema(description = "Brand ID for multi-brand support (optional - will be auto-generated if not provided)", example = "MRTFY0001")
    String brandId
) {}