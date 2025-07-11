package com.example.jwtauthenticator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for company details from external API
 */
public record CompanyDetailsResponse(
    @JsonProperty("Logo")
    LogoInfo logo,
    
    @JsonProperty("Colors")
    List<ColorInfo> colors,
    
    @JsonProperty("Fonts")
    List<FontInfo> fonts,
    
    @JsonProperty("Images")
    List<ImageInfo> images,
    
    @JsonProperty("Company")
    CompanyInfo company,
    
    @JsonProperty("_performance")
    PerformanceInfo performance,
    
    @JsonProperty("_message")
    String message
) {
    
    public record LogoInfo(
        @JsonProperty("Logo")
        String logo,
        
        @JsonProperty("Symbol")
        String symbol,
        
        @JsonProperty("Icon")
        String icon,
        
        @JsonProperty("Banner")
        String banner,
        
        @JsonProperty("LinkedInBanner")
        String linkedInBanner
    ) {}
    
    public record ColorInfo(
        String hex,
        String rgb,
        Integer brightness,
        String name
    ) {}
    
    public record FontInfo(
        String name,
        String type,
        String stack
    ) {}
    
    public record ImageInfo(
        String src,
        String alt
    ) {}
    
    public record CompanyInfo(
        @JsonProperty("Name")
        String name,
        
        @JsonProperty("Description")
        String description,
        
        @JsonProperty("Industry")
        String industry,
        
        @JsonProperty("Location")
        String location,
        
        @JsonProperty("Founded")
        String founded,
        
        @JsonProperty("CompanyType")
        String companyType,
        
        @JsonProperty("Employees")
        String employees,
        
        @JsonProperty("Website")
        String website,
        
        @JsonProperty("SocialLinks")
        Map<String, String> socialLinks,
        
        @JsonProperty("CompanySize")
        String companySize,
        
        @JsonProperty("Headquarters")
        String headquarters,
        
        @JsonProperty("Type")
        String type,
        
        @JsonProperty("Specialties")
        String specialties
    ) {}
    
    public record PerformanceInfo(
        Double extractionTimeSeconds,
        String timestamp
    ) {}
}