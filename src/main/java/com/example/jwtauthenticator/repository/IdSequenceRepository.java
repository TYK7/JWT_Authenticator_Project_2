package com.example.jwtauthenticator.repository;

import com.example.jwtauthenticator.entity.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface IdSequenceRepository extends JpaRepository<IdSequence, Long> {
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM IdSequence s WHERE s.prefix = :prefix")
    Optional<IdSequence> findByPrefixWithLock(@Param("prefix") String prefix);
    
    Optional<IdSequence> findByPrefix(String prefix);
    
    boolean existsByPrefix(String prefix);
}