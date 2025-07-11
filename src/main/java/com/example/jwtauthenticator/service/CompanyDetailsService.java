package com.example.jwtauthenticator.service;

import com.example.jwtauthenticator.dto.CompanyDetailsResponse;
import com.example.jwtauthenticator.dto.ForwardRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.concurrent.CompletableFuture;

/**
 * Service for handling asynchronous company details extraction
 */
@Service
public class CompanyDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyDetailsService.class);
    private static final String EXTERNAL_API_URL = "https://sumnode-main.onrender.com/api/extract-company-details";
    
    private final RestTemplate restTemplate;
    
    @Autowired
    public CompanyDetailsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    /**
     * Asynchronously forwards the URL to the external API for company details extraction
     * 
     * @param forwardRequest The request containing the URL to process
     * @return CompletableFuture containing the company details response
     */
    @Async("taskExecutor")
    public CompletableFuture<CompanyDetailsResponse> extractCompanyDetailsAsync(ForwardRequest forwardRequest) {
        logger.info("Starting asynchronous company details extraction for URL: {}", forwardRequest.url());
        
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "JWT-Authenticator/1.0");
            
            // Create request entity
            HttpEntity<ForwardRequest> requestEntity = new HttpEntity<>(forwardRequest, headers);
            
            // Make the external API call
            long startTime = System.currentTimeMillis();
            ResponseEntity<CompanyDetailsResponse> response = restTemplate.exchange(
                EXTERNAL_API_URL,
                HttpMethod.POST,
                requestEntity,
                CompanyDetailsResponse.class
            );
            long endTime = System.currentTimeMillis();
            
            logger.info("Company details extraction completed in {} ms for URL: {}", 
                       (endTime - startTime), forwardRequest.url());
            
            if (response.getBody() != null) {
                return CompletableFuture.completedFuture(response.getBody());
            } else {
                logger.warn("Received null response body from external API for URL: {}", forwardRequest.url());
                throw new RuntimeException("External API returned empty response");
            }
            
        } catch (HttpClientErrorException e) {
            logger.error("Client error occurred while calling external API for URL: {}. Status: {}, Response: {}", 
                        forwardRequest.url(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("External API client error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            
        } catch (HttpServerErrorException e) {
            logger.error("Server error occurred while calling external API for URL: {}. Status: {}, Response: {}", 
                        forwardRequest.url(), e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("External API server error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            
        } catch (ResourceAccessException e) {
            logger.error("Network error occurred while calling external API for URL: {}. Error: {}", 
                        forwardRequest.url(), e.getMessage());
            throw new RuntimeException("Network error while calling external API: " + e.getMessage());
            
        } catch (Exception e) {
            logger.error("Unexpected error occurred while calling external API for URL: {}. Error: {}", 
                        forwardRequest.url(), e.getMessage(), e);
            throw new RuntimeException("Unexpected error while processing request: " + e.getMessage());
        }
    }
}