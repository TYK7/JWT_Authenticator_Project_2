package com.example.jwtauthenticator.service;

import com.example.jwtauthenticator.entity.IdSequence;
import com.example.jwtauthenticator.repository.IdSequenceRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class IdGeneratorService {

    @Autowired
    private IdSequenceRepository idSequenceRepository;

    @Value("${brand.id.prefix:MRTFY}")
    private String defaultPrefix;

    @Value("${brand.id.number.padding:4}")
    private int numberPadding;

    /**
     * Generate next ID with default prefix from application.properties
     * @return Generated ID (e.g., MRTFY0001)
     */
    @Transactional
    public String generateNextId() {
        return generateNextId(defaultPrefix);
    }

    /**
     * Generate next ID with custom prefix
     * @param prefix Custom prefix (e.g., MKTY, MRTFY)
     * @return Generated ID (e.g., MKTY0001)
     */
    @Transactional
    public String generateNextId(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = defaultPrefix;
        }
        
        prefix = prefix.trim().toUpperCase();
        
        // Validate prefix length
        if (prefix.length() > 10) {
            throw new IllegalArgumentException("Prefix cannot be longer than 10 characters");
        }

        try {
            // Get or create sequence with pessimistic lock for thread safety
            IdSequence sequence = getOrCreateSequenceWithLock(prefix);
            
            // Increment the number
            sequence.setCurrentNumber(sequence.getCurrentNumber() + 1);
            
            // Save the updated sequence
            idSequenceRepository.save(sequence);
            
            // Format the ID
            String formattedId = formatId(prefix, sequence.getCurrentNumber());
            
            log.info("Generated ID: {} for prefix: {}", formattedId, prefix);
            
            return formattedId;
            
        } catch (Exception e) {
            log.error("Error generating ID for prefix: {}", prefix, e);
            throw new RuntimeException("Failed to generate ID for prefix: " + prefix, e);
        }
    }

    /**
     * Get current number for a prefix without incrementing
     * @param prefix The prefix to check
     * @return Current number (0 if prefix doesn't exist)
     */
    public Long getCurrentNumber(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = defaultPrefix;
        }
        
        prefix = prefix.trim().toUpperCase();
        
        Optional<IdSequence> sequence = idSequenceRepository.findByPrefix(prefix);
        return sequence.map(IdSequence::getCurrentNumber).orElse(0L);
    }

    /**
     * Preview next ID without generating it
     * @param prefix The prefix to preview
     * @return What the next ID would be
     */
    public String previewNextId(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            prefix = defaultPrefix;
        }
        
        prefix = prefix.trim().toUpperCase();
        
        Long currentNumber = getCurrentNumber(prefix);
        return formatId(prefix, currentNumber + 1);
    }

    /**
     * Reset sequence for a prefix (use with caution!)
     * @param prefix The prefix to reset
     * @param startNumber The number to start from (default: 0)
     */
    @Transactional
    public void resetSequence(String prefix, Long startNumber) {
        if (prefix == null || prefix.trim().isEmpty()) {
            throw new IllegalArgumentException("Prefix cannot be null or empty");
        }
        
        prefix = prefix.trim().toUpperCase();
        startNumber = startNumber != null ? startNumber : 0L;
        
        Optional<IdSequence> existingSequence = idSequenceRepository.findByPrefix(prefix);
        
        if (existingSequence.isPresent()) {
            IdSequence sequence = existingSequence.get();
            sequence.setCurrentNumber(startNumber);
            idSequenceRepository.save(sequence);
            log.warn("Reset sequence for prefix: {} to start from: {}", prefix, startNumber);
        } else {
            // Create new sequence
            IdSequence newSequence = IdSequence.builder()
                    .prefix(prefix)
                    .currentNumber(startNumber)
                    .build();
            idSequenceRepository.save(newSequence);
            log.info("Created new sequence for prefix: {} starting from: {}", prefix, startNumber);
        }
    }

    /**
     * Get or create sequence with pessimistic lock for thread safety
     */
    private IdSequence getOrCreateSequenceWithLock(String prefix) {
        // Try to get existing sequence with lock
        Optional<IdSequence> existingSequence = idSequenceRepository.findByPrefixWithLock(prefix);
        
        if (existingSequence.isPresent()) {
            return existingSequence.get();
        }
        
        // Create new sequence if it doesn't exist
        IdSequence newSequence = IdSequence.builder()
                .prefix(prefix)
                .currentNumber(0L)
                .build();
        
        return idSequenceRepository.save(newSequence);
    }

    /**
     * Format ID with prefix and padded number
     */
    private String formatId(String prefix, Long number) {
        String paddedNumber = String.format("%0" + numberPadding + "d", number);
        return prefix + paddedNumber;
    }

    /**
     * Get all available prefixes
     */
    public java.util.List<String> getAllPrefixes() {
        return idSequenceRepository.findAll()
                .stream()
                .map(IdSequence::getPrefix)
                .sorted()
                .toList();
    }

    /**
     * Check if a prefix exists
     */
    public boolean prefixExists(String prefix) {
        if (prefix == null || prefix.trim().isEmpty()) {
            return false;
        }
        return idSequenceRepository.existsByPrefix(prefix.trim().toUpperCase());
    }
}