package com.example.jwtauthenticator.repository;

import com.example.jwtauthenticator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameAndTenantId(String username, String tenantId);
    Boolean existsByUsername(String username);
    Boolean existsByUsernameAndTenantId(String username, String tenantId);
    Boolean existsByEmail(String email);
    Boolean existsByEmailAndTenantId(String email, String tenantId);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndTenantId(String email, String tenantId);
    Optional<User> findByVerificationToken(String verificationToken);
    Optional<User> findByUserId(UUID userId);
}
