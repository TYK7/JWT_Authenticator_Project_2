package com.example.jwtauthenticator.controller;

import com.example.jwtauthenticator.model.AuthRequest;
import com.example.jwtauthenticator.model.AuthResponse;
import com.example.jwtauthenticator.model.RegisterRequest;
import com.example.jwtauthenticator.service.AuthService;
import com.example.jwtauthenticator.service.PasswordResetService;
import com.example.jwtauthenticator.service.TfaService;
import com.example.jwtauthenticator.dto.*;
import java.util.Map;
import com.example.jwtauthenticator.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private TfaService tfaService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Register a new user", 
               description = "Register a new user account with email verification")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Username or email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {
            RegisterResponse response = authService.registerUser(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @Operation(summary = "Generate authentication token", 
               description = "Generate JWT access and refresh tokens for authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials or email not verified")
    })
    @PostMapping("/token")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthRequest authenticationRequest) throws Exception {
        AuthResponse authResponse = authService.createAuthenticationToken(authenticationRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "User login", 
               description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody AuthRequest authenticationRequest) throws Exception {
        AuthResponse authResponse = authService.loginUser(authenticationRequest);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Refresh JWT token", 
               description = "Generate new access and refresh tokens using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody String refreshToken) throws Exception {
        AuthResponse authResponse = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(summary = "Google Sign-In", 
               description = "Authenticate user using Google ID token and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Google Sign-In successful", 
                        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Google ID token"),
            @ApiResponse(responseCode = "500", description = "Google Sign-In service error")
    })
    @PostMapping("/google")
    public ResponseEntity<?> googleSignIn(@Valid @RequestBody GoogleSignInRequest request) {
        try {
            AuthResponse authResponse = authService.googleSignIn(request);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Google Sign-In failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PasswordResetRequest request) {
        passwordResetService.createPasswordResetToken(request.email());
        return ResponseEntity.ok("Password reset link sent to your email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordConfirmRequest request) {
        passwordResetService.resetPassword(request.token(), request.newPassword());
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @PostMapping("/tfa/setup")
    public ResponseEntity<?> setupTfa(@RequestParam String username) {
        String secret = tfaService.generateNewSecret(username);
        return ResponseEntity.ok("New 2FA secret generated: " + secret);
    }

    @PostMapping("/tfa/verify")
    public ResponseEntity<?> verifyTfa(@Valid @RequestBody TfaRequest request) {
        if (tfaService.verifyCode(request.username(), Integer.parseInt(request.code()))) {
            return ResponseEntity.ok("2FA code verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code.");
        }
    }

    @PostMapping("/tfa/enable")
    public ResponseEntity<?> enableTfa(@RequestParam String username) {
        tfaService.enableTfa(username);
        return ResponseEntity.ok("2FA enabled for user: " + username);
    }

    @PostMapping("/tfa/disable")
    public ResponseEntity<?> disableTfa(@RequestParam String username) {
        tfaService.disableTfa(username);
        return ResponseEntity.ok("2FA disabled for user: " + username);
    }

    @GetMapping("/tfa/qr-code")
    public ResponseEntity<byte[]> getTfaQrCode(@RequestParam String username) {
        try {
            byte[] qrCode = tfaService.generateQRCode(username);
            return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .body(qrCode);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/tfa/current-code")
    public ResponseEntity<?> getCurrentTotpCode(@RequestParam String username) {
        try {
            int currentCode = tfaService.getCurrentTotpCode(username);
            Map<String, String> response = new HashMap<>();
            response.put("username", username);
            response.put("currentCode", String.format("%06d", currentCode));
            response.put("note", "This code changes every 30 seconds");
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Operation(summary = "Verify email address", 
               description = "Verify user's email address using the verification token sent via email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid verification token")
    })
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Parameter(description = "Email verification token") @RequestParam String token) {
        String response = authService.verifyEmail(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forward")
    public ResponseEntity<?> forwardRequest(@Valid @RequestBody AuthRequest authenticationRequest, @RequestHeader(value = "X-Forward-URL") String forwardUrl) throws Exception {
        // Authenticate user and get JWT token
        AuthResponse authResponse = authService.loginUser(authenticationRequest);
        String token = authResponse.token();

        // Option 1: Using RestTemplate (default)
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("userId", authenticationRequest.username()); // Pass userId in header
        org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>("parameters", headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(forwardUrl, org.springframework.http.HttpMethod.GET, entity, String.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error forwarding request: " + e.getMessage());
        }

        /*
        // Option 2: Using WebClient (commented out for reference)
        WebClient webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .defaultHeader("userId", authenticationRequest.username())
                .build();

        Mono<String> responseMono = webClient.get()
                .uri(forwardUrl)
                .retrieve()
                .bodyToMono(String.class);

        return ResponseEntity.ok(responseMono.block());
        */
    }

    // Profile Update Endpoint
    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update user profile information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest request,
            HttpServletRequest httpRequest) {
        try {
            // Extract user info from JWT token
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header missing");
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.extractUsername(token);
            String brandId = httpRequest.getHeader("X-Brand-Id");

            if (brandId == null) {
                return ResponseEntity.badRequest().body("Brand ID header missing");
            }

            String result = authService.updateProfile(username, brandId, request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Check Email Existence Endpoint
    @PostMapping("/check-email")
    @Operation(summary = "Check if email exists", description = "Check if an email address is already registered")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email check completed"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public ResponseEntity<?> checkEmail(@Valid @RequestBody CheckEmailRequest request) {
        try {
            boolean exists = authService.checkEmailExists(request);
            return ResponseEntity.ok(Map.of(
                "exists", exists,
                "message", exists ? "Email address is already registered" : "Email address is available"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Enhanced Forgot Password - Send Verification Code
    @PostMapping("/forgot-password-code")
    @Operation(summary = "Send password reset verification code", description = "Send a verification code to the user's email for password reset")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification code sent successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Email not found")
    })
    public ResponseEntity<?> sendPasswordResetCode(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            String result = authService.sendPasswordResetCode(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Verify Reset Code - Step 2: Only verify the code
    @PostMapping("/verify-reset-code")
    @Operation(summary = "Verify password reset code", 
               description = "Verify the password reset verification code. This step only validates the code - password reset happens in the next step.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Code verified successfully - proceed to password reset"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired code")
    })
    public ResponseEntity<?> verifyResetCode(@Valid @RequestBody VerifyCodeRequest request) {
        try {
            String result = authService.verifyResetCode(request);
            return ResponseEntity.ok(Map.of(
                "message", result,
                "verified", true,
                "nextStep", "You can now call /auth/set-new-password with the same code to reset your password"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "verified", false
            ));
        }
    }

    // Set New Password - Step 3: Reset password after verification
    @PostMapping("/set-new-password")
    @Operation(summary = "Set new password after verification", 
               description = "Set a new password using the verified code. This can only be done after successfully verifying the code.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid, expired, or already used code")
    })
    public ResponseEntity<?> setNewPassword(@Valid @RequestBody SetNewPasswordRequest request) {
        try {
            String result = authService.setNewPassword(request);
            return ResponseEntity.ok(Map.of(
                "message", result,
                "success", true
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "success", false
            ));
        }
    }
}