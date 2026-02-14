---
apply: always
---

## Stack

Java 21 + Spring Boot 4.0.2 + Maven 3.9.11 + Spring Data Jpa + Mapstruct 1.6.3 + Loombok + Junit 5 + Docker compose

## Project Structure
````
src/
main/java/es/leinadfonfria/eyteacher/
├── application
    ├── dtos
    ├── services [interfaces]
├── domain
    ├── entities
    ├── errors
    ├── events
    └── valueobjects
├── infrastructure
    ├── controllers [REST Controllers]
    ├── services [implementations]
    └── persistence
        ├── entities [JPA Entities]
        └── repositories [JPA Repositories]
└── shared
test

````
