package com.example.jwtauthenticator.service;

import com.example.jwtauthenticator.entity.IdSequence;
import com.example.jwtauthenticator.repository.IdSequenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdGeneratorServiceTest {

    @Mock
    private IdSequenceRepository idSequenceRepository;

    @InjectMocks
    private IdGeneratorService idGeneratorService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(idGeneratorService, "defaultPrefix", "MRTFY");
        ReflectionTestUtils.setField(idGeneratorService, "numberPadding", 4);
    }

    @Test
    void generateNextId_withDefaultPrefix_shouldReturnFormattedId() {
        // Arrange
        IdSequence sequence = IdSequence.builder()
                .prefix("MRTFY")
                .currentNumber(0L)
                .build();
        
        when(idSequenceRepository.findByPrefixWithLock("MRTFY"))
                .thenReturn(Optional.of(sequence));
        when(idSequenceRepository.save(any(IdSequence.class)))
                .thenAnswer(invocation -> {
                    IdSequence saved = invocation.getArgument(0);
                    saved.setCurrentNumber(1L);
                    return saved;
                });

        // Act
        String result = idGeneratorService.generateNextId();

        // Assert
        assertEquals("MRTFY0001", result);
        verify(idSequenceRepository).save(any(IdSequence.class));
    }

    @Test
    void generateNextId_withCustomPrefix_shouldReturnFormattedId() {
        // Arrange
        IdSequence sequence = IdSequence.builder()
                .prefix("MKTY")
                .currentNumber(5L)
                .build();
        
        when(idSequenceRepository.findByPrefixWithLock("MKTY"))
                .thenReturn(Optional.of(sequence));
        when(idSequenceRepository.save(any(IdSequence.class)))
                .thenAnswer(invocation -> {
                    IdSequence saved = invocation.getArgument(0);
                    saved.setCurrentNumber(6L);
                    return saved;
                });

        // Act
        String result = idGeneratorService.generateNextId("MKTY");

        // Assert
        assertEquals("MKTY0006", result);
        verify(idSequenceRepository).save(any(IdSequence.class));
    }

    @Test
    void generateNextId_newPrefix_shouldCreateNewSequence() {
        // Arrange
        when(idSequenceRepository.findByPrefixWithLock("NEWPFX"))
                .thenReturn(Optional.empty());
        when(idSequenceRepository.save(any(IdSequence.class)))
                .thenAnswer(invocation -> {
                    IdSequence saved = invocation.getArgument(0);
                    if (saved.getCurrentNumber() == 0L) {
                        // First save - creating new sequence
                        return saved;
                    } else {
                        // Second save - incrementing
                        saved.setCurrentNumber(1L);
                        return saved;
                    }
                });

        // Act
        String result = idGeneratorService.generateNextId("NEWPFX");

        // Assert
        assertEquals("NEWPFX0001", result);
        verify(idSequenceRepository, times(2)).save(any(IdSequence.class));
    }

    @Test
    void previewNextId_shouldReturnNextIdWithoutIncrementing() {
        // Arrange
        IdSequence sequence = IdSequence.builder()
                .prefix("MRTFY")
                .currentNumber(10L)
                .build();
        
        when(idSequenceRepository.findByPrefix("MRTFY"))
                .thenReturn(Optional.of(sequence));

        // Act
        String result = idGeneratorService.previewNextId("MRTFY");

        // Assert
        assertEquals("MRTFY0011", result);
        verify(idSequenceRepository, never()).save(any(IdSequence.class));
    }

    @Test
    void getCurrentNumber_existingPrefix_shouldReturnCurrentNumber() {
        // Arrange
        IdSequence sequence = IdSequence.builder()
                .prefix("MRTFY")
                .currentNumber(25L)
                .build();
        
        when(idSequenceRepository.findByPrefix("MRTFY"))
                .thenReturn(Optional.of(sequence));

        // Act
        Long result = idGeneratorService.getCurrentNumber("MRTFY");

        // Assert
        assertEquals(25L, result);
    }

    @Test
    void getCurrentNumber_nonExistingPrefix_shouldReturnZero() {
        // Arrange
        when(idSequenceRepository.findByPrefix("NONEXIST"))
                .thenReturn(Optional.empty());

        // Act
        Long result = idGeneratorService.getCurrentNumber("NONEXIST");

        // Assert
        assertEquals(0L, result);
    }

    @Test
    void resetSequence_existingPrefix_shouldUpdateSequence() {
        // Arrange
        IdSequence sequence = IdSequence.builder()
                .prefix("MRTFY")
                .currentNumber(100L)
                .build();
        
        when(idSequenceRepository.findByPrefix("MRTFY"))
                .thenReturn(Optional.of(sequence));

        // Act
        idGeneratorService.resetSequence("MRTFY", 50L);

        // Assert
        assertEquals(50L, sequence.getCurrentNumber());
        verify(idSequenceRepository).save(sequence);
    }

    @Test
    void generateNextId_invalidPrefix_shouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            idGeneratorService.generateNextId("VERYLONGPREFIXTHATEXCEEDSLIMIT");
        });
    }
}