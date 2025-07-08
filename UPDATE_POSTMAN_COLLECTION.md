# How to Update Postman Collection with Google Sign-In

## Manual Update Instructions

Since the JSON structure is complex, here's how to manually add the Google Sign-In request to your existing Postman collection:

### Step 1: Open Postman Collection
1. Import the existing `JWT_Authenticator_Postman_Collection.json`
2. Navigate to the "Authentication" folder

### Step 2: Add Google Sign-In Request
1. Right-click on "Authentication" folder
2. Select "Add Request"
3. Name it "Google Sign-In"

### Step 3: Configure the Request
**Method:** POST
**URL:** `{{baseUrl}}/auth/google`

**Headers:**
```
Content-Type: application/json
```

**Body (raw JSON):**
```json
{
    "idToken": "{{googleIdToken}}"
}
```

**Tests Script:**
```javascript
if (pm.response.code === 200) {
    const response = pm.response.json();
    pm.environment.set('accessToken', response.token);
    pm.environment.set('refreshToken', response.refreshToken);
    console.log('Google Sign-In successful, tokens saved to environment');
    console.log('User:', response.username);
}
```

**Description:**
```
Authenticate user with Google OAuth2 ID token. The idToken should be obtained from Google Sign-In on the frontend.
```

### Step 4: Add Demo Page Request
1. Add another request named "Google Sign-In Demo Page"
2. **Method:** GET
3. **URL:** `{{baseUrl}}/test/google-signin-demo`
4. **Description:** Access the Google Sign-In demo page for testing

### Step 5: Update Environment Variables
Add these variables to your environment:
- `googleIdToken` (secret) - Empty initially, fill with actual token
- `tfaSecret` (secret) - For 2FA testing
- `tfaCode` (default) - For 2FA code testing

### Step 6: Export Updated Collection
1. Right-click on collection name
2. Select "Export"
3. Choose "Collection v2.1"
4. Save as `JWT_Authenticator_Updated_Collection.json`

## Alternative: Use the Additional Requests File

Instead of manually updating, you can:
1. Import the main collection: `JWT_Authenticator_Postman_Collection.json`
2. Import the additional requests: `GOOGLE_SIGNIN_POSTMAN_REQUESTS.json`
3. Use both collections together

## Testing Flow with Google Sign-In

1. **Setup Environment Variables**
   - Set `baseUrl` to `http://localhost:8080`
   - Set `tenantId` to `default`

2. **Get Google ID Token**
   - Use "Google Sign-In Demo Page" request
   - Click "Sign in with Google" on the demo page
   - Copy the ID token from the response

3. **Set Token in Environment**
   - Set `googleIdToken` variable with the copied token

4. **Test Google Sign-In**
   - Run "Google Sign-In" request
   - Tokens will be automatically saved to environment

5. **Test Protected Endpoints**
   - Use the saved access token for protected requests

## Complete API Testing Sequence

1. Register User → Verify Email → Login (traditional)
2. Google Sign-In (OAuth2)
3. Setup 2FA → Verify Code → Enable 2FA
4. Test Protected Endpoints
5. Refresh Tokens
6. Password Reset Flow

This ensures comprehensive testing of all authentication methods and features.