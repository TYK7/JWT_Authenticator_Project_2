package com.example.jwtauthenticator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.Getter;

@Configuration
@Getter
public class AppConfig {
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${server.address:0.0.0.0}")
    private String serverAddress;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    /**
     * Get the complete base URL for API endpoints
     * @return Base URL (e.g., http://localhost:8080)
     */
    public String getApiBaseUrl() {
        return baseUrl;
    }
    
    /**
     * Get API endpoint URL with path
     * @param path API path (e.g., "/auth/login")
     * @return Complete URL (e.g., http://localhost:8080/auth/login)
     */
    public String getApiUrl(String path) {
        return baseUrl + (path.startsWith("/") ? path : "/" + path);
    }
    
    /**
     * Get frontend URL for CORS and redirects
     * @return Frontend URL (e.g., http://localhost:3000)
     */
    public String getFrontendBaseUrl() {
        return frontendUrl;
    }
    
    /**
     * Get frontend URL with path
     * @param path Frontend path (e.g., "/dashboard")
     * @return Complete frontend URL
     */
    public String getFrontendUrl(String path) {
        return frontendUrl + (path.startsWith("/") ? path : "/" + path);
    }
    
    /**
     * Check if running in local development mode
     * @return true if running locally
     */
    public boolean isLocalDevelopment() {
        return baseUrl.contains("localhost") || baseUrl.contains("127.0.0.1");
    }
    
    /**
     * Get server info for logging and monitoring
     * @return Server info string
     */
    public String getServerInfo() {
        return String.format("Server running on %s:%s, Base URL: %s", 
                           serverAddress, serverPort, baseUrl);
    }
}
