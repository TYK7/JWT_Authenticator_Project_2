package com.example.jwtauthenticator.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TfaRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String code;
}
