package com.example.jwtauthenticator.repository;

import com.example.jwtauthenticator.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCode, Long> {
    
    Optional<PasswordResetCode> findByEmailAndBrandIdAndCodeAndUsedFalse(String email, String brandId, String code);
    
    @Modifying
    @Query("DELETE FROM PasswordResetCode p WHERE p.email = :email AND p.brandId = :brandId")
    void deleteByEmailAndBrandId(@Param("email") String email, @Param("brandId") String brandId);
    
    @Modifying
    @Query("DELETE FROM PasswordResetCode p WHERE p.expiresAt < :now")
    void deleteExpiredCodes(@Param("now") LocalDateTime now);
}