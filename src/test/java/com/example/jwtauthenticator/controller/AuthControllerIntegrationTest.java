package com.example.jwtauthenticator.controller;

import com.example.jwtauthenticator.config.TestConfig;
import com.example.jwtauthenticator.model.AuthRequest;
import com.example.jwtauthenticator.model.AuthResponse;
import com.example.jwtauthenticator.model.RegisterRequest;
import com.example.jwtauthenticator.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // Use a test profile for in-memory database
@Import(TestConfig.class)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Clear database before each test
    }

    @Test
    void registerUser_success() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password");
        registerRequest.setEmail("test@example.com");
        registerRequest.setTenantId("tenant1");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("User registered successfully. Please verify your email."));
    }

    @Test
    void loginUser_success() throws Exception {
        // Register and verify user first
        registerUser_success(); // This will register the user
        // Manually verify email for integration test simplicity
        userRepository.findByUsernameAndTenantId("testuser", "tenant1").ifPresent(user -> {
            user.setEmailVerified(true);
            userRepository.save(user);
        });

        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");
        authRequest.setTenantId("tenant1");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    // Add more integration tests for other endpoints (refresh, forgot-password, etc.)
}
