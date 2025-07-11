# Flexible Auto-Incrementing ID Generator

## Overview

This project implements a flexible, thread-safe, auto-incrementing ID generator that creates IDs in the format `{PREFIX}{NUMBER}` (e.g., MRTFY0001, MKTY0001).

## Key Features

✅ **Dynamic Prefix Support** - Use any prefix dynamically  
✅ **Independent Sequences** - Each prefix maintains its own counter  
✅ **Thread-Safe** - Uses pessimistic locking for concurrency control  
✅ **Configurable Padding** - Number padding configurable via properties  
✅ **Default Prefix** - Fallback to application.properties default  
✅ **Production Ready** - Transactional, scalable, and reliable  

## Configuration

### Application Properties
```properties
# Brand ID Configuration
brand.id.prefix=MRTFY
brand.id.number.padding=4
```

## Database Schema

### ID Sequences Table
```sql
CREATE TABLE id_sequences (
    id BIGSERIAL PRIMARY KEY,
    prefix VARCHAR(10) NOT NULL UNIQUE,
    current_number BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
```

## API Endpoints

### 1. Generate ID with Default Prefix
```http
POST /api/id-generator/generate
```
**Response:**
```json
{
  "id": "MRTFY0001",
  "success": true,
  "message": "ID generated successfully"
}
```

### 2. Generate ID with Custom Prefix
```http
POST /api/id-generator/generate/{prefix}
```
**Example:**
```http
POST /api/id-generator/generate/MKTY
```
**Response:**
```json
{
  "id": "MKTY0001",
  "prefix": "MKTY",
  "success": true,
  "message": "ID generated successfully"
}
```

### 3. Preview Next ID
```http
GET /api/id-generator/preview/{prefix}
```
**Response:**
```json
{
  "nextId": "MKTY0005",
  "prefix": "MKTY",
  "currentNumber": 4,
  "success": true
}
```

### 4. Get Current Number
```http
GET /api/id-generator/current/{prefix}
```
**Response:**
```json
{
  "prefix": "MRTFY",
  "currentNumber": 10,
  "exists": true,
  "success": true
}
```

### 5. Get All Prefixes
```http
GET /api/id-generator/prefixes
```
**Response:**
```json
{
  "prefixes": ["MKTY", "MRTFY", "TEST"],
  "count": 3,
  "success": true
}
```

### 6. Reset Sequence (Admin Only)
```http
POST /api/id-generator/reset/{prefix}?startNumber=0
```
**Response:**
```json
{
  "prefix": "MRTFY",
  "startNumber": 0,
  "success": true,
  "message": "Sequence reset successfully"
}
```

## Java Service Usage

### Basic Usage
```java
@Autowired
private IdGeneratorService idGeneratorService;

// Generate with default prefix
String id1 = idGeneratorService.generateNextId(); // MRTFY0001

// Generate with custom prefix
String id2 = idGeneratorService.generateNextId("MKTY"); // MKTY0001
String id3 = idGeneratorService.generateNextId("MKTY"); // MKTY0002

// Preview without generating
String preview = idGeneratorService.previewNextId("MKTY"); // MKTY0003

// Get current number
Long current = idGeneratorService.getCurrentNumber("MKTY"); // 2
```

### Advanced Usage
```java
// Check if prefix exists
boolean exists = idGeneratorService.prefixExists("MKTY");

// Get all prefixes
List<String> prefixes = idGeneratorService.getAllPrefixes();

// Reset sequence (use with caution!)
idGeneratorService.resetSequence("MKTY", 100L);
```

## Thread Safety & Concurrency

The service uses **pessimistic locking** to ensure thread safety:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT s FROM IdSequence s WHERE s.prefix = :prefix")
Optional<IdSequence> findByPrefixWithLock(@Param("prefix") String prefix);
```

This ensures that:
- Multiple threads can't increment the same sequence simultaneously
- No duplicate IDs are generated
- Database consistency is maintained

## Multi-Brand Architecture

### Why BRAND_ID instead of TENANT_ID?

**TENANT_ID** was originally used for multi-tenancy (multiple organizations sharing the same application). We've switched to **BRAND_ID** because:

1. **Better Business Context** - Aligns with brand-specific operations
2. **Clearer Separation** - Each brand has its own data space
3. **Flexible Scaling** - Easier to manage brand-specific features

### Updated Headers
- **Old:** `X-Tenant-Id: tenant1`
- **New:** `X-Brand-Id: brand1`

### Database Changes
```sql
-- Migration applied automatically
ALTER TABLE users RENAME COLUMN tenant_id TO brand_id;
ALTER TABLE password_reset_codes RENAME COLUMN tenant_id TO brand_id;
```

## Example Scenarios

### Scenario 1: Multiple Brands
```java
// Brand MRTFY generates user IDs
String userMRTFY1 = idGeneratorService.generateNextId("MRTFY"); // MRTFY0001
String userMRTFY2 = idGeneratorService.generateNextId("MRTFY"); // MRTFY0002

// Brand MKTY generates user IDs independently
String userMKTY1 = idGeneratorService.generateNextId("MKTY"); // MKTY0001
String userMKTY2 = idGeneratorService.generateNextId("MKTY"); // MKTY0002
```

### Scenario 2: Different Entity Types
```java
// Generate order IDs
String order1 = idGeneratorService.generateNextId("ORD"); // ORD0001
String order2 = idGeneratorService.generateNextId("ORD"); // ORD0002

// Generate invoice IDs
String invoice1 = idGeneratorService.generateNextId("INV"); // INV0001
String invoice2 = idGeneratorService.generateNextId("INV"); // INV0002
```

## Testing

### Unit Tests
Run the comprehensive test suite:
```bash
mvn test -Dtest=IdGeneratorServiceTest
```

### Integration Testing
```bash
# Start the application
mvn spring-boot:run

# Test via REST API
curl -X POST http://localhost:8080/api/id-generator/generate
curl -X POST http://localhost:8080/api/id-generator/generate/MKTY
curl -X GET http://localhost:8080/api/id-generator/preview/MKTY
```

## Performance Considerations

1. **Database Indexes** - Unique index on prefix for fast lookups
2. **Connection Pooling** - Configured for high concurrency
3. **Pessimistic Locking** - Minimal lock time for better throughput
4. **Caching** - Consider Redis for extremely high-volume scenarios

## Security

- **Admin Endpoints** - Reset functionality should be restricted
- **Input Validation** - Prefix length and character validation
- **Rate Limiting** - Consider rate limiting for public endpoints

## Monitoring

Monitor these metrics:
- ID generation rate per prefix
- Lock wait times
- Database connection usage
- Failed generation attempts

## Migration Guide

### From TenantId to BrandId

1. **Database Migration** - Automatically applied via Flyway
2. **API Changes** - Update headers from `X-Tenant-Id` to `X-Brand-Id`
3. **Code Updates** - All service methods now use `brandId` parameter

### Example Migration
```java
// OLD
authService.updateProfile(username, tenantId, request);
userRepository.findByUsernameAndTenantId(username, tenantId);

// NEW
authService.updateProfile(username, brandId, request);
userRepository.findByUsernameAndBrandId(username, brandId);
```

## Troubleshooting

### Common Issues

1. **Duplicate IDs** - Check if pessimistic locking is working
2. **Performance Issues** - Monitor database locks and connection pool
3. **Prefix Not Found** - Verify prefix exists or will be auto-created
4. **Migration Issues** - Check Flyway migration logs

### Debug Logging
```properties
logging.level.com.example.jwtauthenticator.service.IdGeneratorService=DEBUG
```

## Future Enhancements

- [ ] Redis caching for high-volume scenarios
- [ ] Batch ID generation
- [ ] Custom number formatting patterns
- [ ] Audit trail for ID generation
- [ ] Metrics and monitoring dashboard