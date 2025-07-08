package com.example.jwtauthenticator.config;

import com.example.jwtauthenticator.service.EmailService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public EmailService emailService() {
        return Mockito.mock(EmailService.class);
    }
}