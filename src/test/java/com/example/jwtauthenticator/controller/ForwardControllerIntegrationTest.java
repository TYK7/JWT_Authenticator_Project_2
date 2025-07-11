package com.example.jwtauthenticator.controller;

import com.example.jwtauthenticator.dto.ForwardRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ForwardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void forwardRequest_validationError() throws Exception {
        ForwardRequest invalidRequest = new ForwardRequest("invalid-url");

        mockMvc.perform(post("/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forwardRequest_emptyBody() throws Exception {
        mockMvc.perform(post("/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void health_endpoint() throws Exception {
        mockMvc.perform(get("/forward/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Forward service is running"));
    }

    // Note: We don't test the actual external API call in integration tests
    // as it would make the test dependent on external services and slow
}