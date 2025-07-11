package com.example.jwtauthenticator.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User registration response")
public record RegisterResponse(
    @Schema(description = "Success message", example = "User registered successfully. Please verify your email.")
    String message,
    
    @Schema(description = "Generated Brand ID", example = "MRTFY0001")
    String brandId,
    
    @Schema(description = "Generated User Code", example = "USER0001")
    String userCode,
    
    @Schema(description = "Username", example = "john_doe")
    String username,
    
    @Schema(description = "Email address", example = "john.doe@example.com")
    String email,
    
    @Schema(description = "Registration success status", example = "true")
    boolean success
) {}