package com.example.jwtauthenticator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(description = "Profile update request")
public record ProfileUpdateRequest(
    @Schema(description = "User's first name", example = "John")
    String firstName,
    
    @Schema(description = "User's last name", example = "Doe")
    String lastName,
    
    @Schema(description = "User's phone number", example = "+1234567890")
    String phoneNumber,
    
    @Schema(description = "User's location", example = "New York, USA")
    String location,
    
    @Schema(description = "User's email address", example = "john.doe@example.com")
    @Email(message = "Email should be valid")
    String email
) {}