# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

eyTeacher is a full-stack web application for teacher-student communication focused on micro-learning. It has three modules: a Spring Boot backend, an Angular frontend, and a Spring AI service (Ollama/Qwen).

## Monorepo Structure

```
eyTeacher/
├── backend/       # Java 21 + Spring Boot 4.0.2 REST API
├── frontend/      # Angular 21 standalone components
├── ai/         # Spring Boot 4.0.4 + Spring AI 2.0.0-M3 (Ollama)
└── compose.yaml   # Docker orchestration (root)
```

## Commands

### Backend (run from `backend/`)
```bash
./mvnw spring-boot:run          # Dev server on :8080
./mvnw compile                  # Lint/type-check
./mvnw test                     # Unit tests (H2 in-memory)
./mvnw integration-test         # Integration tests (Testcontainers)
./mvnw test -Dtest=ClassName    # Single test class
./mvnw test -Dtest=Class#method # Single test method
./mvnw clean package            # Build JAR
```

### Frontend (run from `frontend/`)
```bash
npm start                                    # Dev server on :4200
npm run lint                                 # Lint
npm test                                     # All tests (Vitest)
ng test --include=path/to/test.spec.ts       # Single test file
npm run build                                # Production build
```

### Docker (full stack, run from `eyTeacher/`)
```bash
# Start
docker compose --env-file .env -f traefik/compose.yaml --profile production up --build -d \
&& docker compose --env-file .env -f backend/compose.yaml --profile production up --build -d \
&& docker compose --env-file .env -f frontend/compose.yaml --profile production up --build -d \
&& cd ai && docker compose up --build -d && cd ..

# Stop
docker compose -f traefik/compose.yaml --profile production stop \
&& docker compose -f backend/compose.yaml --profile production stop \
&& docker compose -f frontend/compose.yaml --profile production stop \
&& cd ai && docker compose stop && cd ..
```
Requires an external Docker network (`docker network create eyteacher-network`) and a `.env` file at the project root.

## Architecture

### Clean / Hexagonal Architecture (both backend and frontend)

Dependencies flow strictly **inward**: `infrastructure → application → domain`.

**Backend layers** (package `es.leinadfonfria.eyteacher`):

| Layer | Package | Rules |
|---|---|---|
| Domain | `domain/` | Pure Java, NO JPA/Spring annotations. Entities, value objects (Java Records), ports (interfaces), errors. |
| Application | `application/` | Use-case services, DTOs (`*Request`/`*Response`). Orchestrates domain, coordinates repositories, publishes events. No framework code. |
| Infrastructure | `infrastructure/` | Controllers, JPA entities (`*JpaEntity`), Spring Data repositories, MapStruct adapters, security, event publishing. |
| Shared | `shared/` | Cross-cutting utilities. |

**Frontend mirrors the same structure** under `src/app/`: `domain/`, `application/`, `infrastructure/features/`, `shared/`.

### Key patterns
- **Value objects**: Immutable Java Records with validation in constructor (e.g. `UserId`, `Email`, `Name`).
- **Ports & Adapters**: Domain defines interfaces (ports); infrastructure implements them (adapters in `persistence/adapter/`).
- **MapStruct**: Used for all mapping between domain entities ↔ JPA entities ↔ DTOs.
- **RepositoryAdapters**: `find*` methods always return `Optional`.
- **Entity relations**: Always define by entity reference, never by ID.
- **REST endpoints**: `/api/v1/<resource>` (flat, no nesting).
- **Events**: RabbitMQ via Spring AMQP, published from application services.

### Naming conventions (backend)
- Services/use cases: `CreateCategoryUseCase`, `FindStudentsUseCase`
- DTOs: `CategoryRequest`, `StudentResponse`
- Domain: `Category`, `StudentId` (no technical suffixes)
- Persistence: `CategoryJpaEntity`, `CategoryRepository`

### Naming conventions (frontend)
- Component folders/files: `kebab-case` (e.g. `user-profile/user-profile.ts`)
- Classes/types: PascalCase; variables/methods: camelCase
- Screens go under `infrastructure/features/<feature-name>/`; reusable UI under `shared/components/`

## Mandatory Rules

- **Javadoc is required** on all public Java classes and methods. Imperative verbs: "Creates", "Validates", "Retrieves". Include `@param`, `@return`, `@throws`. Controllers must document HTTP status codes. **If Javadoc is missing in existing code, do not generate new code — notify the user first.**
- **No business logic** in controllers, JPA entities, or `@Configuration` classes.
- **Lombok**: avoid in domain entities where it would limit immutability or invariants; use explicit constructors and factory methods there.
- **Angular**: use standalone components (Angular 21 style); no business logic in presentation components; use reactive forms consistently.
- **Responses to the user in Spanish; all code (identifiers, comments, UI text) in English.**

## Infrastructure & Config

- **Database**: MySQL 8.4 (Docker) / H2 in test (`application-test.properties`). Migrations via Flyway in `src/main/resources/db/migration/`.
- **Auth**: JWT (JJWT 0.12.6, HS512). Roles: `ROLE_TEACHER`, `ROLE_STUDENT`.
- **AI service**: Ollama on `:11434`, Qwen 3.5 9B model, exposed on `:8081`.
- **OpenAPI/Swagger**: available at `http://localhost:8080/swagger-ui.html` when running locally.
