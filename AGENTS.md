# AGENTS.md

## Project Overview
This project is a full-stack web application for communication between teachers and students focused on micro-learning.
### Backend
- **Project**: eyTeacher
- **Framework**: Spring Boot 4.0.2
- **Language**: Java 21
- **Build Tool**: Maven 3.9.11
- **Database**: MySQL
- **Key Libraries**: 
  - Spring Data JPA, MapStruct 1.6.3, Lombok
  - JUnit 5, Testcontainers, Flyway
  - Spring Security (password encoder with BCrypt, stateless JWT authentication)
  - Thymeleaf, WebFlux, JWT
- **Architecture**: Clean Architecture with layers: Domain, Application, Infrastructure, Persistence
- **REST API Structure**:
  - `/api/v1/auth/login` - Login endpoint with JWT
  - `/api/v1/auth/register` - User registration
  - `/api/v1/categories` - Category CRUD operations
  - `/api/v1/students` - Student management
- **Error Handling**: Domain-error classes → @RestControllerAdvice
- **Project Location**: `/run/media/dani/Data/eyTeacher`

### Frontend
- **Framework**: Angular 21 (standalone components)
- **Language**: TypeScript ES2022
- **Build Tool**: npm/nx
- **SCSS**: Yes
- **Key Features**: Categories, Students, Courses modules
- **Architecture**:
  - Layered: domain (entities, value objects, errors, events), application (use cases, dtos, services), infrastructure (features, services)
  - Shared design system (components, directives, pipes, layout, styles, utils) It consists of a backend built with Java Spring Boot and a frontend built with Angular.

## Build Commands
- Backend (Maven): `./mvnw clean install` or `./mvnw package`
- Frontend (Angular): `npm run build` 
- Run frontend dev server: `npm start`
- Run backend dev server: `./mvnw spring-boot:run`

## Lint Commands
- Backend: `./mvnw compile` (uses Maven compiler plugin with Java 21)
- Frontend: `npm run lint` (uses Angular CLI)

## Test Commands
- Backend unit tests: `./mvnw test` 
- Backend integration tests: `./mvnw integration-test`
- Run a single test class: `./mvnw test -Dtest=TestClass`
- Run a single test method: `./mvnw test -Dtest=TestClass#testMethod`
- Frontend tests: `npm test` (runs all tests)
- Frontend single test file: `ng test --include=path/to/test.spec.ts`

## Code Style Guidelines

### Java Backend (Spring Boot)
- **Stack**
  - Java 21
  - Spring Boot 4.0.2
  - Maven 3.9.11
  - Mysql 12
  - Spring Data JPA
  - MapStruct 1.6.3
  - Lombok
  - JUnit 5
  - Testcontainers (for integration tests)
  - Flyway (database migrations)
  - Docker Compose
- **Naming Conventions**: 
  - Services: `CreateCategoryUseCase`, `FindStudentsUseCase`
  - DTOs: `CategoryRequest`, `StudentResponse`
  - Domain: `Category`, `StudentId` (no suffixes)
  - Persistence: `CategoryJpaEntity`, `StudentRepository`
- **Imports**: Sorted alphabetically with static imports grouped separately
- **Formatting**: Follow Spring Boot conventions with 4-space indentation
- **Types**: Use explicit types; avoid `var` except in local variables
- **Error Handling**: Define domain-specific errors in `domain/errors` and handle with global `@RestControllerAdvice`
- **Documentation**: All public classes and methods must have Javadoc
- **Architecture**: Clean Architecture principle with dependencies flowing inward (infrastructure → application → domain)
- **Testing**: Strict TDD approach - unit tests first, then integration tests

- **Project Structure**
  src/
    main/
      ├── java/es/leinadfonfria/eyteacher/
      │   ├── application
      │   │   ├── dtos
      │   │   └── services        # use case clases
      │   ├── domain
      │   │   ├── entities        # domain entities (NO JPA annotations)
      │   │   ├── valueobjects
      │   │   ├── events
      │   │   ├── ports           # domain interfaces for repositories
      │   │   └── errors
      │   ├── infrastructure
      │   │   ├── controllers     # REST controllers (Spring MVC/WebFlux)
      │   │   ├── services        # application service implementations
      │   │   └── persistence
      │   │       ├── entities    # JPA entities (database mapping)
      │   │       ├── adapter     # Spring Compoment. Implementation for domain ports repositories. Use JPA repositories and Mapstruct mappers
      │   │       └── repositories # Spring Data JPA repositories
      │   └── shared              # utilities, cross-cutting concerns
      └── resources
          └── db/migration # migration sql scripts
    ├──test/
      └── [same structure as src/main]

- **Coding Rules**
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
  - RepositoryAdapters: In find methods, always return Optional
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

- **AI Generation Rules**
  - ALWAYS generate Javadoc before every public class/method
  - Use imperative verbs: "Creates", "Validates", "Retrieves"
  - @param tag for ALL non-obvious parameters
  - @return tag explaining EXACTLY what data it contains
  - @throws tag for domain-specific exceptions
  - Controllers: document HTTP status codes

- **AI Expectations**
  - RESPONSES IN SPANISH, CODE IN ENGLISH
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


### Angular Frontend 
- **Naming Conventions**: 
  - Components: `category-list`, `student-form`
  - Services: `category-service`, `student-service`
  - Pipes: `category-display`, `student-id`
- **Imports**: Follow Angular style guide conventions
- **Formatting**: Use Prettier with 100 character print width, single quotes
- **Types**: TypeScript strict mode enabled
- **Error Handling**: Angular's built-in error handling patterns
- **Testing**: Component testing with Jasmine, Karma runner

- **Stack:**
  - Node 22
  - TypeScript ES2022
  - Angular 21 (standalone)
  - SCSS
  - Docker Compose.  

- **Goal:**
  - Enforce visual consistency and clean architecture in all new screens.

- **Global rules**
  - Use English for code, names, comments, and UI text.
  - Respect project structure and naming rules below.
  - Prefer reusing existing patterns (layout, components, styles) over creating new ones.
  - Optimize for readability, maintainability, and strict typing.

 - **Project structure**
  src/app
  ├── application        // Orchestration, use case services
  │   ├── dtos
  │   └── services
  ├── domain             // Business rules, pure TS
  │   ├── entities
  │   ├── errors
  │   ├── events
  │   └── value_objects
  ├── infrastructure     // Angular features, adapters
  │   ├── features       // Screens / feature flows
  │   └── services       // Http, storage, adapters
  └── shared             // Design system & cross-cutting
      ├── components
      ├── directives
      ├── pipes
      ├── layout
      ├── styles
      └── utils
  test

  - Place screens under `infrastructure/features/<feature-name>`.
  - Place reusable UI under `shared/components`.
  - Place global styles, tokens, mixins under `shared/styles`.

- **Naming conventions**
  - Everything in English.
  - Classes/interfaces/types: PascalCase.
  - Variables/functions/methods: camelCase.
  - Folders and files: kebab-case.
  - Component folders: `user-profile`; files: `user-profile.ts`, `user-profile.html`, `user-profile.scss`.
  - Logical names for components do not include `.component` in descriptions, but keep Angular CLI file suffixes.

 - **Visual consistency rules**
  - When creating a new screen or component, copy and adapt existing patterns instead of inventing new ones.
  1. Styles and tokens
      - Use existing SCSS variables, mixins, and utilities from `shared/styles` for colors, typography, spacing, radius, and shadows.
      - If a new token is required, follow existing naming patterns and keep values aligned with the current design.
  2. Screen layout
      - Use a standard page layout: main container (max-width + centered), header with page title and primary actions, then content sections (filters, lists, forms).
      - For similar use cases, clone the HTML/SCSS structure of an existing screen and only adjust bindings and text.
  3. Reusable components
      - Before creating buttons, cards, modals, tables, or form controls, search in `shared/components`.
      - If a small variation is needed, extend the existing component with typed inputs/outputs instead of duplicating markup.
  4. Typography and hierarchy
      - Reuse the same classes/utilities for headings, subtitles, body text, and labels.
      - Keep semantic order: `h1` for page title, `h2` for sections, etc.
  5. Forms
      - Use reactive forms consistently.
      - Reuse shared form-field components (input, select, date, etc.).
      - Show validation errors using the same structure and styles as existing forms.
  6. Lists and tables
      - Prefer a shared table/list component if available.
      - Reuse the same patterns for pagination, filters, and row actions.
 
- **Angular rules**
  - Use standalone components according to Angular 21 conventions.
  - Presentation components:
      - No business logic.
      - Use typed `@Input` / `@Output`.
      - Do not call HTTP services directly.
  - Application/infrastructure services:
      - Orchestrate use cases, call domain, map DTOs, and call HTTP adapters.
  - Avoid complex logic in templates; move it to TypeScript.
  - Avoid circular dependencies; import only what is required.

- **TypeScript rules**
  - Use strict typing; avoid `any` unless absolutely necessary.
  - Prefer `type`/`interface` for data structures.
  - Keep functions/methods short and single-responsibility.
  - Use descriptive names; avoid abbreviations except well-known ones.
  - Extract shared logic into `shared/utils` when reused in multiple places.

- **Layer interaction**
  - Domain: pure TypeScript, no Angular imports, no UI or infrastructure concerns.
  - Application: use-case services that coordinate domain and infrastructure; components call these services instead of raw HTTP services.
  - Infrastructure: concrete adapters (HTTP, storage, etc.) implementing application contracts.

- **Changes generated by the assistant**
  - Group changes by cohesive feature or screen.
  - Always follow existing naming, folder placement, visual patterns, and SCSS structure.
  - Only add comments when they add meaningful context; avoid redundant comments.