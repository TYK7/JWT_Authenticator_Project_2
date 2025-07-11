package com.example.jwtauthenticator.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Verify reset code request")
public record VerifyCodeRequest(
    @Schema(description = "Email address", example = "user@example.com")
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    String email,
    
    @Schema(description = "Verification code", example = "123456")
    @NotBlank(message = "Verification code is required")
    String code,
    
    @Schema(description = "Brand ID", example = "brand1")
    @NotBlank(message = "Brand ID is required")
    String brandId
) {}