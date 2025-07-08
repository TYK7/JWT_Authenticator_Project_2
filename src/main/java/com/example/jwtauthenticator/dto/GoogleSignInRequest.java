package com.example.jwtauthenticator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoogleSignInRequest {
    
    @NotBlank(message = "ID token is required")
    private String idToken;
}