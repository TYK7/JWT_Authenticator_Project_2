package com.example.jwtauthenticator.service;

import com.example.jwtauthenticator.config.AppConfig;
import com.example.jwtauthenticator.dto.GoogleSignInRequest;
import com.example.jwtauthenticator.dto.GoogleUserInfo;
import com.example.jwtauthenticator.entity.LoginLog;
import com.example.jwtauthenticator.entity.User;
import com.example.jwtauthenticator.entity.User.AuthProvider;
import com.example.jwtauthenticator.entity.User.Role;
import com.example.jwtauthenticator.model.AuthRequest;
import com.example.jwtauthenticator.model.AuthResponse;
import com.example.jwtauthenticator.model.RegisterRequest;
import com.example.jwtauthenticator.repository.LoginLogRepository;
import com.example.jwtauthenticator.repository.UserRepository;
import com.example.jwtauthenticator.security.JwtUserDetailsService;
import com.example.jwtauthenticator.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private GoogleTokenVerificationService googleTokenVerificationService;

    @Autowired
    private LoginLogRepository loginLogRepository;

    public String registerUser(RegisterRequest request) {
        if (userRepository.existsByUsernameAndTenantId(request.getUsername(), request.getTenantId())) {
            throw new RuntimeException("Username already exists for this tenant");
        }
        if (userRepository.existsByEmailAndTenantId(request.getEmail(), request.getTenantId())) {
            throw new RuntimeException("Email already exists for this tenant");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .location(request.getLocation())
                .role(Role.USER) // Default role
                .tenantId(request.getTenantId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .emailVerified(false)
                .build();

        String verificationToken = UUID.randomUUID().toString();
        newUser.setVerificationToken(verificationToken);

        userDetailsService.save(newUser);

        String verificationLink = appConfig.getApiUrl("/auth/verify-email?token=" + verificationToken);
        emailService.sendEmail(newUser.getEmail(), "Email Verification", "Please click the link to verify your email: " + verificationLink);

        return "User registered successfully. Please verify your email.";
    }

    public AuthResponse createAuthenticationToken(AuthRequest authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword(), authenticationRequest.getTenantId());
        final UserDetails userDetails = userDetailsService
                .loadUserByUsernameAndTenantId(authenticationRequest.getUsername(), authenticationRequest.getTenantId());
        User user = userRepository.findByUsernameAndTenantId(authenticationRequest.getUsername(), authenticationRequest.getTenantId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEmailVerified()) {
            throw new RuntimeException("Email not verified. Please verify your email to login.");
        }

        final String token = jwtUtil.generateToken(userDetails, user.getUserId().toString());
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails, user.getUserId().toString());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Log successful password login
        logLogin(user, "PASSWORD", "SUCCESS", "Password login successful");

        return new AuthResponse(token, refreshToken);
    }

    public AuthResponse loginUser(AuthRequest authenticationRequest) throws Exception {
        return createAuthenticationToken(authenticationRequest);
    }

    public AuthResponse refreshToken(String oldRefreshToken) throws Exception {
        String username = jwtUtil.extractUsername(oldRefreshToken);
        String userId = jwtUtil.extractUserId(oldRefreshToken);
        // For multi-tenancy, we need to extract tenantId from the refresh token or pass it separately.
        // For simplicity, assuming tenantId is part of the JWT claims or derived from userId.
        // In a real-world scenario, you might store tenantId in the refresh token claims.
        User user = userRepository.findByUserId(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getRefreshToken().equals(oldRefreshToken) || jwtUtil.isTokenExpired(oldRefreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsernameAndTenantId(username, user.getTenantId());
        final String newToken = jwtUtil.generateToken(userDetails, user.getUserId().toString());
        final String newRefreshToken = jwtUtil.generateRefreshToken(userDetails, user.getUserId().toString());

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return new AuthResponse(newToken, newRefreshToken);
    }

    public String verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        user.setEmailVerified(true);
        user.setVerificationToken(null); // Clear the token after verification
        userRepository.save(user);
        return "Email verified successfully!";
    }

    private void authenticate(String username, String password, String tenantId) throws Exception {
        try {
            // Manual authentication for multi-tenant setup
            User user = userRepository.findByUsernameAndTenantId(username, tenantId)
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }
            
            // Additional checks can be added here (e.g., account enabled, not locked, etc.)
            
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }

    public AuthResponse googleSignIn(GoogleSignInRequest request) throws Exception {
        try {
            // Verify Google ID token and extract user info
            GoogleUserInfo googleUserInfo = googleTokenVerificationService.verifyToken(request.getIdToken());
            
            // Check if user already exists
            Optional<User> existingUserOpt = userRepository.findByEmail(googleUserInfo.getEmail());
            User existingUser = existingUserOpt.orElse(null);
            
            if (existingUser != null) {
                // User exists - update profile picture and email verification if needed
                boolean needsUpdate = false;
                
                if (googleUserInfo.getPicture() != null && 
                    !googleUserInfo.getPicture().equals(existingUser.getProfilePictureUrl())) {
                    existingUser.setProfilePictureUrl(googleUserInfo.getPicture());
                    needsUpdate = true;
                }
                
                // Ensure email is verified for Google users
                if (!existingUser.isEmailVerified()) {
                    existingUser.setEmailVerified(true);
                    needsUpdate = true;
                }
                
                if (needsUpdate) {
                    userRepository.save(existingUser);
                }
                
                // Log successful Google login for existing user
                logLogin(existingUser, "GOOGLE", "SUCCESS", "Google Sign-In successful for existing user");
                
                // Generate JWT token for existing user
                return generateJwtResponse(existingUser);
            } else {
                // User doesn't exist - create new user
                User newUser = createGoogleUser(googleUserInfo);
                userRepository.save(newUser);
                
                // Log successful Google registration and login for new user
                logLogin(newUser, "GOOGLE", "SUCCESS", "Google Sign-In successful - new user created");
                
                // Generate JWT token for new user
                return generateJwtResponse(newUser);
            }
            
        } catch (Exception e) {
            throw new Exception("Google Sign-In failed: " + e.getMessage(), e);
        }
    }

    private User createGoogleUser(GoogleUserInfo googleUserInfo) {
        // Generate a unique username from email
        String baseUsername = googleUserInfo.getEmail().split("@")[0];
        String username = generateUniqueUsername(baseUsername);
        
        return User.builder()
                .username(username)
                .email(googleUserInfo.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString())) // Random password for Google users
                .role(Role.USER)
                .authProvider(AuthProvider.GOOGLE)
                .emailVerified(true) // Always true for Google users since Google has already verified the email
                .profilePictureUrl(googleUserInfo.getPicture())
                .tenantId("default") // You can modify this based on your tenant logic
                .build();
    }

    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;
        
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }

    private AuthResponse generateJwtResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsernameAndTenantId(user.getUsername(), user.getTenantId());
        String accessToken = jwtUtil.generateToken(userDetails, user.getUserId().toString());
        String refreshToken = jwtUtil.generateRefreshToken(userDetails, user.getUserId().toString());
        
        // Save refresh token
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        
        return new AuthResponse(accessToken, refreshToken);
    }

    private void logLogin(User user, String loginMethod, String loginStatus, String details) {
        try {
            LoginLog loginLog = LoginLog.builder()
                    .userId(user.getUserId())
                    .username(user.getUsername())
                    .loginMethod(loginMethod)
                    .loginStatus(loginStatus)
                    .details(details)
                    .build();
            
            loginLogRepository.save(loginLog);
            System.out.println("LOGIN_LOG: " + user.getUsername() + " - " + loginMethod + " - " + loginStatus);
        } catch (Exception e) {
            System.err.println("Failed to log login event: " + e.getMessage());
        }
    }
}


