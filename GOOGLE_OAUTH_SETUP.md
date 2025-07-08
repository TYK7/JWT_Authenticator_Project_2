# Google OAuth2 Setup Guide

## 1. Google Cloud Console Setup

### Step 1: Create Google Cloud Project
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable the Google+ API or Google Identity API

### Step 2: Create OAuth2 Credentials
1. Go to **APIs & Services** > **Credentials**
2. Click **Create Credentials** > **OAuth 2.0 Client IDs**
3. Configure the consent screen if prompted
4. Choose **Web application** as application type
5. Add authorized origins:
   - `http://localhost:3000` (for local frontend development)
   - `http://192.168.1.22:3000` (for network access)
   - Add your production domain
6. Add authorized redirect URIs (if needed for your frontend)
7. Copy the **Client ID** - you'll need this

### Step 3: Configure Backend
1. Open `src/main/resources/application.properties`
2. Replace `YOUR_GOOGLE_CLIENT_ID_HERE` with your actual Google Client ID:
   ```properties
   google.oauth2.client-id=123456789-abcdefghijklmnop.apps.googleusercontent.com
   ```

## 2. Frontend Integration Examples

### React Example with Google Identity Services
```javascript
// Install: npm install @google-cloud/local-auth google-auth-library

import { GoogleAuth } from 'google-auth-library';

// Initialize Google Sign-In
useEffect(() => {
  window.google.accounts.id.initialize({
    client_id: 'YOUR_GOOGLE_CLIENT_ID',
    callback: handleGoogleSignIn
  });
  
  window.google.accounts.id.renderButton(
    document.getElementById('google-signin-button'),
    { theme: 'outline', size: 'large' }
  );
}, []);

// Handle Google Sign-In response
const handleGoogleSignIn = async (response) => {
  try {
    const result = await fetch('http://192.168.1.22:8080/auth/google', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        idToken: response.credential
      })
    });
    
    const data = await result.json();
    
    if (result.ok) {
      // Store JWT tokens
      localStorage.setItem('accessToken', data.token);
      localStorage.setItem('refreshToken', data.refreshToken);
      // Redirect to dashboard
    } else {
      console.error('Google Sign-In failed:', data);
    }
  } catch (error) {
    console.error('Error:', error);
  }
};
```

### HTML + JavaScript Example
```html
<!DOCTYPE html>
<html>
<head>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
</head>
<body>
    <div id="g_id_onload"
         data-client_id="YOUR_GOOGLE_CLIENT_ID"
         data-callback="handleCredentialResponse">
    </div>
    <div class="g_id_signin" data-type="standard"></div>

    <script>
        function handleCredentialResponse(response) {
            fetch('http://192.168.1.22:8080/auth/google', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    idToken: response.credential
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.token) {
                    localStorage.setItem('accessToken', data.token);
                    localStorage.setItem('refreshToken', data.refreshToken);
                    window.location.href = '/dashboard';
                }
            })
            .catch(error => console.error('Error:', error));
        }
    </script>
</body>
</html>
```

## 3. API Endpoints

### Google Sign-In Endpoint
```
POST /auth/google
Content-Type: application/json

{
  "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2NzAyN..."
}
```

**Success Response (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Error Response (400):**
```json
{
  "error": "Google Sign-In failed: Invalid Google ID token"
}
```

## 4. Testing

### Using Postman
1. Get a Google ID token from your frontend
2. Create a POST request to `http://192.168.1.22:8080/auth/google`
3. Set Content-Type to `application/json`
4. Body:
   ```json
   {
     "idToken": "YOUR_ACTUAL_GOOGLE_ID_TOKEN"
   }
   ```

### Using curl
```bash
curl -X POST http://192.168.1.22:8080/auth/google \
  -H "Content-Type: application/json" \
  -d '{"idToken":"YOUR_ACTUAL_GOOGLE_ID_TOKEN"}'
```

## 5. Security Considerations

1. **Client ID Validation**: The backend validates that the token was issued for your specific Google Client ID
2. **Token Expiration**: Google ID tokens expire after 1 hour
3. **HTTPS in Production**: Always use HTTPS in production
4. **CORS Configuration**: Configure CORS properly for your frontend domain

## 6. Troubleshooting

### Common Issues:
1. **"Google OAuth2 client ID not configured"**: Check your `application.properties` file
2. **"Invalid Google ID token"**: Ensure the token is fresh and valid
3. **CORS errors**: Configure CORS in your Spring Boot application
4. **Token verification fails**: Check that your Google Client ID is correct

### Debug Logs:
Enable debug logging in `application.properties`:
```properties
logging.level.com.example.jwtauthenticator.service.GoogleTokenVerificationService=DEBUG
```