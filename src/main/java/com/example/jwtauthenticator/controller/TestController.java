package com.example.jwtauthenticator.controller;

import com.example.jwtauthenticator.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private AppConfig appConfig;

    @Value("${google.oauth2.client-id:}")
    private String googleClientId;

    @GetMapping("/google-signin-demo")
    public ResponseEntity<String> getGoogleSignInDemo() {
        String html = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
                "<title>Google Sign-In Test</title>" +
                "<script src=\"https://accounts.google.com/gsi/client\" async defer></script>" +
                "<style>" +
                    "body { font-family: Arial, sans-serif; margin: 50px; }" +
                    ".container { max-width: 600px; margin: 0 auto; }" +
                    ".result { margin-top: 20px; padding: 10px; border: 1px solid #ccc; }" +
                    ".success { background-color: #d4edda; border-color: #c3e6cb; }" +
                    ".error { background-color: #f8d7da; border-color: #f5c6cb; }" +
                "</style>" +
            "</head>" +
            "<body>" +
                "<div class=\"container\">" +
                    "<h1>Google Sign-In Test</h1>" +
                    "<p><strong>API Base URL:</strong> " + appConfig.getApiBaseUrl() + "</p>" +
                    "<p><strong>Google Client ID:</strong> " + (googleClientId.isEmpty() ? "NOT CONFIGURED" : googleClientId.substring(0, 20) + "...") + "</p>" +
                    "<div id=\"g_id_onload\" data-client_id=\"" + googleClientId + "\" data-callback=\"handleCredentialResponse\"></div>" +
                    "<div class=\"g_id_signin\" data-type=\"standard\"></div>" +
                    "<div id=\"result\" class=\"result\" style=\"display:none;\"></div>" +
                "</div>" +
                "<script>" +
                    "const API_BASE_URL = '" + appConfig.getApiBaseUrl() + "';" +
                    "function handleCredentialResponse(response) {" +
                        "console.log('Google ID Token:', response.credential);" +
                        "console.log('Using API Base URL:', API_BASE_URL);" +
                        "fetch(API_BASE_URL + '/auth/google', {" +
                            "method: 'POST'," +
                            "headers: { 'Content-Type': 'application/json' }," +
                            "body: JSON.stringify({ idToken: response.credential })" +
                        "})" +
                        ".then(response => {" +
                            "if (!response.ok) {" +
                                "return response.json().then(err => Promise.reject(err));" +
                            "}" +
                            "return response.json();" +
                        "})" +
                        ".then(data => {" +
                            "const resultDiv = document.getElementById('result');" +
                            "resultDiv.style.display = 'block';" +
                            "if (data.token) {" +
                                "resultDiv.className = 'result success';" +
                                "resultDiv.innerHTML = '<h3>✅ Success!</h3><p>Access Token: ' + data.token.substring(0, 50) + '...</p>';" +
                            "} else {" +
                                "resultDiv.className = 'result error';" +
                                "resultDiv.innerHTML = '<h3>❌ Failed</h3><p>Error: ' + JSON.stringify(data) + '</p>';" +
                            "}" +
                        "})" +
                        ".catch(error => {" +
                            "const resultDiv = document.getElementById('result');" +
                            "resultDiv.style.display = 'block';" +
                            "resultDiv.className = 'result error';" +
                            "if (error.error) {" +
                                "resultDiv.innerHTML = '<h3>❌ Authentication Error</h3><p>' + error.error + '</p>';" +
                            "} else {" +
                                "resultDiv.innerHTML = '<h3>❌ Network Error</h3><p>' + (error.message || JSON.stringify(error)) + '</p>';" +
                            "}" +
                        "});" +
                    "}" +
                "</script>" +
            "</body>" +
            "</html>";
        
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(html);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("service", "JWT Authenticator");
        status.put("baseUrl", appConfig.getApiBaseUrl());
        status.put("environment", appConfig.isLocalDevelopment() ? "development" : "production");
        status.put("serverInfo", appConfig.getServerInfo());
        status.put("googleSignIn", "Available");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("googleSignIn", appConfig.getApiUrl("/auth/google"));
        endpoints.put("testPage", appConfig.getApiUrl("/test/google-signin-demo"));
        endpoints.put("swagger", appConfig.getApiUrl("/swagger-ui.html"));
        endpoints.put("health", appConfig.getApiUrl("/actuator/health"));
        
        status.put("endpoints", endpoints);
        return ResponseEntity.ok(status);
    }
}