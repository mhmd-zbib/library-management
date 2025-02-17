# Library Management API

A robust Library Management  built with Spring Boot, providing RESTful APIs to manage books, patrons, and borrowing records.

## Features

- Complete CRUD operations for books management
- Patron registration and profile management
- Borrowing records tracking system
- RESTful API endpoints
- Data persistence with PostgreSQL
- Containerization with Docker
- Authentication and authorization
- Input validation and error handling
- Comprehensive test coverage
- Transaction management
- API documentation

## Technologies

- Java 17
- Spring Boot 3.x
- PostgreSQL 15
- Docker
- Maven
- JUnit 5
- Spring Security
- Hibernate/JPA

## Prerequisites

- JDK 17 or later
- Docker and Docker Compose
- Maven 3.6 or later
- Git

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/mhmd-zbib/library-management.git
cd library-management
```

### Environment Setup

Create a `.env` file in the project root with the following configurations:

```properties
POSTGRES_DB=library_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=your_password
POSTGRES_HOST=localhost
POSTGRES_PORT=5432
```

### Running with Docker

1. Build and start the containers:
```bash
docker-compose up -d
```

2. The application will be available at:
    - API: http://localhost:8080
    - Swagger Documentation: http://localhost:8080/swagger-ui.html

### Running Locally

1. Start PostgreSQL database:
```bash
docker-compose up -d postgres
```

2. Build the application:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

## API Documentation

### Book Management

```
GET    /api/books          - Retrieve all books
GET    /api/books/{id}     - Retrieve a specific book
POST   /api/books          - Add a new book
PUT    /api/books/{id}     - Update an existing book
DELETE /api/books/{id}     - Remove a book
```

### Patron Management

```
GET    /api/patrons          - Retrieve all patrons
GET    /api/patrons/{id}     - Retrieve a specific patron
POST   /api/patrons          - Register a new patron
PUT    /api/patrons/{id}     - Update patron information
DELETE /api/patrons/{id}     - Remove a patron
```

### Borrowing Management

```
POST   /api/borrow/{bookId}/patron/{patronId}  - Borrow a book
PUT    /api/return/{bookId}/patron/{patronId}  - Return a book
```

## Database Schema

The system uses the following main entities:

- Books (id, title, author, isbn, publication_year, status)
- Patrons (id, name, email, phone, address)
- BorrowingRecords (id, book_id, patron_id, borrow_date, return_date, status)


## Security

The API is secured using Spring Security with JWT authentication. Protected endpoints require a valid JWT token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```
