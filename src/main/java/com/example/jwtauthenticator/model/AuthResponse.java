package com.example.jwtauthenticator.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "Authentication response containing JWT tokens")
public class AuthResponse implements Serializable {

    private static final long serialVersionUID = -8091879091924046844L;
    
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private final String jwttoken;
    
    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    public AuthResponse(String jwttoken) {
        this.jwttoken = jwttoken;
    }

    public AuthResponse(String jwttoken, String refreshToken) {
        this.jwttoken = jwttoken;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return this.jwttoken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}