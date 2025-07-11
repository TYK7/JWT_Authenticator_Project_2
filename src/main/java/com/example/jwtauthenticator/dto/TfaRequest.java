package com.example.jwtauthenticator.dto;

import jakarta.validation.constraints.NotBlank;

public record TfaRequest(
    @NotBlank
    String username,
    @NotBlank
    String code
) {}
