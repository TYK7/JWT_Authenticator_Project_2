package com.example.jwtauthenticator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username:noreply@yourdomain.com}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
    
    public void sendVerificationEmail(String to, String username, String verificationToken, String baseUrl) {
        String subject = "Email Verification - JWT Authenticator";
        String verificationUrl = baseUrl + "/auth/verify-email?token=" + verificationToken;
        
        String emailBody = """
            Hello %s,
            
            Welcome to JWT Authenticator! Please verify your email address by clicking the link below:
            
            %s
            
            This link will expire in 24 hours for security reasons.
            
            If you didn't create this account, please ignore this email.
            
            Best regards,
            JWT Authenticator Team
            """.formatted(username, verificationUrl);
            
        sendEmail(to, subject, emailBody);
    }
    
    public void sendPasswordResetEmail(String to, String username, String resetToken, String baseUrl) {
        String subject = "Password Reset - JWT Authenticator";
        String resetUrl = baseUrl + "/auth/reset-password?token=" + resetToken;
        
        String emailBody = """
            Hello %s,
            
            You have requested to reset your password. Click the link below to reset your password:
            
            %s
            
            This link will expire in 1 hour for security reasons.
            
            If you didn't request this password reset, please ignore this email.
            
            Best regards,
            JWT Authenticator Team
            """.formatted(username, resetUrl);
            
        sendEmail(to, subject, emailBody);
    }
}
