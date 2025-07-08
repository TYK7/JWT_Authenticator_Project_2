package com.example.jwtauthenticator.repository;

import com.example.jwtauthenticator.entity.LoginLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoginLogRepository extends JpaRepository<LoginLog, Long> {
    
    List<LoginLog> findByUserIdOrderByLoginTimeDesc(UUID userId);
    
    List<LoginLog> findByUsernameOrderByLoginTimeDesc(String username);
    
    List<LoginLog> findByLoginStatusOrderByLoginTimeDesc(String loginStatus);
}