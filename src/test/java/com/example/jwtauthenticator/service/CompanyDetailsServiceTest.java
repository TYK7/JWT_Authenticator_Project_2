package com.example.jwtauthenticator.service;

import com.example.jwtauthenticator.dto.CompanyDetailsResponse;
import com.example.jwtauthenticator.dto.ForwardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyDetailsServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CompanyDetailsService companyDetailsService;

    private ForwardRequest forwardRequest;
    private CompanyDetailsResponse mockResponse;

    @BeforeEach
    void setUp() {
        forwardRequest = new ForwardRequest("https://xamplify.com");
        
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
    void extractCompanyDetailsAsync_success() throws ExecutionException, InterruptedException {
        ResponseEntity<CompanyDetailsResponse> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(CompanyDetailsResponse.class)
        )).thenReturn(responseEntity);

        CompletableFuture<CompanyDetailsResponse> future = companyDetailsService.extractCompanyDetailsAsync(forwardRequest);
        CompanyDetailsResponse result = future.get();

        assertNotNull(result);
        assertEquals("xAmplify", result.company().name());
        assertEquals("Software Development", result.company().industry());
        assertEquals(71.995, result.performance().extractionTimeSeconds());
    }

    @Test
    void extractCompanyDetailsAsync_httpClientError() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(CompanyDetailsResponse.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        assertThrows(RuntimeException.class, () -> {
            CompletableFuture<CompanyDetailsResponse> future = companyDetailsService.extractCompanyDetailsAsync(forwardRequest);
            try {
                future.get();
            } catch (ExecutionException e) {
                throw (RuntimeException) e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void extractCompanyDetailsAsync_httpServerError() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(CompanyDetailsResponse.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error"));

        assertThrows(RuntimeException.class, () -> {
            CompletableFuture<CompanyDetailsResponse> future = companyDetailsService.extractCompanyDetailsAsync(forwardRequest);
            try {
                future.get();
            } catch (ExecutionException e) {
                throw (RuntimeException) e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void extractCompanyDetailsAsync_networkError() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(CompanyDetailsResponse.class)
        )).thenThrow(new ResourceAccessException("Network timeout"));

        assertThrows(RuntimeException.class, () -> {
            CompletableFuture<CompanyDetailsResponse> future = companyDetailsService.extractCompanyDetailsAsync(forwardRequest);
            try {
                future.get();
            } catch (ExecutionException e) {
                throw (RuntimeException) e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void extractCompanyDetailsAsync_nullResponse() {
        ResponseEntity<CompanyDetailsResponse> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
        
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.POST),
            any(HttpEntity.class),
            eq(CompanyDetailsResponse.class)
        )).thenReturn(responseEntity);

        assertThrows(RuntimeException.class, () -> {
            CompletableFuture<CompanyDetailsResponse> future = companyDetailsService.extractCompanyDetailsAsync(forwardRequest);
            try {
                future.get();
            } catch (ExecutionException e) {
                throw (RuntimeException) e.getCause();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        });
    }
}