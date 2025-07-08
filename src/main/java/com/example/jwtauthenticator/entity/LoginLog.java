package com.example.jwtauthenticator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "login_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "username", nullable = false, length = 50)
    private String username;

    @Column(name = "login_method", nullable = false, length = 20)
    private String loginMethod; // "PASSWORD", "GOOGLE"

    @Column(name = "login_status", nullable = false, length = 20)
    private String loginStatus; // "SUCCESS", "FAILURE"

    @Column(name = "login_time", nullable = false)
    @Builder.Default
    private LocalDateTime loginTime = LocalDateTime.now();

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;
}