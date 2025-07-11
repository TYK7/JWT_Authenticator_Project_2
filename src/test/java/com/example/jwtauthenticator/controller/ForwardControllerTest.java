package com.example.jwtauthenticator.controller;

import com.example.jwtauthenticator.config.AsyncConfig;
import com.example.jwtauthenticator.dto.CompanyDetailsResponse;
import com.example.jwtauthenticator.dto.ForwardRequest;
import com.example.jwtauthenticator.service.CompanyDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ForwardController.class)
@Import({AsyncConfig.class})
class ForwardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyDetailsService companyDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private ForwardRequest validRequest;
    private CompanyDetailsResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new ForwardRequest("https://xamplify.com");
        
        // Create mock response
        CompanyDetailsResponse.LogoInfo logoInfo = new CompanyDetailsResponse.LogoInfo(
            "https://xamplify.com/logo.png", null, "https://xamplify.com/icon.png", null, null
        );
        
        CompanyDetailsResponse.ColorInfo colorInfo = new CompanyDetailsResponse.ColorInfo(
            "#cc3366", "rgb(204,51,102)", 103, "link_text"
        );
        
        CompanyDetailsResponse.FontInfo fontInfo = new CompanyDetailsResponse.FontInfo(
            "-apple-system", "heading", "-apple-system, BlinkMacSystemFont"
        );
        
        CompanyDetailsResponse.ImageInfo imageInfo = new CompanyDetailsResponse.ImageInfo(
            "https://xamplify.com/400", "og:image:width"
        );
        
        CompanyDetailsResponse.CompanyInfo companyInfo = new CompanyDetailsResponse.CompanyInfo(
            "xAmplify", "Test Description", "Software Development", "Fremont, California",
            "2018", null, null, "https://xamplify.com/", 
            Map.of("Twitter", "https://twitter.com/xamplifys"), "11-50 employees",
            "Fremont, California", "Privately Held", "channel ecosystem"
        );
        
        CompanyDetailsResponse.PerformanceInfo performanceInfo = new CompanyDetailsResponse.PerformanceInfo(
            71.995, "2025-07-10T15:59:09.969Z"
        );
        
        mockResponse = new CompanyDetailsResponse(
            logoInfo, List.of(colorInfo), List.of(fontInfo), List.of(imageInfo),
            companyInfo, performanceInfo, "Data extracted dynamically"
        );
    }

    @Test
    void forwardRequest_success() throws Exception {
        when(companyDetailsService.extractCompanyDetailsAsync(any(ForwardRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(mockResponse));

        mockMvc.perform(post("/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.Company.Name").value("xAmplify"))
                .andExpect(jsonPath("$.Company.Industry").value("Software Development"))
                .andExpect(jsonPath("$._performance.extractionTimeSeconds").value(71.995));
    }

    @Test
    void forwardRequest_invalidUrl() throws Exception {
        ForwardRequest invalidRequest = new ForwardRequest("invalid-url");

        mockMvc.perform(post("/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forwardRequest_emptyUrl() throws Exception {
        ForwardRequest emptyRequest = new ForwardRequest("");

        mockMvc.perform(post("/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void forwardRequest_serviceException() throws Exception {
        when(companyDetailsService.extractCompanyDetailsAsync(any(ForwardRequest.class)))
            .thenReturn(CompletableFuture.failedFuture(new RuntimeException("External API error")));

        mockMvc.perform(post("/forward")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadGateway());
    }

    @Test
    void health_success() throws Exception {
        mockMvc.perform(get("/forward/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Forward service is running"));
    }
}