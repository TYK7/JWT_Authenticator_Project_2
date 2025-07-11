package com.example.jwtauthenticator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Set new password request")
public record SetNewPasswordRequest(
    @Schema(description = "Email address", example = "user@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    @Schema(description = "Verification code", example = "123456")
    @NotBlank(message = "Verification code is required")
    String code,
    
    @Schema(description = "New password", example = "NewSecurePassword123!")
    @NotBlank(message = "New password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String newPassword,
    
    @Schema(description = "Brand ID", example = "brand1")
    @NotBlank(message = "Brand ID is required")
    String brandId
) {}