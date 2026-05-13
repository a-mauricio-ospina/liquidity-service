# liquidity-service

Citizens Banking microservice responsible for managing liquidity operations.
This service exposes a REST API for deposit inquiries and is built following
clean architecture and enterprise Spring Boot conventions.

---

## Tech Stack

| Technology        | Version    |
|-------------------|------------|
| Java              | 17         |
| Spring Boot       | 3.3.5      |
| Spring Web        | (managed)  |
| Spring Data JPA   | (managed)  |
| Spring Actuator   | (managed)  |
| Spring Boot       | (managed)  |
| PostgreSQL Driver | (managed)  |
| Lombok            | (managed)  |
| Maven             | 3.9+       |

---

## Package Structure

```
src/main/java/com/citizens/banking/liquidity/
├── LiquidityApplication.java       # Application entry point
├── config/                         # Spring configuration beans and filters
│   ├── CorrelationIdFilter.java    # Propagates X-Correlation-ID via MDC
│   └── RequestLoggingFilter.java   # Logs inbound/outbound requests with duration
├── controller/                     # REST controllers (HTTP layer only)
│   ├── DepositController.java
│   └── HealthController.java
├── domain/                         # JPA entities (persistence model)
│   └── DepositEntity.java
├── dto/                            # Immutable data transfer objects
│   ├── DepositResponse.java
│   ├── HealthResponse.java
│   └── ApiErrorResponse.java
├── exception/                      # Domain exceptions and global handler
│   ├── DepositNotFoundException.java
│   └── GlobalExceptionHandler.java
├── repository/                     # Spring Data JPA repositories
│   └── DepositRepository.java
├── service/                        # Business logic
│   └── DepositService.java
└── util/                           # Constants and static helpers
    └── DepositConstants.java
```

---

## Running Locally

### Prerequisites

- Java 17+
- Maven 3.9+ (or use the included `./mvnw` wrapper)
- Docker (to run the PostgreSQL database via Docker Compose)

### 1. Start the database

```bash
docker-compose up -d
```

This starts a PostgreSQL 16 container with the following defaults:

| Property | Value          |
|----------|----------------|
| Host     | `localhost`    |
| Port     | `5432`         |
| Database | `liquiditydb`  |
| User     | `liquidity_user` |
| Password | `liquidity_pass` |

### 2. Build and Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

---

## Configuration

The main configuration file is `src/main/resources/application.yaml`.

Key properties:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/liquiditydb
    username: liquidity_user
    password: liquidity_pass

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health, info
```

---

## API Endpoints

### Deposits

| Method | Endpoint                          | Description          |
|--------|-----------------------------------|----------------------|
| GET    | `/api/v1/deposits`                | List all deposits    |
| GET    | `/api/v1/deposits/{depositId}`    | Get deposit by ID    |

**GET /api/v1/deposits** — example response:

```json
[
  {
    "depositId": 1001,
    "accountId": 42,
    "depositType": "FIXED_TERM",
    "principalAmount": 10000.00,
    "interestRate": 4.25,
    "accruedInterest": 125.50,
    "maturityDate": "2026-12-31",
    "status": "ACTIVE"
  }
]
```

**GET /api/v1/deposits/9999** — not found response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Deposit not found with id: 9999",
  "path": "/api/v1/deposits/9999",
  "timestamp": "2026-05-11T18:00:00Z"
}
```

### Health (custom)

| Method | Endpoint          | Description              |
|--------|-------------------|--------------------------|
| GET    | `/api/v1/health`  | Application health check |

```json
{
  "status": "UP",
  "service": "liquidity-service"
}
```

---

## Spring Actuator Endpoints

| Endpoint           | Description          |
|--------------------|----------------------|
| `/actuator/health` | Application health   |
| `/actuator/info`   | Application metadata |

---

## Correlation ID

Every request is automatically assigned a **Correlation ID** used for distributed tracing.

- If the incoming request contains an `X-Correlation-ID` header, that value is used.
- If absent, a 12-character alphanumeric ID is generated automatically.
- The Correlation ID is:
  - Stored in the SLF4J MDC under the key `correlationId`
  - Returned in the `X-Correlation-ID` response header
  - Included in every log line for that request

Example request/response headers:

```
Request:  X-Correlation-ID: abc123def456
Response: X-Correlation-ID: abc123def456
```

---

## Logging

The service uses SLF4J with Logback. Log lines include the Correlation ID from MDC:

```
2026-05-11 18:00:00.123 [http-nio-8080-exec-1] [abc123def456] INFO  c.c.b.l.s.DepositService - Fetching all deposits from database
```

Log format pattern:

```
%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{correlationId:-NO_ID}] %-5level %logger{36} - %msg%n
```

Request logging (inbound/outbound) is handled by `RequestLoggingFilter`. Actuator endpoints are excluded from request logs.

---

## Docker

### Build Image

```bash
docker build -t liquidity-service:latest .
```

### Run Container

```bash
docker run -p 8080:8080 liquidity-service:latest
```

### Build and Run (one-liner)

```bash
docker build -t liquidity-service:latest . && docker run -p 8080:8080 liquidity-service:latest
```

The Dockerfile uses a **single-stage build**:
- Base image: `eclipse-temurin:17-jdk`
- Copies the pre-built JAR from `target/` into the container
- Runs the application with `java -jar app.jar`

> **Note:** The JAR must be built before the Docker image (`./mvnw clean package -DskipTests`).

---

## Project Conventions

- Controllers contain no business logic — they delegate to services
- Services contain business logic and throw domain exceptions
- All API responses use typed DTOs (immutable, Lombok `@Value` + `@Builder`)
- Error responses follow a standardized `ApiErrorResponse` structure
- Monetary amounts use `BigDecimal` (never `double` or `float`)
- Constants are centralized in `util/DepositConstants`
- All service methods are annotated with `@Transactional(readOnly = true)`
