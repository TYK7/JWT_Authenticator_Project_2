package com.example.jwtauthenticator.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordConfirmRequest(
    @NotBlank
    String token,
    @NotBlank
    String newPassword
) {}
