# Forward Endpoint Documentation

## Overview
The `/forward` endpoint is an asynchronous API that extracts company details from websites by forwarding requests to an external service.

## Endpoint Details

### URL
```
POST /forward
```

### Authentication
- **Public endpoint** - No authentication required
- Accessible without JWT token

### Request Format

#### Headers
```
Content-Type: application/json
```

#### Request Body
```json
{
    "url": "https://xamplify.com"
}
```

**Field Validation:**
- `url` (required): Must be a valid HTTP or HTTPS URL

### Response Format

#### Success Response (200 OK)
```json
{
    "Logo": {
        "Logo": "https://xamplify.com/wp-content/uploads/2023/03/xamplify-logo-400x200-1.png",
        "Symbol": null,
        "Icon": "https://xamplify.com/wp-content/uploads/2021/03/xamplify-Icon-80x80-1.png",
        "Banner": null,
        "LinkedInBanner": "https://media.licdn.com/dms/image/v2/D563DAQFsHqrcWgrYJg/image-scale_191_1128/B56ZbqC9OTG4Ac-/0/1747683366613/xamplify_cover?e=2147483647&v=beta&t=bUyvsk_SN0fmzDVJASgeNWiieznR756uBBbg-t9dXHc"
    },
    "Colors": [
        {
            "hex": "#cc3366",
            "rgb": "rgb(204,51,102)",
            "brightness": 103,
            "name": "link_text"
        }
    ],
    "Fonts": [
        {
            "name": "-apple-system",
            "type": "heading",
            "stack": "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, \"Noto Sans\", sans-serif"
        }
    ],
    "Images": [
        {
            "src": "https://xamplify.com/400",
            "alt": "og:image:width"
        }
    ],
    "Company": {
        "Name": "xAmplify",
        "Description": "Company description...",
        "Industry": "Software Development",
        "Location": "Fremont, California",
        "Founded": "2018",
        "Website": "https://xamplify.com/",
        "SocialLinks": {
            "Twitter": "https://twitter.com/xamplifys",
            "LinkedIn": "https://www.linkedin.com/company/xamplify/",
            "Facebook": "https://www.facebook.com/xAmplifyUSA"
        },
        "CompanySize": "11-50 employees",
        "Headquarters": "Fremont, California",
        "Type": "Privately Held",
        "Specialties": "channel ecosystem, channel marketing, Marketing Automation..."
    },
    "_performance": {
        "extractionTimeSeconds": 71.995,
        "timestamp": "2025-07-10T15:59:09.969Z"
    },
    "_message": "Data extracted dynamically. Accuracy may vary based on website structure."
}
```

#### Error Responses

**400 Bad Request** - Invalid URL format
```json
{
    "timestamp": "2025-07-10T15:59:09.969Z",
    "status": 400,
    "error": "Bad Request",
    "message": "URL must be a valid HTTP or HTTPS URL",
    "path": "/forward"
}
```

**408 Request Timeout** - External API timeout (5 minutes)
```json
{
    "timestamp": "2025-07-10T15:59:09.969Z",
    "status": 408,
    "error": "Request Timeout",
    "message": "Request processing timeout",
    "path": "/forward"
}
```

**502 Bad Gateway** - External API error
```json
{
    "timestamp": "2025-07-10T15:59:09.969Z",
    "status": 502,
    "error": "Bad Gateway",
    "message": "External service error",
    "path": "/forward"
}
```

**500 Internal Server Error** - Unexpected server error
```json
{
    "timestamp": "2025-07-10T15:59:09.969Z",
    "status": 500,
    "error": "Internal Server Error",
    "message": "An unexpected error occurred",
    "path": "/forward"
}
```

## Usage Examples

### cURL Example
```bash
curl -X POST http://localhost:8080/forward \
  -H "Content-Type: application/json" \
  -d '{"url": "https://xamplify.com"}'
```

### JavaScript Example
```javascript
const response = await fetch('http://localhost:8080/forward', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        url: 'https://xamplify.com'
    })
});

const companyDetails = await response.json();
console.log(companyDetails);
```

### Python Example
```python
import requests

url = "http://localhost:8080/forward"
payload = {"url": "https://xamplify.com"}

response = requests.post(url, json=payload)
company_details = response.json()
print(company_details)
```

## Technical Details

### Asynchronous Processing
- The endpoint uses Spring's `@Async` annotation for non-blocking processing
- External API calls are handled asynchronously with CompletableFuture
- Timeout configured for 5 minutes (300 seconds)

### External API Integration
- Forwards requests to: `https://sumnode-main.onrender.com/api/extract-company-details`
- Uses RestTemplate with appropriate timeouts
- Handles various error scenarios (network, HTTP errors, timeouts)

### Performance Considerations
- Processing time varies based on website complexity (typically 30-120 seconds)
- Thread pool configured for concurrent requests
- Connection and read timeouts configured for reliability

### Health Check
```
GET /forward/health
```
Returns: `"Forward service is running"`

## Implementation Files

### Core Components
- **Controller**: `ForwardController.java`
- **Service**: `CompanyDetailsService.java`
- **DTOs**: `ForwardRequest.java`, `CompanyDetailsResponse.java`
- **Configuration**: `AsyncConfig.java`

### Security Configuration
- Added `/forward/**` to permitted URLs in `SecurityConfig.java`
- No authentication required for public access

### Testing
- **Unit Tests**: `ForwardControllerTest.java`, `CompanyDetailsServiceTest.java`
- **Integration Tests**: `ForwardControllerIntegrationTest.java`

## Notes
- The external API may take significant time to process complex websites
- Response accuracy depends on the website structure and content
- The service includes comprehensive error handling and logging
- All requests and responses are logged for monitoring and debugging