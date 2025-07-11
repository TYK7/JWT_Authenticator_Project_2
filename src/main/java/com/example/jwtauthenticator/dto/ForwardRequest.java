package com.example.jwtauthenticator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for the /forward endpoint
 */
public record ForwardRequest(
    @NotBlank(message = "URL is required")
    @Pattern(regexp = "^https?://.*", message = "URL must be a valid HTTP or HTTPS URL")
    String url
) {}