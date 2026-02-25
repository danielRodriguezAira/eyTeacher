---
apply: always
---

## Stack

Node 22 + TypeScript ES2022 + Angular 21 + SCSS + Docker compose

## Project Structure
````
src/app
├── application
    ├── dtos
    ├── services
├── domain
    ├── entities
    ├── errors
    ├── events
    └── value_objects
├── infrastructure
    ├── features
    └── services
└── shared
test
````

## Naming conventions

Always English words.
PascalCase for clases/interfaces.
Kebab-case for folders and files.
Components without '.component' suffix.
