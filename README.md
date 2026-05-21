# E-Commerce API - Spring Boot Backend

A RESTful API backend for an e-commerce product catalog built with Spring Boot. This API provides full CRUD operations and advanced filtering capabilities for managing products with in-memory data storage.

## Project Overview

This Spring Boot application demonstrates REST API fundamentals including:
- **HTTP Methods**: GET, POST, PUT, PATCH, DELETE
- **HTTP Status Codes**: Proper use of 200, 201, 204, 400, 404, 500
- **REST Principles**: Resource-based endpoints, stateless operations
- **Error Handling**: Global exception handling with consistent error responses
- **Input Validation**: Data validation at both controller and service layers
- **In-Memory Storage**: Using Java Collections Framework (List) for data persistence within the application lifecycle

## Technology Stack

- **Language**: Java 22+
- **Framework**: Spring Boot 3.3.0
- **Build Tool**: Gradle
- **Dependencies**:
  - Spring Web (REST support)
  - Lombok (code generation)

## Setup Instructions

### Prerequisites

- Java 22 or higher installed
- Git version control system
- Gradle wrapper (included in the project)

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/EcommerceApi.git
   cd EcommerceApi
   ```

2. **Build the project**
   ```bash
   # Using Gradle wrapper on Windows
   .\gradlew.bat build
   
   # Using Gradle wrapper on Linux/Mac
   ./gradlew build
   ```

3. **Run the application**
   ```bash
   # Using Gradle wrapper
   .\gradlew.bat bootRun     # Windows
   ./gradlew bootRun         # Linux/Mac
   
   # Or using Java directly (after building)
   java -jar build/libs/EcommerceApi-0.0.1-SNAPSHOT.jar
   ```

4. **Verify the server is running**
   
   The API will be accessible at: `http://localhost:8080/api/v1/products`

## API Endpoint Reference

### Base URL
```
http://localhost:8080/api/v1/products
```

---

### 1. GET All Products
**Endpoint**: `GET /api/v1/products`

**Description**: Retrieve all products from the catalog.

**Response Status**: 
- `200 OK` - Successfully retrieved all products

**Response Example**:
```json
[
  {
    "id": 1,
    "name": "One Piece - Oversized Tee",
    "description": "Comfortable oversized fit perfect for casual wear",
    "price": 600.0,
    "category": "Oversized",
    "stock": 50,
    "imageUrl": "product1.jpg"
  },
  {
    "id": 2,
    "name": "Eternal - Oversized Tee",
    "description": "Timeless design with premium quality fabric",
    "price": 300.0,
    "category": "Oversized",
    "stock": 45,
    "imageUrl": "product2.jpg"
  }
]
```

---

### 2. GET Product by ID
**Endpoint**: `GET /api/v1/products/{id}`

**Parameters**:
- `id` (path parameter): The unique product ID (e.g., 1, 2, 3)

**Response Status**:
- `200 OK` - Product found
- `404 Not Found` - Product ID doesn't exist

**Response Example (Success)**:
```json
{
  "id": 1,
  "name": "One Piece - Oversized Tee",
  "description": "Comfortable oversized fit perfect for casual wear",
  "price": 600.0,
  "category": "Oversized",
  "stock": 50,
  "imageUrl": "product1.jpg"
}
```

**Response Example (Error)**:
```json
{
  "timestamp": "2025-05-21T10:30:45.123",
  "status": 404,
  "message": "Product Not Found",
  "details": "Product with ID 999 not found"
}
```

---

### 3. Filter Products
**Endpoint**: `GET /api/v1/products/filter?filterType=<type>&filterValue=<value>`

**Parameters**:
- `filterType` (query parameter): The filter criteria type
  - `category` - Filter by product category
  - `name` - Filter by product name (substring match, case-insensitive)
  - `price_min` - Filter by minimum price (inclusive)
  - `price_max` - Filter by maximum price (inclusive)
  - `price_range` - Filter by price range (format: "min,max")

- `filterValue` (query parameter): The value to filter by

**Response Status**:
- `200 OK` - Filter executed successfully (may return 0 or more products)
- `400 Bad Request` - Invalid filter type or value

**Examples**:

**Filter by Category**:
```
GET /api/v1/products/filter?filterType=category&filterValue=Oversized
```

Response:
```json
[
  {
    "id": 1,
    "name": "One Piece - Oversized Tee",
    "price": 600.0,
    "category": "Oversized",
    "stock": 50
  },
  {
    "id": 4,
    "name": "Urban Vibes - Oversized Tee",
    "price": 550.0,
    "category": "Oversized",
    "stock": 35
  }
]
```

**Filter by Name**:
```
GET /api/v1/products/filter?filterType=name&filterValue=tee
```

**Filter by Price Range**:
```
GET /api/v1/products/filter?filterType=price_range&filterValue=300,500
```

**Filter by Minimum Price**:
```
GET /api/v1/products/filter?filterType=price_min&filterValue=400
```

**Filter by Maximum Price**:
```
GET /api/v1/products/filter?filterType=price_max&filterValue=450
```

---

### 4. CREATE New Product
**Endpoint**: `POST /api/v1/products`

**Request Header**: `Content-Type: application/json`

**Request Body**:
```json
{
  "name": "Premium Cotton Tee",
  "description": "High quality cotton t-shirt",
  "price": 475.0,
  "category": "Standard Round Neck",
  "stock": 80,
  "imageUrl": "premium-tee.jpg"
}
```

**Field Requirements**:
- `name` (String, required): Minimum 3 characters
- `description` (String, optional): Product description
- `price` (Double, required): Must be positive
- `category` (String, required): Product category
- `stock` (Integer, required): Must be non-negative
- `imageUrl` (String, optional): URL to product image

**Response Status**:
- `201 Created` - Product successfully created
- `400 Bad Request` - Invalid data or missing required fields

**Response Example**:
```json
{
  "id": 13,
  "name": "Premium Cotton Tee",
  "description": "High quality cotton t-shirt",
  "price": 475.0,
  "category": "Standard Round Neck",
  "stock": 80,
  "imageUrl": "premium-tee.jpg"
}
```

**Error Response**:
```json
{
  "timestamp": "2025-05-21T10:30:45.123",
  "status": 400,
  "message": "Invalid Request",
  "details": "Product price must be a positive number"
}
```

---

### 5. UPDATE Entire Product (PUT)
**Endpoint**: `PUT /api/v1/products/{id}`

**Description**: Replace the entire product with new data. All fields must be provided.

**Parameters**:
- `id` (path parameter): The product ID to update

**Request Body**:
```json
{
  "name": "Updated Product Name",
  "description": "Updated description",
  "price": 550.0,
  "category": "Oversized",
  "stock": 40,
  "imageUrl": "updated.jpg"
}
```

**Response Status**:
- `200 OK` - Product successfully updated
- `404 Not Found` - Product ID doesn't exist
- `400 Bad Request` - Invalid data

**Response Example**:
```json
{
  "id": 1,
  "name": "Updated Product Name",
  "description": "Updated description",
  "price": 550.0,
  "category": "Oversized",
  "stock": 40,
  "imageUrl": "updated.jpg"
}
```

---

### 6. PARTIAL UPDATE Product (PATCH)
**Endpoint**: `PATCH /api/v1/products/{id}`

**Description**: Update only specified fields. Unspecified fields remain unchanged.

**Parameters**:
- `id` (path parameter): The product ID to update

**Request Body** (only include fields to update):
```json
{
  "price": 425.0,
  "stock": 35
}
```

**Response Status**:
- `200 OK` - Product successfully updated
- `404 Not Found` - Product ID doesn't exist

**Response Example**:
```json
{
  "id": 1,
  "name": "One Piece - Oversized Tee",
  "description": "Comfortable oversized fit perfect for casual wear",
  "price": 425.0,
  "category": "Oversized",
  "stock": 35,
  "imageUrl": "product1.jpg"
}
```

---

### 7. DELETE Product
**Endpoint**: `DELETE /api/v1/products/{id}`

**Description**: Remove a product from the catalog.

**Parameters**:
- `id` (path parameter): The product ID to delete

**Response Status**:
- `204 No Content` - Product successfully deleted
- `404 Not Found` - Product ID doesn't exist

**Response Example**:
- No response body (204 No Content)

---

## HTTP Status Codes Used

| Status Code | Description | When Used |
|---|---|---|
| **200** | OK | Successful GET, PUT, PATCH requests |
| **201** | Created | Successful POST request (resource created) |
| **204** | No Content | Successful DELETE request |
| **400** | Bad Request | Invalid input data, invalid filter type, missing required fields |
| **404** | Not Found | Product ID doesn't exist |
| **500** | Internal Server Error | Unexpected server-side errors |

---

## Input Validation

All endpoints validate input data according to these rules:

### Product Name
- **Required**: Yes
- **Type**: String
- **Constraints**: Minimum 3 characters, cannot be empty

### Product Price
- **Required**: Yes
- **Type**: Double
- **Constraints**: Must be positive (> 0)

### Product Category
- **Required**: Yes
- **Type**: String
- **Constraints**: Cannot be empty

### Product Stock
- **Required**: Yes
- **Type**: Integer
- **Constraints**: Must be non-negative (≥ 0)

### Product Description
- **Required**: No
- **Type**: String

### Product Image URL
- **Required**: No
- **Type**: String

---

## Testing the API

### Using Postman

1. **Import the API**:
   - Open Postman
   - Create a new collection called "E-Commerce API"

2. **Create requests**:

   **GET All Products**:
   - Method: GET
   - URL: `http://localhost:8080/api/v1/products`
   - Click "Send"

   **CREATE Product**:
   - Method: POST
   - URL: `http://localhost:8080/api/v1/products`
   - Headers: `Content-Type: application/json`
   - Body (raw JSON):
     ```json
     {
       "name": "New Awesome Tee",
       "description": "Amazing product",
       "price": 399.0,
       "category": "Standard Round Neck",
       "stock": 100,
       "imageUrl": "awesome.jpg"
     }
     ```

   **GET By ID**:
   - Method: GET
   - URL: `http://localhost:8080/api/v1/products/1`
   - Click "Send"

   **UPDATE (PUT)**:
   - Method: PUT
   - URL: `http://localhost:8080/api/v1/products/1`
   - Headers: `Content-Type: application/json`
   - Body: Complete product JSON

   **PARTIAL UPDATE (PATCH)**:
   - Method: PATCH
   - URL: `http://localhost:8080/api/v1/products/1`
   - Headers: `Content-Type: application/json`
   - Body: Only changed fields

   **DELETE**:
   - Method: DELETE
   - URL: `http://localhost:8080/api/v1/products/1`
   - Click "Send"

### Using cURL

```bash
# Get all products
curl http://localhost:8080/api/v1/products

# Get product by ID
curl http://localhost:8080/api/v1/products/1

# Filter products by category
curl "http://localhost:8080/api/v1/products/filter?filterType=category&filterValue=Oversized"

# Create product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Test Tee","price":300.0,"category":"Standard Round Neck","stock":50}'

# Update product (PUT)
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Updated Tee","description":"Updated","price":400.0,"category":"Oversized","stock":40,"imageUrl":"test.jpg"}'

# Partial update (PATCH)
curl -X PATCH http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{"price":350.0}'

# Delete product
curl -X DELETE http://localhost:8080/api/v1/products/1
```

---

## Known Limitations

### In-Memory Storage
- **Data Persistence**: Data is stored in memory using Java `List<Product>`. All data is **lost when the application restarts**.
- **Concurrency**: No thread-safety mechanisms implemented. Not suitable for production with concurrent access.
- **Scalability**: Limited by available RAM. Not suitable for large datasets.
- **No Transactions**: No ACID compliance or rollback capabilities.

### Features Not Implemented
- Database integration (planned for future versions)
- User authentication/authorization
- Order management
- Shopping cart functionality
- Payment processing
- Product reviews and ratings

### Future Enhancements
- Integrate with SQL database (PostgreSQL, MySQL)
- Implement Spring Data JPA for data access
- Add user authentication (Spring Security)
- Implement pagination and sorting
- Add API documentation with Swagger/OpenAPI
- Implement caching with Redis
- Add comprehensive unit and integration tests

---

## Project Structure

```
EcommerceApi/
├── src/
│   ├── main/
│   │   ├── java/com/ws101/obrino/
│   │   │   ├── EcommerceApiApplication.java     (Main app class)
│   │   │   ├── controller/
│   │   │   │   └── ProductController.java       (REST endpoints)
│   │   │   ├── service/
│   │   │   │   └── ProductService.java          (Business logic)
│   │   │   ├── model/
│   │   │   │   └── Product.java                 (Data entity)
│   │   │   └── exception/
│   │   │       ├── ProductNotFoundException.java (Custom exception)
│   │   │       ├── ErrorResponse.java           (Error model)
│   │   │       └── GlobalExceptionHandler.java  (Exception handling)
│   │   └── resources/
│   │       └── application.properties           (App configuration)
│   └── test/
│       └── java/com/ws101/obrino/
│           └── EcommerceApiApplicationTests.java
├── build.gradle                                  (Gradle build config)
├── settings.gradle                               (Gradle settings)
├── .gitignore                                    (Git ignore rules)
└── README.md                                     (This file)
```

---

## Pair Programming Notes

This project was developed using pair programming methodology:
- Partners: [Partner 1 Name], [Partner 2 Name]
- Pairing sessions: [Number] hours
- Code review: Conducted before each commit
- Test coverage: All endpoints manually tested

---

## Git Workflow

**Main Branch**: `main` - Production-ready code

**Feature Branches**:
- `feat/product-model` - Product entity and Lombok setup
- `feat/service-layer` - ProductService implementation
- `feat/controller-endpoints` - REST endpoints
- `feat/exception-handling` - Global error handling
- `feat/documentation` - API documentation

**Commit Message Format**: `<Type>: <action phrase>`

Examples:
- `feat: implemented product filtering by price range`
- `fix: resolved getAllProducts() returning null values`
- `docs: added complete API documentation`

---

## Contributing

1. Create a feature branch: `git checkout -b feat/your-feature`
2. Make your changes and test thoroughly
3. Commit with meaningful messages
4. Push to remote: `git push origin feat/your-feature`
5. Create a Pull Request for code review

---

## License

This project is part of the WS101 Laboratory course.

---

## Contact & Support

For issues or questions about this API:
- Create an issue in the GitHub repository
- Contact the development team

---

**Last Updated**: May 21, 2025

**Version**: 1.0.0

**Status**: Lab 7 - Initial Implementation Complete ✓
