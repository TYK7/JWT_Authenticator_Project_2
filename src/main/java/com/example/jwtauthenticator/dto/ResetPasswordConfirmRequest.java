package com.example.jwtauthenticator.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordConfirmRequest {
    @NotBlank
    private String token;
    @NotBlank
    private String newPassword;
}
