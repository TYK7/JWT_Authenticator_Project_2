package com.example.jwtauthenticator.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ProtectedController {

    @GetMapping("/protected")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public String protectedEndpoint() {
        return "This is a protected endpoint!";
    }
}
