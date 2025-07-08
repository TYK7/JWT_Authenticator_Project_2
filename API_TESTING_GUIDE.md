# JWT Authenticator API Testing Guide

## 🚀 Quick Start Testing

### Prerequisites
1. Application running on `http://localhost:8080`
2. PostgreSQL database configured
3. Google OAuth2 credentials set up (for Google Sign-In)
4. Postman or similar API testing tool

## 📋 Complete API Testing Flow

### 1. User Registration & Email Verification

#### Register New User
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: default" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "location": "New York",
    "tenantId": "default"
  }'
```

**Expected Response:**
```json
{
  "message": "User registered successfully. Please check your email to verify your account."
}
```

#### Verify Email (Check email for token)
```bash
curl -X GET "http://localhost:8080/auth/verify-email?token=YOUR_VERIFICATION_TOKEN"
```

### 2. User Authentication

#### Login with Password
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -H "X-Tenant-Id: default" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "tenantId": "default"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "testuser"
}
```

#### Google Sign-In
```bash
curl -X POST http://localhost:8080/auth/google \
  -H "Content-Type: application/json" \
  -d '{
    "idToken": "YOUR_GOOGLE_ID_TOKEN"
  }'
```

**To get Google ID Token:**
1. Visit: `http://localhost:8080/test/google-signin-demo`
2. Click "Sign in with Google"
3. Copy the ID token from the response

### 3. Two-Factor Authentication (2FA)

#### Setup 2FA
```bash
curl -X POST "http://localhost:8080/auth/tfa/setup?username=testuser"
```

**Expected Response:**
```json
{
  "secret": "JBSWY3DPEHPK3PXP",
  "qrCodeUrl": "https://chart.googleapis.com/chart?chs=200x200&chld=M%7C0&cht=qr&chl=otpauth://totp/JWT%2520Authenticator:testuser%3Fsecret%3DJBSWY3DPEHPK3PXP%26issuer%3DJWT%2520Authenticator",
  "manualEntryKey": "JBSWY3DPEHPK3PXP"
}
```

#### Verify 2FA Code
```bash
curl -X POST http://localhost:8080/auth/tfa/verify \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "code": "123456"
  }'
```

#### Enable 2FA
```bash
curl -X POST "http://localhost:8080/auth/tfa/enable?username=testuser"
```

#### Disable 2FA
```bash
curl -X POST "http://localhost:8080/auth/tfa/disable?username=testuser"
```

### 4. Password Reset

#### Request Password Reset
```bash
curl -X POST http://localhost:8080/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com"
  }'
```

#### Reset Password (Check email for token)
```bash
curl -X POST http://localhost:8080/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "token": "YOUR_RESET_TOKEN",
    "newPassword": "newpassword123"
  }'
```

### 5. Protected Endpoints

#### Access Protected Data
```bash
curl -X GET http://localhost:8080/protected/data \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "X-Tenant-Id: default"
```

#### Get User Profile
```bash
curl -X GET http://localhost:8080/protected/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "X-Tenant-Id: default"
```

### 6. Token Management

#### Refresh Token
```bash
curl -X POST http://localhost:8080/auth/refresh \
  -H "Content-Type: application/json" \
  -d "YOUR_REFRESH_TOKEN"
```

## 🧪 Postman Testing

### Import Collections
1. Import `JWT_Authenticator_Postman_Collection.json`
2. Import `JWT_Authenticator_Environment.json`
3. Import `GOOGLE_SIGNIN_POSTMAN_REQUESTS.json` (additional requests)

### Environment Variables
Set these variables in your Postman environment:
- `baseUrl`: `http://localhost:8080`
- `tenantId`: `default`
- `testUsername`: `testuser`
- `testEmail`: `test@example.com`
- `testPassword`: `password123`
- `googleIdToken`: (obtain from Google Sign-In demo)

### Testing Sequence
1. **Register User** → Save verification token from email
2. **Verify Email** → Use token from step 1
3. **Login User** → Tokens automatically saved to environment
4. **Setup 2FA** → Save secret for authenticator app
5. **Test Protected Endpoints** → Uses saved access token
6. **Test Google Sign-In** → Use demo page to get ID token

## 🔍 Database Verification

### Check User Creation
```sql
SELECT username, email, email_verified, auth_provider, tfa_enabled 
FROM users 
WHERE username = 'testuser';
```

### Check Login Logs
```sql
SELECT username, login_method, login_status, login_time 
FROM login_log 
ORDER BY login_time DESC 
LIMIT 10;
```

### Check Password Reset Tokens
```sql
SELECT token, expiry_date, user_id 
FROM password_reset_tokens 
WHERE expiry_date > NOW();
```

## 🚨 Common Issues & Solutions

### 1. Email Not Verified
**Error:** `"Email not verified. Please check your email and verify your account."`
**Solution:** Check email for verification link or use the verification endpoint

### 2. Invalid Google ID Token
**Error:** `"Google Sign-In failed: Invalid Google ID token"`
**Solutions:**
- Ensure Google Client ID is configured correctly
- Use fresh ID token (they expire after 1 hour)
- Check CORS configuration

### 3. 2FA Code Invalid
**Error:** `"Invalid 2FA code"`
**Solutions:**
- Ensure time synchronization on device
- Use current TOTP code (30-second window)
- Verify secret was set up correctly

### 4. Token Expired
**Error:** `"JWT token has expired"`
**Solution:** Use refresh token to get new access token

### 5. Tenant ID Missing
**Error:** `"Tenant ID is required"`
**Solution:** Include `X-Tenant-Id` header in requests

## 📊 Performance Testing

### Load Testing with curl
```bash
# Test login endpoint
for i in {1..100}; do
  curl -X POST http://localhost:8080/auth/login \
    -H "Content-Type: application/json" \
    -H "X-Tenant-Id: default" \
    -d '{"username":"testuser","password":"password123","tenantId":"default"}' &
done
wait
```

### Monitoring
- Check application logs for performance metrics
- Monitor database connections
- Watch memory usage during load tests

## 🔐 Security Testing

### Test Cases
1. **SQL Injection**: Try malicious input in username/email fields
2. **XSS**: Test with script tags in input fields
3. **Brute Force**: Multiple failed login attempts
4. **Token Manipulation**: Modify JWT tokens and test validation
5. **CORS**: Test cross-origin requests

### Security Headers Check
```bash
curl -I http://localhost:8080/auth/login
```

Look for security headers:
- `X-Frame-Options`
- `X-Content-Type-Options`
- `X-XSS-Protection`
- `Strict-Transport-Security` (in production with HTTPS)

## 📈 Monitoring & Health Checks

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Application Info
```bash
curl http://localhost:8080/actuator/info
```

### Swagger Documentation
Visit: `http://localhost:8080/swagger-ui.html`

This comprehensive testing guide covers all aspects of the JWT Authenticator API, including the new Google Sign-In and complete 2FA functionality.