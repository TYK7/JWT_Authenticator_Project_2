package com.example.jwtauthenticator.service;

import com.example.jwtauthenticator.entity.User;
import com.example.jwtauthenticator.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class TfaService {

    @Autowired
    private UserRepository userRepository;

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String generateNewSecret(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        user.setTfaSecret(key.getKey());
        userRepository.save(user);
        return key.getKey();
    }

    public boolean verifyCode(String username, int code) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        if (user.getTfaSecret() == null) {
            throw new RuntimeException("2FA not set up for this user");
        }

        return gAuth.authorize(user.getTfaSecret(), code);
    }

    public void enableTfa(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        user.setTfaEnabled(true);
        userRepository.save(user);
    }

    public void disableTfa(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        user.setTfaEnabled(false);
        user.setTfaSecret(null);
        userRepository.save(user);
    }

    public byte[] generateQRCode(String username) throws WriterException, IOException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        
        if (user.getTfaSecret() == null) {
            throw new RuntimeException("2FA secret not generated. Please generate secret first.");
        }

        String issuer = "JWT Authenticator";
        String accountName = user.getEmail() != null ? user.getEmail() : username;
        
        String qrCodeData;
        try {
            qrCodeData = String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                URLEncoder.encode(issuer, "UTF-8"),
                URLEncoder.encode(accountName, "UTF-8"),
                user.getTfaSecret(),
                URLEncoder.encode(issuer, "UTF-8")
            );
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported", e);
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeData, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    public int getCurrentTotpCode(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (!userOptional.isPresent()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();
        
        if (user.getTfaSecret() == null) {
            throw new RuntimeException("2FA secret not generated. Please generate secret first.");
        }

        return gAuth.getTotpPassword(user.getTfaSecret());
    }
}
