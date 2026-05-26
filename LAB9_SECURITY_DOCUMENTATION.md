# E-Commerce REST API with Spring Security - Lab 9 Documentation

## Project Overview

This project is a Spring Boot-based E-Commerce REST API secured with Spring Security using **session-based authentication (not JWT)** and comprehensive **input validation**. The application demonstrates enterprise-grade security practices including authentication, authorization, role-based access control, and input validation.

## Security Architecture

### 1. Session-Based Authentication

**How It Works:**
- Users authenticate by submitting credentials (username/password) via HTTP Form Login
- Upon successful authentication, Spring Security creates an HTTP session
- The session ID is stored in a cookie named `JSESSIONID` that the browser automatically sends with each request
- Server validates the session cookie to confirm user identity
- This approach is stateful and typically used for web applications with server-side session storage

**Key Features:**
- **Session Fixation Protection**: Spring Security migrates the session after login to prevent session fixation attacks
- **Session Concurrency Control**: By default, one session per user is allowed
- **Automatic Session Invalidation**: Sessions expire after a configurable timeout period
- **CSRF Protection**: Enabled by default for form submissions to prevent Cross-Site Request Forgery attacks

### 2. Authentication Flow

```
1. User requests login page → /login (public)
2. Spring Security displays login form with CSRF token
3. User submits credentials + CSRF token
4. Spring Security authenticates via DaoAuthenticationProvider
5. CustomUserDetailsService loads user from database
6. BCryptPasswordEncoder verifies password
7. If valid: Create session, set JSESSIONID cookie
8. Redirect to requested resource or home page
9. User automatically authenticated for subsequent requests (via JSESSIONID cookie)
```

### 3. Authorization & Access Control

**Role-Based Access Control (RBAC):**
- Users are assigned roles: `USER`, `ADMIN`, `SELLER`
- Endpoints require specific roles for access
- Enforced via `@PreAuthorize` annotations on controller methods

**Public Endpoints (No Authentication):**
- `GET /api/v1/products` - View all products
- `GET /api/v1/products/{id}` - View product details
- `GET /api/v1/products/filter` - Filter products
- `POST /api/v1/auth/register` - User registration
- `GET /login` - Login page

**Protected Endpoints (Authentication Required):**
- `POST /api/v1/orders` - Requires `USER` or `ADMIN` role
- `POST /api/v1/products` - Requires `ADMIN` role
- `PUT /api/v1/products/{id}` - Requires `ADMIN` role
- `PATCH /api/v1/products/{id}` - Requires `ADMIN` role
- `DELETE /api/v1/products/{id}` - Requires `ADMIN` role

### 4. Password Security

**BCrypt Hashing:**
- Passwords are hashed using BCryptPasswordEncoder
- BCrypt automatically generates a unique salt for each password
- Computational cost increases over time to defend against brute-force attacks
- Never store plain-text passwords; always hash before persisting to database

## Input Validation

### Validation Framework

This project uses **Jakarta Validation (formerly javax.validation)** for automatic input validation:

### Validation Constraints Applied

#### RegisterUserDto
```java
@NotBlank(message = "Username is required")
@Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
private String username;

@NotBlank(message = "Email is required")
@Email(message = "Email must be valid")
private String email;

@NotBlank(message = "Password is required")
@Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
private String password;

@Pattern(regexp = "USER|ADMIN|SELLER", message = "Role must be USER, ADMIN, or SELLER")
private String role;
```

#### ProductDTO
```java
@NotBlank(message = "Product name is required")
@Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
private String name;

@NotNull(message = "Product price is required")
@Positive(message = "Product price must be positive")
@DecimalMin(value = "0.01", message = "Product price must be at least 0.01")
private BigDecimal price;

@NotNull(message = "Stock quantity is required")
@PositiveOrZero(message = "Stock must be zero or positive")
private Integer stock;

@Size(max = 500, message = "Product description must not exceed 500 characters")
private String description;
```

### Validation Response Format

When validation fails (e.g., invalid data), the API returns a `400 Bad Request` with detailed error information:

```json
{
  "timestamp": "2025-02-20T10:30:45",
  "status": 400,
  "error": "Validation Failed",
  "message": "Input validation failed. Please check the errors field for details.",
  "errors": {
    "username": "Username must be between 4 and 20 characters",
    "email": "Email must be valid",
    "price": "Product price must be positive"
  }
}
```

### Validation Best Practices

1. **@Valid Annotation**: Used in controller methods to trigger validation
   ```java
   @PostMapping
   public ResponseEntity<?> register(@Valid @RequestBody RegisterUserDto dto) {
       // Validation happens before method execution
   }
   ```

2. **Global Exception Handler**: Centralized handling of validation errors via `GlobalExceptionHandler`

3. **Field-Level Messages**: User-friendly error messages for each validation constraint

## API Reference

### Authentication Endpoints

#### 1. Register User
**Endpoint:** `POST /api/v1/auth/register`
**Access:** Public (no authentication required)

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "password": "SecurePass123!",
  "confirmPassword": "SecurePass123!",
  "role": "USER"
}
```

**Response (201 Created):**
```json
{
  "timestamp": "2025-02-20T10:30:45",
  "status": 201,
  "message": "User registered successfully",
  "user": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "role": "USER",
    "createdAt": "2025-02-20T10:30:45"
  }
}
```

**Error Response (400 Bad Request - Duplicate Username):**
```json
{
  "timestamp": "2025-02-20T10:30:45",
  "status": 400,
  "error": "Username Already Exists",
  "message": "The username 'john_doe' is already taken. Please choose a different one."
}
```

#### 2. Login
**Endpoint:** `POST /api/v1/auth/login`
**Access:** Public (no authentication required)

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "SecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "timestamp": "2025-02-20T10:30:45",
  "status": 200,
  "message": "Login successful. Session cookie has been set.",
  "sessionManagement": "Session-based authentication enabled"
}
```
**Note:** The response includes a `Set-Cookie: JSESSIONID=...` header that the browser stores

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2025-02-20T10:30:45",
  "status": 401,
  "error": "Authentication Failed",
  "message": "Invalid username or password"
}
```

#### 3. Logout
**Endpoint:** `POST /api/v1/auth/logout`
**Access:** Authenticated users

**Response (200 OK):**
```json
{
  "timestamp": "2025-02-20T10:30:45",
  "status": 200,
  "message": "Logout successful. Session has been invalidated."
}
```

### Product Endpoints

#### 1. Get All Products
**Endpoint:** `GET /api/v1/products`
**Access:** Public

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Product Name",
    "description": "Product description",
    "price": "600.00",
    "stock": 100,
    "imageUrl": "image-url.jpg",
    "createdAt": "2025-02-20T10:30:45",
    "updatedAt": "2025-02-20T10:30:45"
  }
]
```

#### 2. Get Product by ID
**Endpoint:** `GET /api/v1/products/{id}`
**Access:** Public
**Parameters:**
- `id` (path): Product ID

**Response (200 OK):** Returns single product object

#### 3. Filter Products
**Endpoint:** `GET /api/v1/products/filter?filterType=<type>&filterValue=<value>`
**Access:** Public

**Supported Filters:**
- `filterType=category&filterValue=Oversized`
- `filterType=name&filterValue=tee`
- `filterType=price_range&filterValue=300,600`

#### 4. Create Product
**Endpoint:** `POST /api/v1/products`
**Access:** `ADMIN` role required
**Authentication:** Required (JSESSIONID cookie)

**Request Body:**
```json
{
  "name": "New Product",
  "description": "Product description",
  "price": 450.00,
  "stock": 100,
  "imageUrl": "product.jpg"
}
```

**Response (201 Created):** Returns created product

**Error Responses:**
- `400 Bad Request` - Validation failed (see validation error format above)
- `401 Unauthorized` - Not authenticated
- `403 Forbidden` - Authenticated but not ADMIN

#### 5. Update Product
**Endpoint:** `PUT /api/v1/products/{id}`
**Access:** `ADMIN` role required
**Authentication:** Required

#### 6. Partial Update Product
**Endpoint:** `PATCH /api/v1/products/{id}`
**Access:** `ADMIN` role required
**Authentication:** Required

#### 7. Delete Product
**Endpoint:** `DELETE /api/v1/products/{id}`
**Access:** `ADMIN` role required
**Authentication:** Required

**Response (204 No Content):** Success with no response body

## Security Configuration Details

### SecurityConfig Class
Located in: `src/main/java/com/ws101/obrino/config/SecurityConfig.java`

**Key Components:**
1. **SecurityFilterChain**: Defines HTTP authorization rules and filter chain
2. **PasswordEncoder**: BCryptPasswordEncoder for secure password hashing
3. **AuthenticationManager**: Orchestrates authentication providers
4. **DaoAuthenticationProvider**: Loads users via CustomUserDetailsService

### Global Exception Handler
Located in: `src/main/java/com/ws101/obrino/exception/GlobalExceptionHandler.java`

**Handles:**
- `MethodArgumentNotValidException` - Bean validation failures (400)
- `ProductNotFoundException` - Resource not found (404)
- `DataIntegrityViolationException` - Database constraint violations (400)
- Generic `Exception` - Unexpected errors (500)

## Testing with Postman

### Postman Collection Steps

1. **Get CSRF Token** (if needed)
   ```
   GET http://localhost:8080/login
   ```

2. **Register User**
   ```
   POST http://localhost:8080/api/v1/auth/register
   Body (JSON):
   {
     "username": "testuser",
     "email": "test@example.com",
     "password": "TestPass123!",
     "confirmPassword": "TestPass123!",
     "role": "USER"
   }
   ```

3. **Login**
   ```
   POST http://localhost:8080/api/v1/auth/login
   Body (JSON):
   {
     "username": "testuser",
     "password": "TestPass123!"
   }
   Note: Save JSESSIONID cookie from response
   ```

4. **Access Protected Resource**
   ```
   POST http://localhost:8080/api/v1/products
   Headers: Include JSESSIONID cookie (automatic in Postman with "Store cookies" enabled)
   Body (JSON):
   {
     "name": "Test Product",
     "description": "A test product",
     "price": 100.00,
     "stock": 50
   }
   ```

## Frontend Integration

### Login Flow
1. User navigates to `login.html`
2. Frontend retrieves CSRF token from `/login` endpoint
3. User submits credentials to `POST /api/v1/auth/login`
4. On success, browser stores `JSESSIONID` cookie
5. Frontend redirects to products page

### CSRF Token Handling
- Spring Security automatically includes CSRF tokens in form submissions
- API endpoints handle CSRF protection automatically
- For AJAX requests, include CSRF token in headers if needed

### Error Handling
- **401 Unauthorized**: Redirect to login page
- **403 Forbidden**: Display "Access Denied" message
- **400 Bad Request**: Display validation error details
- **500 Server Error**: Display generic error message

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL (BCrypt hashed),
    email VARCHAR(100),
    full_name VARCHAR(100),
    role VARCHAR(50) NOT NULL,
    account_enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);
```

## Running the Application

### Prerequisites
- Java 22+
- Gradle 7.0+
- H2 Database (in-memory)

### Build and Run
```bash
# Build the project
./gradlew build -x test

# Run the application
./gradlew bootRun
```

### Default Credentials (if using InitializedData)
- Username: `admin`
- Password: (auto-generated and printed in console logs)
- Role: `ADMIN`

## Deployment Considerations

### Production Checklist
1. **Change Default Credentials**: Remove auto-generated passwords
2. **Enable HTTPS**: Always use HTTPS in production
3. **Configure Session Timeout**: Set appropriate session expiration (e.g., 30 minutes)
4. **Database**: Replace H2 with production database (MySQL, PostgreSQL)
5. **Environment Variables**: Store sensitive data in environment variables
6. **Logging**: Enable comprehensive security logging
7. **Rate Limiting**: Implement rate limiting for authentication endpoints
8. **CORS**: Configure CORS based on frontend domain

## Additional Resources

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Jakarta Validation Specification](https://jakarta.ee/specifications/validation/)
- [OWASP Security Cheat Sheet](https://cheatsheetseries.owasp.org/)
- [BCrypt Password Hashing](https://en.wikipedia.org/wiki/Bcrypt)

## Support & Troubleshooting

### Common Issues

**1. JSESSIONID Cookie Not Being Set**
- Ensure `credentials: 'include'` is set in fetch requests
- Check browser cookie settings
- Verify HTTPS/HTTP consistency

**2. Validation Errors Not Displaying**
- Check that `@Valid` annotation is present on controller method
- Verify DTO class has validation annotations
- Review GlobalExceptionHandler configuration

**3. 403 Forbidden When Accessing Admin Endpoints**
- Verify user role is set to "ADMIN"
- Check `@PreAuthorize` annotations on controller methods
- Confirm `@EnableMethodSecurity` is present in SecurityConfig

## License

This project is part of the Spring Security course and is provided for educational purposes.
