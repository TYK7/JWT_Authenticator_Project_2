package com.example.jwtauthenticator.audit;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    public void logEvent(String username, String eventType, String details) {
        System.out.println(String.format("AUDIT | %s | User: %s | Event: %s | Details: %s", LocalDateTime.now(), username, eventType, details));
        // In a real application, this would write to a database, log file, or message queue
    }
}
