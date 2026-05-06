# liquidity-service

Citizens Banking microservice responsible for managing liquidity operations.
This service exposes a REST API for deposit inquiries and is built following
clean architecture and enterprise Spring Boot conventions.

---

## Tech Stack

| Technology        | Version  |
|-------------------|----------|
| Java              | 17       |
| Spring Boot       | 3.3.5    |
| Spring Web        | (managed)|
| Spring Actuator   | (managed)|
| Lombok            | (managed)|
| Maven             | 3.9+     |

---

## Package Structure

```
src/main/java/com/citizens/banking/liquidity/
├── LiquidityApplication.java       # Application entry point
├── config/                         # Spring configuration beans
├── controller/                     # REST controllers (HTTP layer only)
│   ├── DepositController.java
│   └── HealthController.java
├── service/                        # Business logic
│   └── DepositService.java
├── dto/                            # Immutable data transfer objects
│   ├── DepositResponse.java
│   ├── HealthResponse.java
│   └── ApiErrorResponse.java
├── exception/                      # Domain exceptions and global handler
│   ├── DepositNotFoundException.java
│   └── GlobalExceptionHandler.java
└── util/                           # Constants and static helpers
    └── DepositConstants.java
```

---

## Running Locally

### Prerequisites

- Java 17+
- Maven 3.9+ (or use the included `./mvnw` wrapper)

### Build and Run

```bash
# Build
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

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
    "depositId": "DEP-1001",
    "accountNumber": "123456789",
    "amount": 1500.00,
    "currency": "USD",
    "status": "PENDING"
  }
]
```

**GET /api/v1/deposits/DEP-9999** — not found response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Deposit not found with id: DEP-9999",
  "path": "/api/v1/deposits/DEP-9999",
  "timestamp": "2026-05-06T18:00:00Z"
}
```

### Health (custom)

| Method | Endpoint          | Description             |
|--------|-------------------|-------------------------|
| GET    | `/api/v1/health`  | Application health check|

```json
{
  "status": "UP",
  "service": "liquidity-service"
}
```

---

## Spring Actuator Endpoints

| Endpoint              | Description            |
|-----------------------|------------------------|
| `/actuator/health`    | Application health     |
| `/actuator/info`      | Application metadata   |

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

The Dockerfile uses a **multi-stage build**:
- Stage 1 compiles the JAR with `eclipse-temurin:17-jdk-alpine`
- Stage 2 runs with `eclipse-temurin:17-jre-alpine` (smaller footprint)
- Spring Boot layertools splits dependencies and application code into separate
  Docker layers for efficient caching on re-deployments
- The process runs as a non-root user (`appuser`) for security

---

## Project Conventions

- Controllers contain no business logic — they delegate to services
- Services contain business logic and throw domain exceptions
- All API responses use typed DTOs (immutable, Lombok `@Value` + `@Builder`)
- Error responses follow a standardized `ApiErrorResponse` structure
- Monetary amounts use `BigDecimal` (never `double` or `float`)
- Constants are centralized in `util/DepositConstants`
