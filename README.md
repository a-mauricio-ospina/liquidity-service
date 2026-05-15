# liquidity-service

Citizens Banking microservice responsible for managing liquidity operations.
This service exposes a REST API for deposit inquiries, customer management, and account management.
It is built following clean architecture and enterprise Spring Boot conventions.

---

## Tech Stack

| Technology              | Version    |
|-------------------------|------------|
| Java                    | 17         |
| Spring Boot             | 3.3.5      |
| Spring Web              | (managed)  |
| Spring Data JPA         | (managed)  |
| Spring Validation       | (managed)  |
| Spring Actuator         | (managed)  |
| PostgreSQL Driver       | (managed)  |
| Springdoc OpenAPI UI    | 2.6.0      |
| Lombok                  | (managed)  |
| Maven                   | 3.9+       |

---

## Package Structure

```
src/main/java/com/citizens/banking/liquidity/
├── LiquidityApplication.java       # Application entry point
├── config/                         # Spring configuration beans and filters
│   ├── CorrelationIdFilter.java    # Propagates X-Correlation-ID via MDC
│   ├── OpenApiConfig.java          # Springdoc OpenAPI metadata (title, version, description)
│   └── RequestLoggingFilter.java   # Logs inbound/outbound requests with duration
├── controller/                     # REST controllers (HTTP layer only)
│   ├── AccountController.java
│   ├── CustomerController.java
│   ├── DepositController.java
│   ├── DepositRateController.java
│   ├── DepositSubAccountController.java
│   ├── HealthController.java
│   └── MarketRateVersionController.java
├── domain/                         # JPA entities (persistence model)
│   ├── AccountEntity.java
│   ├── CustomerEntity.java
│   ├── DepositEntity.java
│   ├── DepositRateEntity.java
│   ├── DepositSubAccountEntity.java
│   └── MarketRateVersionEntity.java
├── dto/                            # Immutable data transfer objects
│   ├── AccountResponse.java
│   ├── ApiErrorResponse.java
│   ├── CreateAccountRequest.java            # Request DTO with Jakarta Validation annotations
│   ├── CreateCustomerRequest.java           # Request DTO with Jakarta Validation annotations
│   ├── CreateDepositRateRequest.java        # Request DTO with Jakarta Validation annotations
│   ├── CreateDepositRequest.java            # Request DTO with Jakarta Validation annotations
│   ├── CreateDepositSubAccountRequest.java  # Request DTO with Jakarta Validation annotations
│   ├── CreateMarketRateVersionRequest.java  # Request DTO with Jakarta Validation annotations
│   ├── CustomerResponse.java
│   ├── DepositRateResponse.java
│   ├── DepositResponse.java
│   ├── DepositSubAccountResponse.java
│   ├── HealthResponse.java
│   └── MarketRateVersionResponse.java
├── exception/                      # Domain exceptions and global handler
│   ├── AccountNotFoundException.java
│   ├── CustomerNotFoundException.java
│   ├── DepositNotFoundException.java
│   ├── DepositRateNotFoundException.java
│   ├── DepositSubAccountNotFoundException.java
│   ├── GlobalExceptionHandler.java
│   └── MarketRateVersionNotFoundException.java
├── repository/                     # Spring Data JPA repositories
│   ├── AccountRepository.java
│   ├── CustomerRepository.java
│   ├── DepositRateRepository.java
│   ├── DepositRepository.java
│   ├── DepositSubAccountRepository.java
│   └── MarketRateVersionRepository.java
├── service/                        # Business logic
│   ├── AccountService.java
│   ├── CustomerService.java
│   ├── DepositRateService.java
│   ├── DepositService.java
│   ├── DepositSubAccountService.java
│   └── MarketRateVersionService.java
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

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /swagger-ui/index.html
  paths-to-exclude: /actuator/**
```

---

## Database Entities

### Customer (`customer` table)

| Column          | PostgreSQL Type | Java Type        | Constraints              |
|-----------------|-----------------|------------------|--------------------------|
| `customer_id`   | `BIGINT`        | `Long`           | PK, auto-generated       |
| `customer_name` | `VARCHAR(255)`  | `String`         | NOT NULL                 |
| `customer_type` | `VARCHAR(50)`   | `String`         | NOT NULL                 |
| `status`        | `VARCHAR(20)`   | `String`         | NOT NULL                 |
| `rm_id`         | `BIGINT`        | `Long`           | NOT NULL                 |
| `channel`       | `VARCHAR(50)`   | `String`         | NOT NULL                 |
| `region`        | `VARCHAR(50)`   | `String`         | NOT NULL                 |
| `created_at`    | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                 |
| `updated_at`    | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                 |

### Account (`account` table)

Each account belongs to a `Customer` via a Many-to-One relationship (`customer_id` FK).

| Column           | PostgreSQL Type | Java Type        | Constraints              |
|------------------|-----------------|------------------|--------------------------|
| `account_id`     | `BIGINT`        | `Long`           | PK, auto-generated       |
| `customer_id`    | `BIGINT`        | `CustomerEntity` | FK → `customer`, NOT NULL |
| `account_number` | `VARCHAR(30)`   | `String`         | NOT NULL                 |
| `account_type`   | `VARCHAR(50)`   | `String`         | NOT NULL                 |
| `currency`       | `VARCHAR(3)`    | `String`         | NOT NULL, ISO-4217       |
| `status`         | `VARCHAR(20)`   | `String`         | NOT NULL                 |
| `effective_from` | `DATE`          | `LocalDate`      | NOT NULL                 |
| `effective_till` | `DATE`          | `LocalDate`      | nullable                 |
| `created_at`     | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                 |
| `updated_at`     | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                 |

### Deposit (`deposit` table)

Each deposit belongs to an `Account` via a Many-to-One relationship (`account_id` FK).

| Column           | PostgreSQL Type  | Java Type        | Constraints               |
|------------------|-----------------|------------------|---------------------------|
| `deposit_id`     | `BIGINT`        | `Long`           | PK, auto-generated        |
| `account_id`     | `BIGINT`        | `AccountEntity`  | FK → `account`, NOT NULL  |
| `dpf_ref_id`     | `VARCHAR(100)`  | `String`         | NOT NULL                  |
| `deposit_amount` | `NUMERIC(18,2)` | `BigDecimal`     | NOT NULL                  |
| `currency`       | `VARCHAR(3)`    | `String`         | NOT NULL, ISO-4217        |
| `status`         | `VARCHAR(20)`   | `String`         | NOT NULL                  |
| `created_at`     | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                  |
| `updated_at`     | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                  |

### DepositSubAccount (`deposit_sub_account` table)

Each deposit sub-account belongs to a `Deposit` via a Many-to-One relationship (`deposit_id` FK).

| Column                    | PostgreSQL Type  | Java Type        | Constraints               |
|---------------------------|-----------------|------------------|---------------------------|
| `deposit_sub_account_id`  | `BIGINT`        | `Long`           | PK, auto-generated        |
| `deposit_id`              | `BIGINT`        | `DepositEntity`  | FK → `deposit`, NOT NULL  |
| `party_name`              | `VARCHAR(255)`  | `String`         | NOT NULL                  |
| `share`                   | `NUMERIC(5,2)`  | `BigDecimal`     | NOT NULL                  |
| `rate`                    | `NUMERIC(18,2)` | `BigDecimal`     | NOT NULL                  |
| `created_at`              | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                  |
| `updated_at`              | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL                  |

### MarketRateVersion (`market_rate_version` table)

Standalone entity — no foreign keys to other tables. Intended to hold versioned market rate snapshots for future use by rate-linked entities.

| Column            | PostgreSQL Type  | Java Type        | Constraints        |
|-------------------|-----------------|------------------|--------------------|
| `rate_version_id` | `BIGINT`        | `Long`           | PK, auto-generated |
| `base_rate`       | `NUMERIC(18,2)` | `BigDecimal`     | NOT NULL           |
| `spread`          | `NUMERIC(18,2)` | `BigDecimal`     | NOT NULL           |
| `all_in_rate`     | `NUMERIC(18,2)` | `BigDecimal`     | NOT NULL           |
| `effective_from`  | `DATE`          | `LocalDate`      | NOT NULL           |
| `effective_till`  | `DATE`          | `LocalDate`      | nullable           |
| `created_at`      | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL           |
| `updated_at`      | `TIMESTAMPTZ`   | `OffsetDateTime` | NOT NULL           |

### DepositRate (`deposit_rate` table)

Each deposit rate links a `Deposit` with a `MarketRateVersion` via two Many-to-One relationships.

| Column            | PostgreSQL Type  | Java Type                  | Constraints                              |
|-------------------|-----------------|----------------------------|------------------------------------------|
| `deposit_rate_id` | `BIGINT`        | `Long`                     | PK, auto-generated                       |
| `deposit_id`      | `BIGINT`        | `DepositEntity`            | FK → `deposit`, NOT NULL                 |
| `rate_version_id` | `BIGINT`        | `MarketRateVersionEntity`  | FK → `market_rate_version`, NOT NULL     |
| `all_in_rate`     | `NUMERIC(18,2)` | `BigDecimal`               | NOT NULL                                 |
| `status`          | `VARCHAR(20)`   | `String`                   | NOT NULL                                 |
| `created_at`      | `TIMESTAMPTZ`   | `OffsetDateTime`           | NOT NULL                                 |
| `updated_at`      | `TIMESTAMPTZ`   | `OffsetDateTime`           | NOT NULL                                 |

---

## API Endpoints

### Customers

| Method | Endpoint                            | Description            |
|--------|-------------------------------------|------------------------|
| GET    | `/api/v1/customers`                 | List all customers     |
| GET    | `/api/v1/customers/{customerId}`    | Get customer by ID     |
| POST   | `/api/v1/customers`                 | Create a new customer  |

**GET /api/v1/customers** — example response:

```json
[
  {
    "customerId": 1,
    "customerName": "Acme Corporation",
    "customerType": "CORPORATE",
    "status": "ACTIVE",
    "rmId": 501,
    "channel": "DIGITAL",
    "region": "NORTHEAST",
    "createdAt": "2026-05-14T11:00:00-05:00",
    "updatedAt": "2026-05-14T11:00:00-05:00"
  }
]
```

**GET /api/v1/customers/99** — not found response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Customer not found with id: 99",
  "path": "/api/v1/customers/99",
  "timestamp": "2026-05-14T16:00:00Z",
  "fieldErrors": null
}
```

**POST /api/v1/customers** — example request:

```json
{
  "customerName": "Acme Corporation",
  "customerType": "CORPORATE",
  "status": "ACTIVE",
  "rmId": 501,
  "channel": "DIGITAL",
  "region": "NORTHEAST"
}
```

`HTTP 201 Created` — example response body:

```json
{
  "customerId": 1,
  "customerName": "Acme Corporation",
  "customerType": "CORPORATE",
  "status": "ACTIVE",
  "rmId": 501,
  "channel": "DIGITAL",
  "region": "NORTHEAST",
  "createdAt": "2026-05-14T11:00:00-05:00",
  "updatedAt": "2026-05-14T11:00:00-05:00"
}
```

**POST /api/v1/customers** — validation error response (`HTTP 400 Bad Request`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/customers",
  "timestamp": "2026-05-14T16:00:00Z",
  "fieldErrors": {
    "customerName": "must not be blank",
    "customerType": "must not be blank",
    "status": "must not be blank",
    "rmId": "must not be null",
    "channel": "must not be blank",
    "region": "must not be blank"
  }
}
```

### Validation Rules — `CreateCustomerRequest`

| Field          | Constraints                          |
|----------------|--------------------------------------|
| `customerName` | `@NotBlank`, `@Size(max = 255)`      |
| `customerType` | `@NotBlank`, `@Size(max = 50)`       |
| `status`       | `@NotBlank`, `@Size(max = 20)`       |
| `rmId`         | `@NotNull`, `@Positive`              |
| `channel`      | `@NotBlank`, `@Size(max = 50)`       |
| `region`       | `@NotBlank`, `@Size(max = 50)`       |

Validation errors are handled globally by `GlobalExceptionHandler` and returned as a structured `fieldErrors` map with `HTTP 400`.

---

### Deposits

| Method | Endpoint                          | Description          |
|--------|-----------------------------------|----------------------|
| GET    | `/api/v1/deposits`                | List all deposits    |
| GET    | `/api/v1/deposits/{depositId}`    | Get deposit by ID    |
| POST   | `/api/v1/deposits`                | Create a new deposit |

Each deposit is linked to an existing `Account` via `accountId`. Creating a deposit with a non-existent `accountId` returns `HTTP 404`.

**GET /api/v1/deposits** — example response:

```json
[
  {
    "depositId": 100,
    "accountId": 10,
    "dpfRefId": "DPF-2026-00001",
    "depositAmount": 50000.00,
    "currency": "USD",
    "status": "ACTIVE",
    "createdAt": "2026-05-14T11:00:00-05:00",
    "updatedAt": "2026-05-14T11:00:00-05:00"
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
  "timestamp": "2026-05-14T16:00:00Z",
  "fieldErrors": null
}
```

**POST /api/v1/deposits** — example request:

```json
{
  "accountId": 10,
  "dpfRefId": "DPF-2026-00001",
  "depositAmount": 50000.00,
  "currency": "USD",
  "status": "ACTIVE"
}
```

`HTTP 201 Created` — example response body:

```json
{
  "depositId": 100,
  "accountId": 10,
  "dpfRefId": "DPF-2026-00001",
  "depositAmount": 50000.00,
  "currency": "USD",
  "status": "ACTIVE",
  "createdAt": "2026-05-14T11:00:00-05:00",
  "updatedAt": "2026-05-14T11:00:00-05:00"
}
```

**POST /api/v1/deposits** — validation error response (`HTTP 400 Bad Request`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/deposits",
  "timestamp": "2026-05-14T16:00:00Z",
  "fieldErrors": {
    "accountId": "must not be null",
    "dpfRefId": "must not be blank",
    "depositAmount": "must be greater than 0",
    "currency": "must not be blank",
    "status": "must not be blank"
  }
}
```

### Validation Rules — `CreateDepositRequest`

| Field           | Constraints                        |
|-----------------|------------------------------------|
| `accountId`     | `@NotNull`, `@Positive`            |
| `dpfRefId`      | `@NotBlank`, `@Size(max = 100)`    |
| `depositAmount` | `@NotNull`, `@Positive`            |
| `currency`      | `@NotBlank`, `@Size(max = 3)`      |
| `status`        | `@NotBlank`, `@Size(max = 20)`     |

### Accounts

| Method | Endpoint                          | Description          |
|--------|-----------------------------------|----------------------|
| GET    | `/api/v1/accounts`                | List all accounts    |
| GET    | `/api/v1/accounts/{accountId}`    | Get account by ID    |
| POST   | `/api/v1/accounts`                | Create a new account |

Each account is linked to an existing `Customer` via `customerId`. Creating an account with a non-existent `customerId` returns `HTTP 404`.

**GET /api/v1/accounts** — example response:

```json
[
  {
    "accountId": 10,
    "customerId": 1,
    "accountNumber": "ACC-0001-2026",
    "accountType": "CHECKING",
    "currency": "USD",
    "status": "ACTIVE",
    "effectiveFrom": "2026-01-01",
    "effectiveTill": null,
    "createdAt": "2026-05-14T11:00:00-05:00",
    "updatedAt": "2026-05-14T11:00:00-05:00"
  }
]
```

**GET /api/v1/accounts/99** — not found response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Account not found with id: 99",
  "path": "/api/v1/accounts/99",
  "timestamp": "2026-05-14T16:00:00Z",
  "fieldErrors": null
}
```

**POST /api/v1/accounts** — example request:

```json
{
  "customerId": 1,
  "accountNumber": "ACC-0001-2026",
  "accountType": "CHECKING",
  "currency": "USD",
  "status": "ACTIVE",
  "effectiveFrom": "2026-01-01",
  "effectiveTill": null
}
```

`HTTP 201 Created` — example response body:

```json
{
  "accountId": 10,
  "customerId": 1,
  "accountNumber": "ACC-0001-2026",
  "accountType": "CHECKING",
  "currency": "USD",
  "status": "ACTIVE",
  "effectiveFrom": "2026-01-01",
  "effectiveTill": null,
  "createdAt": "2026-05-14T11:00:00-05:00",
  "updatedAt": "2026-05-14T11:00:00-05:00"
}
```

**POST /api/v1/accounts** — validation error response (`HTTP 400 Bad Request`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/accounts",
  "timestamp": "2026-05-14T16:00:00Z",
  "fieldErrors": {
    "customerId": "must not be null",
    "accountNumber": "must not be blank",
    "accountType": "must not be blank",
    "currency": "must not be blank",
    "status": "must not be blank",
    "effectiveFrom": "must not be null"
  }
}
```

### Validation Rules — `CreateAccountRequest`

| Field           | Constraints                        |
|-----------------|------------------------------------|
| `customerId`    | `@NotNull`, `@Positive`            |
| `accountNumber` | `@NotBlank`, `@Size(max = 30)`     |
| `accountType`   | `@NotBlank`, `@Size(max = 50)`     |
| `currency`      | `@NotBlank`, `@Size(max = 3)`      |
| `status`        | `@NotBlank`, `@Size(max = 20)`     |
| `effectiveFrom` | `@NotNull`                         |
| `effectiveTill` | optional, no constraint            |

---

### DepositSubAccounts

| Method | Endpoint                                              | Description                    |
|--------|-------------------------------------------------------|--------------------------------|
| GET    | `/api/v1/deposit-sub-accounts`                        | List all deposit sub-accounts  |
| GET    | `/api/v1/deposit-sub-accounts/{depositSubAccountId}`  | Get deposit sub-account by ID  |
| POST   | `/api/v1/deposit-sub-accounts`                        | Create a new deposit sub-account |

Each deposit sub-account is linked to an existing `Deposit` via `depositId`. Creating a deposit sub-account with a non-existent `depositId` returns `HTTP 404`.

**GET /api/v1/deposit-sub-accounts** — example response:

```json
[
  {
    "depositSubAccountId": 200,
    "depositId": 100,
    "partyName": "John Doe",
    "share": 50.00,
    "rate": 4.25,
    "createdAt": "2026-05-15T09:00:00-05:00",
    "updatedAt": "2026-05-15T09:00:00-05:00"
  }
]
```

**GET /api/v1/deposit-sub-accounts/99** — not found response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "DepositSubAccount not found with id: 99",
  "path": "/api/v1/deposit-sub-accounts/99",
  "timestamp": "2026-05-15T14:00:00Z",
  "fieldErrors": null
}
```

**POST /api/v1/deposit-sub-accounts** — example request:

```json
{
  "depositId": 100,
  "partyName": "John Doe",
  "share": 50.00,
  "rate": 4.25
}
```

`HTTP 201 Created` — example response body:

```json
{
  "depositSubAccountId": 200,
  "depositId": 100,
  "partyName": "John Doe",
  "share": 50.00,
  "rate": 4.25,
  "createdAt": "2026-05-15T09:00:00-05:00",
  "updatedAt": "2026-05-15T09:00:00-05:00"
}
```

**POST /api/v1/deposit-sub-accounts** — validation error response (`HTTP 400 Bad Request`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/deposit-sub-accounts",
  "timestamp": "2026-05-15T14:00:00Z",
  "fieldErrors": {
    "depositId": "must not be null",
    "partyName": "must not be blank",
    "share": "must not be null",
    "rate": "must not be null"
  }
}
```

### Validation Rules — `CreateDepositSubAccountRequest`

| Field       | Constraints                        |
|-------------|------------------------------------|
| `depositId` | `@NotNull`, `@Positive`            |
| `partyName` | `@NotBlank`, `@Size(max = 255)`    |
| `share`     | `@NotNull`, `@Positive`            |
| `rate`      | `@NotNull`, `@Positive`            |

---

### MarketRateVersions

| Method | Endpoint                                       | Description                      |
|--------|------------------------------------------------|----------------------------------|
| GET    | `/api/v1/market-rate-versions`                 | List all market rate versions    |
| GET    | `/api/v1/market-rate-versions/{rateVersionId}` | Get market rate version by ID    |
| POST   | `/api/v1/market-rate-versions`                 | Create a new market rate version |

Standalone entity — no parent validation required. `effectiveTill` is optional.

**GET /api/v1/market-rate-versions** — example response:

```json
[
  {
    "rateVersionId": 1,
    "baseRate": 4.00,
    "spread": 0.25,
    "allInRate": 4.25,
    "effectiveFrom": "2026-01-01",
    "effectiveTill": null,
    "createdAt": "2026-05-15T09:00:00-05:00",
    "updatedAt": "2026-05-15T09:00:00-05:00"
  }
]
```

**GET /api/v1/market-rate-versions/99** — not found response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "MarketRateVersion not found with id: 99",
  "path": "/api/v1/market-rate-versions/99",
  "timestamp": "2026-05-15T14:00:00Z",
  "fieldErrors": null
}
```

**POST /api/v1/market-rate-versions** — example request:

```json
{
  "baseRate": 4.00,
  "spread": 0.25,
  "allInRate": 4.25,
  "effectiveFrom": "2026-01-01",
  "effectiveTill": null
}
```

`HTTP 201 Created` — example response body:

```json
{
  "rateVersionId": 1,
  "baseRate": 4.00,
  "spread": 0.25,
  "allInRate": 4.25,
  "effectiveFrom": "2026-01-01",
  "effectiveTill": null,
  "createdAt": "2026-05-15T09:00:00-05:00",
  "updatedAt": "2026-05-15T09:00:00-05:00"
}
```

**POST /api/v1/market-rate-versions** — validation error response (`HTTP 400 Bad Request`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/market-rate-versions",
  "timestamp": "2026-05-15T14:00:00Z",
  "fieldErrors": {
    "baseRate": "must not be null",
    "spread": "must be greater than 0",
    "allInRate": "must not be null",
    "effectiveFrom": "must not be null"
  }
}
```

### Validation Rules — `CreateMarketRateVersionRequest`

| Field           | Constraints             |
|-----------------|-------------------------|
| `baseRate`      | `@NotNull`, `@Positive` |
| `spread`        | `@NotNull`, `@Positive` |
| `allInRate`     | `@NotNull`, `@Positive` |
| `effectiveFrom` | `@NotNull`              |
| `effectiveTill` | optional, no constraint |

---

### DepositRates

| Method | Endpoint                               | Description              |
|--------|----------------------------------------|--------------------------|
| GET    | `/api/v1/deposit-rates`                | List all deposit rates   |
| GET    | `/api/v1/deposit-rates/{depositRateId}`| Get deposit rate by ID   |
| POST   | `/api/v1/deposit-rates`                | Create a new deposit rate|

Each deposit rate links an existing `Deposit` and an existing `MarketRateVersion`. Creating a deposit rate with a non-existent `depositId` or `rateVersionId` returns `HTTP 404`.

**GET /api/v1/deposit-rates** — example response:

```json
[
  {
    "depositRateId": 500,
    "depositId": 100,
    "rateVersionId": 1,
    "allInRate": 4.25,
    "status": "ACTIVE",
    "createdAt": "2026-05-15T09:00:00-05:00",
    "updatedAt": "2026-05-15T09:00:00-05:00"
  }
]
```

**GET /api/v1/deposit-rates/99** — not found response:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "DepositRate not found with id: 99",
  "path": "/api/v1/deposit-rates/99",
  "timestamp": "2026-05-15T14:00:00Z",
  "fieldErrors": null
}
```

**POST /api/v1/deposit-rates** — example request:

```json
{
  "depositId": 100,
  "rateVersionId": 1,
  "allInRate": 4.25,
  "status": "ACTIVE"
}
```

`HTTP 201 Created` — example response body:

```json
{
  "depositRateId": 500,
  "depositId": 100,
  "rateVersionId": 1,
  "allInRate": 4.25,
  "status": "ACTIVE",
  "createdAt": "2026-05-15T09:00:00-05:00",
  "updatedAt": "2026-05-15T09:00:00-05:00"
}
```

**POST /api/v1/deposit-rates** — validation error response (`HTTP 400 Bad Request`):

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/deposit-rates",
  "timestamp": "2026-05-15T14:00:00Z",
  "fieldErrors": {
    "depositId": "must not be null",
    "rateVersionId": "must not be null",
    "allInRate": "must be greater than 0",
    "status": "must not be blank"
  }
}
```

### Validation Rules — `CreateDepositRateRequest`

| Field           | Constraints                    |
|-----------------|--------------------------------|
| `depositId`     | `@NotNull`, `@Positive`        |
| `rateVersionId` | `@NotNull`, `@Positive`        |
| `allInRate`     | `@NotNull`, `@Positive`        |
| `status`        | `@NotBlank`, `@Size(max = 20)` |

---

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

## API Documentation

Swagger UI and the raw OpenAPI specification are available when the application is running:

| Resource        | URL                                          |
|-----------------|----------------------------------------------|
| Swagger UI      | http://localhost:8080/swagger-ui/index.html  |
| OpenAPI JSON    | http://localhost:8080/v3/api-docs            |

Actuator endpoints are excluded from the generated spec (`paths-to-exclude: /actuator/**`).

### OpenAPI metadata

Configured in `OpenApiConfig.java`:

- **Title:** Liquidity Service API
- **Description:** Citizens Banking Liquidity Microservice APIs
- **Version:** v1

### Annotation strategy

| Annotation      | Scope            | Purpose                                          |
|-----------------|------------------|--------------------------------------------------|
| `@Tag`          | Controller class | Groups all endpoints under a named API section   |
| `@Operation`    | Endpoint method  | Provides summary and description per operation   |
| `@ApiResponses` | Endpoint method  | Documents expected HTTP response codes           |
| `@Schema`       | DTO field        | Describes field type, example value, and purpose |

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
- Request DTOs use `@Value @Builder @Jacksonized` for immutable JSON deserialization
- Error responses follow a standardized `ApiErrorResponse` structure
- All endpoints are documented with `@Tag`, `@Operation`, and `@ApiResponses` — Swagger UI is the primary API reference
- DTO fields exposed in the API are annotated with `@Schema` (description + example)
- Validation is applied at the controller layer using `@Valid` on `@RequestBody` parameters
- Validation annotations (`@NotNull`, `@NotBlank`, `@Positive`) are declared on request DTOs only, never on entities
- Validation errors are handled globally in `GlobalExceptionHandler` and returned as a structured `fieldErrors` map
- Monetary amounts use `BigDecimal` (never `double` or `float`)
- Dates use `LocalDate` (never `Date` or `Calendar`)
- Constants are centralized in `util/DepositConstants`
- All service methods are annotated with `@Transactional(readOnly = true)`
