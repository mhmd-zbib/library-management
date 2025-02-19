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
- Docker and Docker Compose
- PostgreSQL 16

### Quick Start

1. Clone the repository:
```bash
git clone https://github.com/mhmd-zbib/library-management
cd library-management
```

2. Create `.env` file in project root:
```properties
POSTGRES_DB=library_db
POSTGRES_USER=library_user
POSTGRES_PASSWORD=library_password
POSTGRES_PORT=5544
```

3. Start with Docker:
```bash
docker-compose up -d

docker-compose logs -f

docker-compose down

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
