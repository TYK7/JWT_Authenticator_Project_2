package com.example.jwtauthenticator.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Autowired
    private AppConfig appConfig;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("JWT Authenticator API")
                        .version("1.0.0")
                        .description("A comprehensive JWT-based authentication system with multi-tenant support, " +
                                "email verification, two-factor authentication, and audit logging.")
                        .contact(new Contact()
                                .name("JWT Authenticator Team")
                                .email("support@jwtauthenticator.com")
                                .url("https://github.com/example/jwt-authenticator"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(Arrays.asList(
                        new Server()
                                .url(appConfig.getApiBaseUrl())
                                .description(appConfig.isLocalDevelopment() ? "Development Server" : "Production Server"),
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Fallback")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer")
                .description("Enter JWT Bearer token in the format: Bearer {token}");
    }
}