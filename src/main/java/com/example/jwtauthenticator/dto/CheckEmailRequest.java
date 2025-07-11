package com.example.jwtauthenticator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Check email existence request")
public record CheckEmailRequest(
    @Schema(description = "Email address to check", example = "user@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    @Schema(description = "Brand ID", example = "brand1")
    @NotBlank(message = "Brand ID is required")
    String brandId
) {}