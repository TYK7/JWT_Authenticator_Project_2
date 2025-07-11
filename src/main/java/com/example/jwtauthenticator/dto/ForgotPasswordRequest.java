package com.example.jwtauthenticator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Forgot password request")
public record ForgotPasswordRequest(
    @Schema(description = "Email address to send verification code", example = "user@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    @Schema(description = "Brand ID", example = "brand1")
    @NotBlank(message = "Brand ID is required")
    String brandId
) {}