# Liquidity Service

A Spring Boot microservice for managing deposit lifecycle operations within the Citizens Banking platform.

---

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Project Structure](#project-structure)
4. [Domain: Deposit](#domain-deposit)
5. [API Endpoints](#api-endpoints)
6. [Configuration](#configuration)
7. [Running the Service](#running-the-service)
8. [Testing](#testing)
9. [Conventions](#conventions)

---

## Overview

`liquidity-service` handles the creation and management of **Deposits**, **Deposit Rates**, and **Deposit Sub-Accounts**. It exposes a RESTful API backed by PostgreSQL and follows a modular, domain-driven package structure.

---

## Architecture

The service uses a **modular clean architecture** approach, organized by business domain:

```
presentation (API controllers)
      │
application (orchestration + service layer)
      │
domain (entities / business objects)
      │
infrastructure (repositories / persistence)
```

Each domain is self-contained. Shared concerns (exception handling, security, config) live outside domain packages.

---

## Project Structure

```
com.citizens.banking.liquidity
│
├── common/
│   ├── HealthController.java          # GET /health
│   └── HealthResponse.java
│
├── config/                            # Spring configuration beans
│
├── security/                          # Security filters (Correlation ID, etc.)
│
├── exception/
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice
│   ├── ApiErrorResponse.java
│   ├── DepositNotFoundException.java
│   ├── DepositRateNotFoundException.java
│   └── DepositSubAccountNotFoundException.java
│
└── deposit/
    ├── api/
    │   ├── DepositController.java
    │   ├── DepositRateController.java
    │   └── DepositSubAccountController.java
    │
    ├── application/
    │   ├── orchestration/
    │   │   └── DepositWorkflowOrchestrator.java
    │   └── service/
    │       ├── DepositService.java
    │       ├── DepositRateService.java
    │       └── DepositSubAccountService.java
    │
    ├── domain/
    │   └── model/
    │       ├── DepositEntity.java
    │       ├── DepositRateEntity.java
    │       └── DepositSubAccountEntity.java
    │
    ├── infrastructure/
    │   ├── persistence/               # package-info (future persistence adapters)
    │   └── repository/
    │       ├── DepositRepository.java
    │       ├── DepositRateRepository.java
    │       └── DepositSubAccountRepository.java
    │
    ├── dto/
    │   ├── request/
    │   │   ├── CreateDepositRequest.java
    │   │   ├── CreateDepositRateRequest.java
    │   │   └── CreateDepositSubAccountRequest.java
    │   └── response/
    │       ├── DepositResponse.java
    │       ├── DepositRateResponse.java
    │       └── DepositSubAccountResponse.java
    │
    └── mapper/                        # package-info (future mapping adapters)
```

---

## Domain: Deposit

### Entities

| Entity | Table | Description |
|---|---|---|
| `DepositEntity` | `deposit` | Core deposit record linked to an external `accountId` |
| `DepositRateEntity` | `deposit_rate` | Rate version assigned to a deposit |
| `DepositSubAccountEntity` | `deposit_sub_account` | Party share within a deposit |

> **Note:** `DepositEntity` stores `accountId` as a plain `Long` column. Account management is handled externally and is not part of this service's domain.

### Orchestration

`DepositWorkflowOrchestrator` acts as the single entry point from the API layer into the deposit domain. Controllers call the orchestrator, which delegates to the appropriate service. This keeps controllers free of coordination logic and provides a natural extension point for future validations and side-effects.

---

## API Endpoints

### Health

| Method | Path | Description |
|---|---|---|
| GET | `/health` | Service health check |

### Deposits

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/deposits` | List all deposits |
| GET | `/api/v1/deposits/{id}` | Get deposit by ID |
| POST | `/api/v1/deposits` | Create a new deposit |

### Deposit Rates

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/deposit-rates` | List all deposit rates |
| GET | `/api/v1/deposit-rates/{id}` | Get deposit rate by ID |
| POST | `/api/v1/deposit-rates` | Create a new deposit rate |

### Deposit Sub-Accounts

| Method | Path | Description |
|---|---|---|
| GET | `/api/v1/deposit-sub-accounts` | List all deposit sub-accounts |
| GET | `/api/v1/deposit-sub-accounts/{id}` | Get deposit sub-account by ID |
| POST | `/api/v1/deposit-sub-accounts` | Create a new deposit sub-account |

---

## Configuration

### `application.properties` / `application.yml`

Key properties:

| Property | Description |
|---|---|
| `spring.datasource.url` | PostgreSQL JDBC URL |
| `spring.datasource.username` | Database username |
| `spring.datasource.password` | Database password |
| `spring.jpa.hibernate.ddl-auto` | Schema generation strategy |
| `springdoc.api-docs.path` | OpenAPI spec path (default `/v3/api-docs`) |
| `springdoc.swagger-ui.path` | Swagger UI path (default `/swagger-ui.html`) |

### Environment Profiles

| Profile | Usage |
|---|---|
| `default` | Local development |
| `test` | Integration / CI test runs |
| `prod` | Production |

---

## Running the Service

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ running and accessible

### Start

```bash
./mvnw spring-boot:run
```

### Swagger UI

```
http://localhost:8080/swagger-ui.html
```

---

## Testing

### Unit Tests

```bash
./mvnw test
```

All unit tests use **JUnit 5 + Mockito** and do not require a running database. Integration tests that require PostgreSQL use `@SpringBootTest` with a real connection.

### Test Coverage Summary

| Test Class | Scope | Tests |
|---|---|---|
| `DepositControllerTest` | Web layer (MockMvc) | 5 |
| `DepositRateControllerTest` | Web layer (MockMvc) | 5 |
| `DepositSubAccountControllerTest` | Web layer (MockMvc) | 5 |
| `DepositServiceTest` | Service unit tests | 5 |
| `DepositRateServiceTest` | Service unit tests | 6 |
| `DepositSubAccountServiceTest` | Service unit tests | 6 |
| `LiquidityApplicationTests` | Spring context smoke test | 1 |

**Total: 33 tests, 0 failures**

---

## Conventions

### Naming

| Artifact | Convention | Example |
|---|---|---|
| Classes | PascalCase | `DepositService` |
| Methods / Variables | camelCase | `findById`, `depositAmount` |
| Constants | ALL_CAPS | `MAX_RETRY_ATTEMPTS` |
| REST paths | kebab-case | `/deposit-sub-accounts` |

### Package Layout per Domain

```
{domain}/
├── api/            → REST controllers (@RestController)
├── application/
│   ├── orchestration/  → workflow coordinators
│   └── service/        → business logic
├── domain/model/   → JPA entities
├── infrastructure/repository/  → Spring Data repositories
└── dto/
    ├── request/    → inbound payloads (@Valid)
    └── response/   → outbound payloads
```

### Error Handling

All exceptions are handled centrally by `GlobalExceptionHandler`. Every error response uses `ApiErrorResponse` with the following fields:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Deposit with id 99 not found",
  "path": "/api/v1/deposits/99",
  "timestamp": "2026-05-19T21:00:00Z",
  "fieldErrors": {}
}
```

### Request Tracing

Every inbound request receives a `correlationId` (UUID) injected as an MDC key `correlationId` via a security/filter component. All log lines include this ID for distributed tracing.
