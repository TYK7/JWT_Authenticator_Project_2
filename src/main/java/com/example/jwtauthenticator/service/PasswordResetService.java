package com.example.jwtauthenticator.service;

import com.example.jwtauthenticator.config.AppConfig;
import com.example.jwtauthenticator.entity.PasswordResetToken;
import com.example.jwtauthenticator.entity.User;
import com.example.jwtauthenticator.repository.PasswordResetTokenRepository;
import com.example.jwtauthenticator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppConfig appConfig;

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);
        myToken.setExpiryDate(LocalDateTime.now().plusMinutes(30)); // Token valid for 30 minutes
        passwordResetTokenRepository.save(myToken);

        // Send email
        String resetLink = appConfig.getApiUrl("/auth/reset-password?token=" + token);
        emailService.sendEmail(user.getEmail(), "Password Reset Request", "To reset your password, click the link: " + resetLink);
    }

    public void createPasswordResetToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with that email not found"));
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(user, token);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid or expired password reset token"));

        if (resetToken.isExpired()) {
            throw new RuntimeException("Password reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }
}
