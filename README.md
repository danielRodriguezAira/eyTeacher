# eyTeacher

Aplicación web full-stack para la comunicación entre profesores y alumnos, orientada al micro-aprendizaje. El profesor crea categorías, temas y tareas; los alumnos las resuelven enviando soluciones; el profesor corrige y el sistema notifica a ambas partes en tiempo real. Un servicio de IA asistente genera enunciados de ejercicios y proporciona pistas contextuales sin revelar la solución.

---

## Presentación del proyecto

/docs/Presentación EyTeacher.odp

---

## URL Publicación 

[EyTeacher](https://eyteacher.dpdns.org/)

---

## Stack tecnológico

### Backend (`backend/`)
| Componente | Tecnología |
|---|---|
| Lenguaje | Java 21 |
| Framework | Spring Boot 4.0.2 |
| Seguridad | Spring Security + JWT (JJWT 0.12.6, HS512) |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8.4 |
| Migraciones | Flyway |
| Mensajería | RabbitMQ (Spring AMQP) |
| Mapeo | MapStruct |
| Documentación API | SpringDoc OpenAPI / Swagger UI |
| Tests unitarios | JUnit 5 + Mockito |
| Tests integración | Testcontainers |

### Frontend (`frontend/`)
| Componente | Tecnología |
|---|---|
| Framework | Angular 21 (standalone components) |
| UI | Angular Material 21 |
| Editor de texto enriquecido | Quill 2 |
| Reactividad | RxJS 7.8 |
| Tests | Vitest |

### Servicio IA (`ai/`)
| Componente | Tecnología |
|---|---|
| Framework | Spring Boot 4.0.4 |
| IA | Spring AI 2.0.0-M3 + OpenAI API (gpt-4o-mini) |
| Seguridad | Spring Security OAuth2 Resource Server (JWT) |

### Infraestructura
| Componente | Tecnología |
|---|---|
| Contenedores | Docker + Docker Compose |
| Broker de mensajes | RabbitMQ 3 |

---

## Instalación y ejecución

### Prerrequisitos
- Docker y Docker Compose instalados
- Red Docker externa creada:
  ```bash
  docker network create eyteacher-network
  ```
- Fichero `.env` en la raíz del proyecto con las siguientes variables:
  ```env
  JWT_SECRET=<secreto_jwt_mínimo_64_chars>
  MYSQL_USER=eyteacher
  MYSQL_PASSWORD=<contraseña>
  MYSQL_ROOT_PASSWORD=<contraseña_root>
  RABBITMQ_USER=eyteacher
  RABBITMQ_PASSWORD=<contraseña>
  OPENAI_API_KEY=<api_key_openai>
  DOCKER_PLATFORM=linux/arm64
  ```

### Ejecución en producción (stack completo)
```bash
# Arrancar
docker compose --env-file .env -f backend/compose.yaml --profile production up --build -d \
&& docker compose --env-file .env -f frontend/compose.yaml --profile production up --build -d \
&& cd ai && docker compose --env-file ../.env up --build -d && cd ..

# Parar
docker compose -f backend/compose.yaml --profile production stop \
&& docker compose -f frontend/compose.yaml --profile production stop \
&& cd ai && docker compose stop && cd ..
```

El túnel de Cloudflare (`cloudflared tunnel --url http://localhost:8082`) actúa como punto de entrada único. El nginx del frontend enruta `/api/` al backend y `/ai-api/` al servicio IA.

### Ejecución en desarrollo (módulos por separado)

**Backend** (puerto 8080):
```bash
cd backend
./mvnw spring-boot:run
```

**Frontend** (puerto 8082):
```bash
cd frontend
npm install
npm start
```

**Servicio IA** (puerto 8081):
```bash
cd ai
./mvnw spring-boot:run
```

### Comandos útiles

```bash
# Backend
./mvnw compile          # Compilar
./mvnw test             # Tests unitarios (H2 en memoria)
./mvnw integration-test # Tests de integración (Testcontainers)
./mvnw clean package    # Generar JAR

# Frontend
npm run lint            # Linting
npm test                # Tests
npm run build           # Build de producción
```

### URLs de desarrollo
| Servicio | URL |
|---|---|
| Frontend | http://localhost:8082 |
| Backend API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| Servicio IA | http://localhost:8081 |
| RabbitMQ Management | http://localhost:15672 |

---

## Estructura del proyecto

```
eyTeacher/
├── backend/                        # API REST Java/Spring Boot
│   └── src/main/java/es/leinadfonfria/eyteacher/
│       ├── domain/                 # Entidades, value objects, ports, errores
│       ├── application/            # Casos de uso (interfaces + DTOs)
│       ├── infrastructure/         # Controladores, JPA, seguridad, eventos
│       └── shared/                 # Utilidades transversales
│   └── src/main/resources/
│       └── db/migration/           # Scripts Flyway (V1–V10)
│
├── frontend/                       # SPA Angular
│   └── src/app/
│       ├── domain/                 # Entidades y value objects TypeScript
│       ├── application/            # Puertos de servicios (interfaces)
│       ├── infrastructure/
│       │   └── web/
│       │       ├── features/       # Pantallas por funcionalidad
│       │       └── services/       # Implementaciones HTTP de los puertos
│       └── shared/                 # Componentes y pipes reutilizables
│
├── ai/                             # Servicio de asistencia IA
│   └── src/main/java/              # Endpoints de generación de ejercicios y pistas
│
├── docs/                           # Documentación del proyecto (requisitos, visión)
├── CLAUDE.md                       # Guía de arquitectura para Claude Code
└── README.md
```

La arquitectura sigue el patrón **hexagonal / Clean Architecture** en ambos módulos (backend y frontend): las dependencias fluyen exclusivamente hacia adentro (`infrastructure → application → domain`), lo que mantiene el dominio libre de cualquier framework.

---

## Funcionalidades principales

### Gestión de contenido (rol Profesor)
- **Categorías**: agrupan temas por materia o asignatura.
- **Temas**: pertenecen a una categoría; el profesor puede suscribir alumnos por email.
- **Tareas**: asociadas a un tema, con descripciones en texto enriquecido (Quill). El servicio IA puede generar el enunciado automáticamente.

### Flujo de aprendizaje (rol Alumno)
- El alumno visualiza las tareas de los temas en los que está suscrito, agrupadas por estado (sin solución → sin corrección → corregida).
- Envía soluciones con descripción en texto enriquecido.
- Puede solicitar una **pista** al servicio de IA, que orienta sin revelar la solución.
- Una vez corregida la solución, puede enviar una nueva.

### Correcciones (rol Profesor)
- El profesor revisa las soluciones de sus alumnos y añade una corrección por escrito.

### Notificaciones en tiempo real
- Los eventos (nueva tarea, nueva solución, nueva corrección, nueva suscripción) se publican en **RabbitMQ** y se persisten como notificaciones para el destinatario.
- Las notificaciones muestran un indicador de no leídas y enlazan directamente al recurso relacionado.

### Paginación
- Las listas de tareas, soluciones, notificaciones y alumnos se cargan de 10 en 10 con un botón **"Ver más"** (cursor-based pagination).

### Autenticación y seguridad
- Registro y login con JWT (HS512).
- Roles `ROLE_TEACHER` y `ROLE_STUDENT` con acceso diferenciado a los endpoints.
- Actualización de perfil y cambio de contraseña.

### Servicio de IA
- **Generación de ejercicios**: a partir de un tema libre, genera un enunciado conciso y autocontenido.
- **Pistas contextuales**: dado el enunciado de la tarea, devuelve una pista pedagógica sin resolver el ejercicio.
