package com.example.jwtauthenticator.aspect;

import com.example.jwtauthenticator.audit.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {

    @Autowired
    private AuditService auditService;

    @Before("execution(* com.example.jwtauthenticator.controller.AuthController.loginUser(..))")
    public void auditLoginAttempt(JoinPoint joinPoint) {
        String username = ((com.example.jwtauthenticator.model.AuthRequest) joinPoint.getArgs()[0]).username();
        auditService.logEvent(username, "LOGIN_ATTEMPT", "User attempted to log in");
    }

    @AfterReturning("execution(* com.example.jwtauthenticator.controller.AuthController.loginUser(..))")
    public void auditLoginSuccess(JoinPoint joinPoint) {
        String username = ((com.example.jwtauthenticator.model.AuthRequest) joinPoint.getArgs()[0]).username();
        auditService.logEvent(username, "LOGIN_SUCCESS", "User successfully logged in");
    }

    @AfterThrowing(pointcut = "execution(* com.example.jwtauthenticator.controller.AuthController.loginUser(..))", throwing = "ex")
    public void auditLoginFailure(JoinPoint joinPoint, Throwable ex) {
        String username = ((com.example.jwtauthenticator.model.AuthRequest) joinPoint.getArgs()[0]).username();
        auditService.logEvent(username, "LOGIN_FAILURE", "User failed to log in: " + ex.getMessage());
    }

    @Before("execution(* com.example.jwtauthenticator.controller.AuthController.createAuthenticationToken(..))")
    public void auditTokenIssueAttempt(JoinPoint joinPoint) {
        String username = ((com.example.jwtauthenticator.model.AuthRequest) joinPoint.getArgs()[0]).username();
        auditService.logEvent(username, "TOKEN_ISSUE_ATTEMPT", "Attempting to issue JWT token");
    }

    @AfterReturning("execution(* com.example.jwtauthenticator.controller.AuthController.createAuthenticationToken(..))")
    public void auditTokenIssueSuccess(JoinPoint joinPoint) {
        String username = ((com.example.jwtauthenticator.model.AuthRequest) joinPoint.getArgs()[0]).username();
        auditService.logEvent(username, "TOKEN_ISSUE_SUCCESS", "JWT token successfully issued");
    }

    @AfterThrowing(pointcut = "execution(* com.example.jwtauthenticator.controller.AuthController.createAuthenticationToken(..))", throwing = "ex")
    public void auditTokenIssueFailure(JoinPoint joinPoint, Throwable ex) {
        String username = ((com.example.jwtauthenticator.model.AuthRequest) joinPoint.getArgs()[0]).username();
        auditService.logEvent(username, "TOKEN_ISSUE_FAILURE", "Failed to issue JWT token: " + ex.getMessage());
    }

    @Before("execution(* com.example.jwtauthenticator.controller.ProtectedController.*(..))")
    public void auditProtectedAccess(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";
        String methodName = joinPoint.getSignature().getName();
        auditService.logEvent(username, "PROTECTED_ACCESS", "Accessed protected endpoint: " + methodName);
    }
}
