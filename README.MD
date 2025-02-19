# Library Management System

A Spring Boot application for managing library operations, implementing advanced features and best practices.

## Features

- Authentication with JWT
- Books management
- Patron management
- Borrowing system
- Custom logging
- Error handling
- Input validation
- Transaction management
- API documentation with Swagger
- Docker containerization
- PostgreSQL database

## Technical Implementation Highlights

### Architecture & Design
- Clean architecture with proper separation of concerns
- RESTful API design following best practices
- Comprehensive DTO pattern implementation
- Custom annotations for cross-cutting concerns

### Advanced Features

#### Caching
- Implemented Spring Cache for frequently accessed data
- Cache configuration for books and patron details
- Custom cache eviction strategies
- Performance optimization for repeated queries

#### Exception Handling
- Global exception handling with @ControllerAdvice
- Custom exception classes for business logic
- Structured error responses
- Proper HTTP status code mapping
- Validation error handling

#### Logging & Monitoring
- AOP-based logging for all operations
- Custom @LoggableOperation annotation
- Performance metrics logging
- Method-level execution time tracking
- Structured logging with log4j2

#### Security
- JWT-based authentication
- Role-based access control
- Password encryption
- Secure token management

#### Database & Transactions
- PostgreSQL with proper indexing
- @Transactional management
- Optimistic locking for concurrent operations
- Proper relationship mapping
- Database migrations

#### Input Validation
- Request validation using @Valid
- Custom validation annotations
- Complex business rule validations
- Cross-field validations

## Demo Account
```json
{
    "email": "admin@library.com",
    "password": "admin123"
}
```

The system comes pre-loaded with:
- 1 admin user
- 10 sample books
- 5 sample patrons
- 3 borrowing records (1 active, 1 overdue, 1 returned)

## Setup & Running

### Requirements
- Java 21
- Maven 3.9+
- Docker and Docker Compose (optional)
- PostgreSQL 16 (if running locally)

### Local Development Setup

1. Clone the repository:
```bash
git clone https://github.com/mhmd-zbib/library-management
cd library-management
```

2. Create local configuration:
   - Copy `.env.example` to `.env`
   - Set up your local environment variables:
```properties
POSTGRES_DB=library_db
POSTGRES_USER=library_user
POSTGRES_PASSWORD=library_password
POSTGRES_PORT=5544
SERVER_PORT=9000
SPRING_PROFILES_ACTIVE=local
```

3. Set up local PostgreSQL:
   - Install PostgreSQL 16
   - Create database and user:
```sql
CREATE DATABASE library_db;
CREATE USER library_user WITH PASSWORD 'library_password';
GRANT ALL PRIVILEGES ON DATABASE library_db TO library_user;
```

4. Build and run locally:
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### Docker Setup

1. Using Docker Compose:
```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Clean up volumes
docker-compose down -v
```

The application will be available at:
- API: http://localhost:9000/api
- Swagger UI: http://localhost:9000/api/swagger-ui.html

### Security
- JWT authentication required for all endpoints except login/register
- Add token to requests: `Authorization: Bearer <token>`
- Token expires after 24 hours

### Logging
- Operations logged automatically
- Critical actions tracked with custom annotations
- Error logging with stack traces
- Performance monitoring

### Database
- PostgreSQL 16
- Automatic schema creation
- Sample data loaded on startup
- Transaction management
