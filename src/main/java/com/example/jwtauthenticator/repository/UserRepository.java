package com.example.jwtauthenticator.repository;

import com.example.jwtauthenticator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndBrandId(String username, String brandId);
    Boolean existsByUsername(String username);
    Boolean existsByUsernameAndBrandId(String username, String brandId);
    Boolean existsByEmail(String email);
    Boolean existsByEmailAndBrandId(String email, String brandId);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndBrandId(String email, String brandId);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByUserId(UUID userId);
}
