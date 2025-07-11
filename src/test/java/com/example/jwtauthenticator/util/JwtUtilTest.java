package com.example.jwtauthenticator.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @InjectMocks
    private JwtUtil jwtUtil;

    private String secret = "thisisalongsecretkeyforjwttestingthatmeetstherequirementsofhs256";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtil, "secretString", secret);
    }

    @Test
    void generateToken_shouldReturnValidToken() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String userId = "123e4567-e89b-12d3-a456-426614174000";

        String token = jwtUtil.generateToken(userDetails, userId);

        assertNotNull(token);
        assertEquals("testuser", jwtUtil.extractUsername(token));
        assertEquals(userId, jwtUtil.extractUserId(token));
        assertFalse(jwtUtil.isTokenExpired(token));
    }

    @Test
    void generateRefreshToken_shouldReturnValidToken() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String userId = "123e4567-e89b-12d3-a456-426614174000";

        String refreshToken = jwtUtil.generateRefreshToken(userDetails, userId);

        assertNotNull(refreshToken);
        assertEquals("testuser", jwtUtil.extractUsername(refreshToken));
        assertEquals(userId, jwtUtil.extractUserId(refreshToken));
        assertFalse(jwtUtil.isTokenExpired(refreshToken));
    }

    @Test
    void validateToken_shouldReturnTrueForValidToken() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String token = jwtUtil.generateToken(userDetails, userId);

        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalseForExpiredToken() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String userId = "123e4567-e89b-12d3-a456-426614174000";

        // Create an expired token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        String expiredToken = Jwts.builder().setClaims(claims).setSubject("testuser").setIssuedAt(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24))
                .setExpiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 12))
                .signWith(SignatureAlgorithm.HS256, secret).compact();

        assertFalse(jwtUtil.validateToken(expiredToken, userDetails));
    }

    @Test
    void validateToken_shouldReturnFalseForInvalidUser() {
        UserDetails userDetails = new User("testuser", "password", Collections.emptyList());
        String userId = "123e4567-e89b-12d3-a456-426614174000";
        String token = jwtUtil.generateToken(userDetails, userId);

        UserDetails invalidUserDetails = new User("anotheruser", "password", Collections.emptyList());

        assertFalse(jwtUtil.validateToken(token, invalidUserDetails));
    }
}
