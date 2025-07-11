package com.example.jwtauthenticator.example;

import com.example.jwtauthenticator.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Example demonstrating the flexible ID generator usage
 * This will run on application startup to show how the ID generator works
 */
@Component
public class IdGeneratorExample implements CommandLineRunner {

    @Autowired
    private IdGeneratorService idGeneratorService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== ID Generator Example ===");
        
        // Generate IDs with default prefix (MRTFY from application.properties)
        System.out.println("1. Generating IDs with default prefix:");
        for (int i = 0; i < 3; i++) {
            String id = idGeneratorService.generateNextId();
            System.out.println("Generated: " + id);
        }
        
        // Generate IDs with custom prefix
        System.out.println("\n2. Generating IDs with custom prefix (MKTY):");
        for (int i = 0; i < 3; i++) {
            String id = idGeneratorService.generateNextId("MKTY");
            System.out.println("Generated: " + id);
        }
        
        // Generate IDs with another custom prefix
        System.out.println("\n3. Generating IDs with another prefix (TEST):");
        for (int i = 0; i < 2; i++) {
            String id = idGeneratorService.generateNextId("TEST");
            System.out.println("Generated: " + id);
        }
        
        // Show current numbers
        System.out.println("\n4. Current numbers for each prefix:");
        System.out.println("MRTFY current number: " + idGeneratorService.getCurrentNumber("MRTFY"));
        System.out.println("MKTY current number: " + idGeneratorService.getCurrentNumber("MKTY"));
        System.out.println("TEST current number: " + idGeneratorService.getCurrentNumber("TEST"));
        
        // Preview next IDs
        System.out.println("\n5. Preview next IDs (without generating):");
        System.out.println("Next MRTFY ID would be: " + idGeneratorService.previewNextId("MRTFY"));
        System.out.println("Next MKTY ID would be: " + idGeneratorService.previewNextId("MKTY"));
        System.out.println("Next TEST ID would be: " + idGeneratorService.previewNextId("TEST"));
        
        // Show all prefixes
        System.out.println("\n6. All available prefixes: " + idGeneratorService.getAllPrefixes());
        
        System.out.println("\n=== ID Generator Example Complete ===\n");
    }
}