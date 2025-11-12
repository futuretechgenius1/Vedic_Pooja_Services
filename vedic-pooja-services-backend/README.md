# Vedic Pooja Services - Backend

Spring Boot (Java 17) backend for the Vedic Pooja Services platform.

## Tech Stack

- Java 17, Spring Boot 3
- Spring Web, Spring Data JPA, Spring Security (JWT)
- Flyway Migrations
- MySQL
- OpenAPI (Swagger UI)
- Docker / Docker Compose

## Quick Start (Docker)

Prerequisites:
- Docker and Docker Compose

Run:
```
docker compose up --build
```

This will:
- Start MySQL on port 3306 (root/password)
- Build and run the backend on port 8080

Swagger UI:
- http://localhost:8080/swagger-ui/index.html

Health:
- http://localhost:8080/actuator/health

## Quick Start (Local)

Prerequisites:
- JDK 17
- Maven
- MySQL running locally

Environment:
```
export DB_URL="jdbc:mysql://localhost:3306/vedicpooja?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export DB_USERNAME="root"
export DB_PASSWORD="password"
export JWT_SECRET="dev-secret-change-me"
```

Run:
```
mvn spring-boot:run
```

## API Overview (MVP)

- Auth
  - POST /api/auth/register
  - POST /api/auth/login
  - GET  /api/auth/me

- Services (catalog)
  - GET  /api/services
  - POST /api/admin/services (ADMIN)

- Purohit
  - POST /api/purohits/onboard (USER authenticated)
  - POST /api/purohits/me/availability (PUROHIT authenticated)

- Booking
  - POST /api/bookings/hold (USER authenticated)

Authorization:
- Bearer JWT in `Authorization` header for protected endpoints.

## Database Migrations

Flyway runs automatically at startup, creating all core tables:
- users, purohits, services, purohit_services, availability, bookings, payments, reviews, settlements

## Notes

- JWT secret: provide a sufficiently strong secret in production.
- CORS is enabled with default configuration; adjust as needed.
- This is a modular monolith starter. Additional modules (payments, reviews, admin ops, notifications) can be added incrementally.

## License

Proprietary - All rights reserved.