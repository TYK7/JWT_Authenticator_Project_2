package com.example.jwtauthenticator.controller;

import com.example.jwtauthenticator.dto.CompanyDetailsResponse;
import com.example.jwtauthenticator.dto.ForwardRequest;
import com.example.jwtauthenticator.service.CompanyDetailsService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controller for handling forward requests to external company details API
 */
@RestController
@RequestMapping("/forward")
@CrossOrigin(origins = "*")
public class ForwardController {
    
    private static final Logger logger = LoggerFactory.getLogger(ForwardController.class);
    
    private final CompanyDetailsService companyDetailsService;
    
    @Autowired
    public ForwardController(CompanyDetailsService companyDetailsService) {
        this.companyDetailsService = companyDetailsService;
    }
    
    /**
     * Forwards URL to external API for company details extraction
     * 
     * @param forwardRequest Request containing the URL to process
     * @return CompanyDetailsResponse from the external API
     */
    @PostMapping
    public CompletableFuture<ResponseEntity<CompanyDetailsResponse>> forwardRequest(
            @Valid @RequestBody ForwardRequest forwardRequest) {
        
        logger.info("Received forward request for URL: {}", forwardRequest.url());
        
        return companyDetailsService.extractCompanyDetailsAsync(forwardRequest)
            .thenApply(response -> {
                logger.info("Successfully processed forward request for URL: {}", forwardRequest.url());
                return ResponseEntity.ok(response);
            })
            .exceptionally(throwable -> {
                logger.error("Error processing forward request for URL: {}. Error: {}", 
                           forwardRequest.url(), throwable.getMessage());
                
                // Return appropriate error response based on the exception
                if (throwable.getCause() instanceof RuntimeException) {
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body(null);
                }
                
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
            })
            .orTimeout(300, TimeUnit.SECONDS) // 5 minute timeout
            .exceptionally(throwable -> {
                if (throwable.getCause() instanceof java.util.concurrent.TimeoutException) {
                    logger.error("Timeout occurred while processing forward request for URL: {}", forwardRequest.url());
                    return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body(null);
                }
                logger.error("Unexpected error in forward request processing for URL: {}. Error: {}", 
                           forwardRequest.url(), throwable.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
            });
    }
    
    /**
     * Health check endpoint for the forward service
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Forward service is running");
    }
}