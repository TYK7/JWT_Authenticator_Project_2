package com.example.jwtauthenticator.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleSignInRequest(
    @NotBlank(message = "ID token is required")
    String idToken
) {}