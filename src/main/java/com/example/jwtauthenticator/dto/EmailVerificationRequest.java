package com.example.jwtauthenticator.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class EmailVerificationRequest {
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String verificationCode;
}
