package com.example.jwtauthenticator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class JwtAuthenticatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthenticatorApplication.class, args);
    }

}
