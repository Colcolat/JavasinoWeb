# System Architecture

This document describes the overall architecture of JavasinoWeb, including package structure, layer responsibilities, design decisions, and technology stack.

---

## Overview

JavasinoWeb is a monolithic Spring Boot REST API backed by PostgreSQL. The architecture is designed to be clean, modular, and easy to evolve — with each business domain organized as a self-contained feature module.

---

## Package structure

```
colcolat.javasinoweb
│
├── shared/
│   ├── config/              Spring Security, CORS, datasource config
│   ├── exceptions/          Global exception handler (@ControllerAdvice)
│   └── model/               Reusable non-entity classes (Card, Deck)
│
├── user/
│   ├── controller/          UserController — REST endpoints
│   ├── dto/                 UserDTO, RegisterRequest, LoginRequest
│   ├── model/               User entity
│   ├── repository/          UserRepository (JPA)
│   └── service/             UserService — business logic
│
├── wallet/
│   ├── controller/          WalletController
│   ├── dto/                 WalletDTO, TransactionDTO
│   ├── model/               Wallet, Transaction entities
│   ├── repository/          WalletRepository, TransactionRepository
│   └── service/             WalletService
│
└── games/
    ├── blackjack/
    │   ├── controller/      BlackjackController
    │   ├── dto/             BlackjackSessionDTO
    │   ├── model/           GameSession, BlackjackSession entities
    │   ├── repository/      GameSessionRepository, BlackjackSessionRepository
    │   └── service/         BlackjackService
    ├── roulette/
    │   └── ...              Same structure
    ├── bingo/
    │   └── ...              Same structure
    ├── horses/
    │   └── ...              Same structure
    └── poker/
        └── ...              Same structure (last phase)
```

---

## Architecture pattern: Package by Feature

The project uses **Package by Feature** instead of the conventional Package by Layer.

| Package by Layer | Package by Feature (chosen) |
|------------------|-----------------------------|
| All controllers together | Each feature owns its controller |
| All services together | Each feature owns its service |
| Hard to see what a module does | A module is self-contained and readable |
| Doesn't scale with team size | Each module can be owned by a different developer |

Inside each feature module, the classic layered pattern is preserved: `controller → service → repository → model`.

---

## Layer responsibilities

### Controller
- Receives HTTP requests
- Validates input (via `@Valid`)
- Delegates to the service layer
- Returns HTTP responses with appropriate status codes
- Never contains business logic

### Service
- Contains all business logic
- Orchestrates calls between repositories
- Integrates with WalletService for credit operations
- Throws custom exceptions handled globally

### Repository
- Extends `JpaRepository`
- Data access only — no logic
- Custom queries via `@Query` when needed

### Model (Entity)
- JPA entities mapped to PostgreSQL tables
- Annotated with `@Entity`, `@Table`, etc.
- No business logic in entities

### DTO (Data Transfer Object)
- Separates the API contract from the internal entity model
- Prevents accidental exposure of sensitive fields (e.g. password)
- Separate DTOs for request (input) and response (output)

---

## Technology stack

| Layer | Technology | Reason |
|-------|-----------|--------|
| Language | Java 21 | LTS version, modern features (Records, Pattern Matching, Streams) |
| Framework | Spring Boot | Industry standard for enterprise Java backends |
| API | Spring Web (MVC) | REST API with `@RestController` |
| Security | Spring Security + JWT | Stateless authentication |
| ORM | Spring Data JPA / Hibernate | Clean data access with PostgreSQL |
| Database | PostgreSQL | University stack + best AWS RDS support |
| Testing | JUnit 5 + Mockito | Unit and integration tests |
| Build | Gradle | Dependency management and builds |
| Documentation | OpenAPI / Swagger | Auto-generated API docs |

---

## Environment configuration

Spring Profiles are used to separate local development from production:

| File | Committed | Purpose |
|------|-----------|---------|
| `application.properties` | Yes | Shared base config |
| `application-dev.properties` | No (.gitignore) | Local PostgreSQL, verbose logging, `create-drop` DDL |
| `application-prod.properties` | No (.gitignore) | Environment variables only, `validate` DDL, minimal logging |
| `application-dev.properties.example` | Yes | Template for new developers |

**Credentials are never committed to Git.** Production credentials are passed as environment variables (`DB_URL`, `DB_USER`, `DB_PASSWORD`).

---

## Request lifecycle

```
HTTP Request
    │
    ▼
@RestController (input validation)
    │
    ▼
@Service (business logic + wallet integration)
    │
    ▼
@Repository (data access via JPA)
    │
    ▼
PostgreSQL
    │
    ▼
HTTP Response (DTO)
```

---

## Shared components

### GlobalExceptionHandler (`shared/exceptions/`)
A single `@ControllerAdvice` class catches all custom exceptions thrown by any service and returns consistent JSON error responses. No try/catch blocks in controllers.

### Card & Deck (`shared/model/`)
Reusable Java classes (not JPA entities) used by Blackjack and Poker. Encapsulate card representation and deck shuffling logic. Not persisted — instantiated in memory per game session.

---

## Future considerations

- **AWS deployment:** The current architecture (Package by Feature, Spring Profiles, PostgreSQL) is designed to migrate cleanly to AWS (EC2 + RDS) with only configuration changes — no structural refactoring needed.
- **Microservices:** Each feature module is self-contained and could be extracted into its own service if the project ever needs to scale horizontally. This is not planned, but the structure does not prevent it.
- **Docker:** Containerization is planned for the deployment phase to standardize the runtime environment between dev and prod.
