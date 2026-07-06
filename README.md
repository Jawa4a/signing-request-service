# Signing Request Service

Small Spring Boot backend application that simulates a digital signing request workflow.

## Tech stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Docker Compose
- Bean Validation
- Spring Boot Actuator
- springdoc-openapi / Swagger UI
- JUnit 5
- Mockito
- Testcontainers

## Features

- Create signing requests
- List signing requests
- Filter by status
- Get signing request by ID
- Approve, reject and sign requests
- Validate input data
- Handle errors with structured JSON responses
- Store audit events for request creation and status changes
- OpenAPI/Swagger documentation
- Health endpoint via Actuator

## How to run

```powershell
docker compose up -d
cd backend
.\gradlew.bat bootRun
````

## API documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

## Main endpoints

```text
POST /api/signing-requests
GET  /api/signing-requests
GET  /api/signing-requests?status=CREATED
GET  /api/signing-requests/{id}
POST /api/signing-requests/{id}/approve
POST /api/signing-requests/{id}/reject
POST /api/signing-requests/{id}/sign
GET  /api/signing-requests/{id}/audit-events
GET  /actuator/health
```

## Testing

```powershell
cd backend
.\gradlew.bat test
```