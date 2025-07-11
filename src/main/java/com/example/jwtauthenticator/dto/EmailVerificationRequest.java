package com.example.jwtauthenticator.dto;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
public class EmailVerificationRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String verificationCode;
}
