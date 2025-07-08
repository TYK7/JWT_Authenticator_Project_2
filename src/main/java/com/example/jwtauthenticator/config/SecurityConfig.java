package com.example.jwtauthenticator.config;

import com.example.jwtauthenticator.security.JwtRequestFilter;
import com.example.jwtauthenticator.security.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    private AppConfig appConfig;

    public SecurityConfig(JwtUserDetailsService jwtUserDetailsService, PasswordEncoder passwordEncoder, JwtRequestFilter jwtRequestFilter) {
        this.jwtUserDetailsService = jwtUserDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder);
    }



    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .cors().and() // Enable CORS
                .headers()
                    .frameOptions().deny() // X-Frame-Options
                    .xssProtection().xssProtectionEnabled(true).and()
                    .httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(31536000).and()
                    .and()
                .authorizeRequests().antMatchers("/auth/register", "/auth/token", "/auth/login", "/auth/forward", "/auth/forgot-password", "/auth/reset-password", "/auth/refresh", "/auth/verify-email", "/auth/tfa/**", "/auth/google", "/test/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .anyRequest().authenticated().and()
                .exceptionHandling().and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow specific origins for Google Sign-In and frontend
        configuration.setAllowedOrigins(Arrays.asList(
            appConfig.getFrontendBaseUrl(), // Centralized frontend URL
            "http://localhost:3000", // Development fallback
            "http://localhost:3001", // Development fallback
            "https://accounts.google.com" // Google Sign-In
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Tenant-Id", "X-Forward-URL"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
