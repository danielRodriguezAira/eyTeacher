---
apply: always
---

---
apply: always
---

## Stack

Java 21
Spring Boot 4.0.2
Maven 3.9.11
Mysql 12
Spring Data JPA
MapStruct 1.6.3
Lombok
JUnit 5
Testcontainers (for integration tests)
Flyway (database migrations)
Docker Compose

## Project Structure

src/
  main/
    ├── java/es/leinadfonfria/eyteacher/
    │   ├── application
    │   │   ├── dtos
    │   │   └── services        # use case interfaces
    │   ├── domain
    │   │   ├── entities        # domain entities (NO JPA annotations)
    │   │   ├── valueobjects
    │   │   ├── events
    │   │   └── errors
    │   ├── infrastructure
    │   │   ├── controllers     # REST controllers (Spring MVC/WebFlux)
    │   │   ├── services        # application service implementations
    │   │   └── persistence
    │   │       ├── entities    # JPA entities (database mapping)
    │   │       └── repositories # Spring Data JPA repositories
    │   └── shared              # utilities, cross-cutting concerns
    └── resources
        └── db/migration # migration sql scripts
  ├──test/
    └── [same structure as src/main]

## Principles

- Clean Architecture (dependencies inward: infrastructure → application → domain)
- Test Driven Development (strict TDD)
- If any request conflicts with Clean Architecture or TDD:
    - FIRST: briefly explain the conflict **IN SPANISH**
    - THEN: propose an aligned alternative **IN SPANISH**
    - Code and comments must ALWAYS be generated in English

## Coding Rules

- NO business logic in controllers, repositories, or JPA entities
- Business logic ONLY in domain layer (entities, value objects, domain services) and application services
- Application services:
    - orchestrate use cases
    - coordinate repositories
    - publish domain events
- Use MapStruct for:
    - domain entities/value objects ↔ DTOs mapping
    - domain entities ↔ persistence entities mapping
- Error handling:
    - define domain-specific errors in `domain/errors`
    - return API errors in controllers using global handler (RestControllerAdvice)
- Lombok: avoid in domain entities if it limits immutability/invariants; prioritize explicit constructors and factory methods
- Tests:
    - unit tests FIRST for domain and application
    - integration tests AFTER for controllers/persistence with JUnit 5 + Testcontainers
- Configuration:
    - NO business logic in @Configuration or @Bean classes
    - keep Spring configuration isolated in `infrastructure`
- Entity relations:
    - always define relations by entity, not by entity id 

MANDATORY: Javadoc on all public classes/methods per Documentation Rules above.
IF JAVADOC IS MISSING → DO NOT generate code, notify me first.

### Example Expected Output

✅ GOOD:
/**
    Retrieves all categories for current teacher.
    @return List<CategoryResponse> with id, name, color
*/
List<CategoryResponse> findAll();

❌ BAD:
public List<CategoryResponse> findAll() { ... } // NO JAVADOC

## Documentation Rules **(MANDATORY)**

**GENERATE JAVA DOC EVERYWHERE REQUIRED BY CONVENTION**

### 5. NO Javadoc required
- Private methods
- Simple getters/setters
- Trivial utility classes
- JPA annotations (@Entity, @Id, etc.)

## Javadoc Template
/**
    [What it does - 1 line]
    [When to use / context]
    [Important parameters]
    @param request [concrete description]
    [Returns]
    @return [type] [exactly what it contains]
    [Possible exceptions]
    @throws [ExceptionClass] [when it occurs]
*/

## AI Generation Rules

    ALWAYS generate Javadoc before every public class/method
    Use imperative verbs: "Creates", "Validates", "Retrieves"
    @param tag for ALL non-obvious parameters
    @return tag explaining EXACTLY what data it contains
    @throws tag for domain-specific exceptions
    Controllers: document HTTP status codes

## AI Expectations

**RESPONSES IN SPANISH, CODE IN ENGLISH**

- Before generating use case code:
    1. Define required interfaces in `application/services`
    2. Define relevant entities/value objects in `domain`
    3. ONLY THEN generate infrastructure adapters (controllers, repositories, mappers)
- If functional information is missing:
    - propose 2-3 reasonable options **IN SPANISH**
    - state your chosen assumption **IN SPANISH** before generating code
- Class naming:
    - Services: `CreateCategoryUseCase`, `FindStudentsUseCase`
    - DTOs: `CategoryRequest`, `StudentResponse`
    - Domain: `Category`, `StudentId` (no suffixes)
    - Persistence: `CategoryJpaEntity`, `StudentRepository`
- REST endpoints: `/api/v1/categories`, `/api/v1/students` (no nesting)
