# Cafe POS System Documentation

Welcome to the technical documentation for the Cafe POS application. This project is a modern Point of Sale system built with **Spring Boot 3** (Backend) and **Nuxt 4** (Frontend).

## Documentation Sections

### Architecture

- [System Overview](./architecture/system-overview.md): High-level architecture and technology stack.
- [Database Schema](./architecture/database-schema.md): ER diagrams and data models.

### Frontend (Nuxt 4)

- [Setup & Installation](./frontend/setup.md): How to run the Nuxt frontend.
- [Directory Structure](./frontend/directory-structure.md): Understanding the `app/` folder organization.
- [Theming & UI](./frontend/theming.md): TailwindCSS design system and component usage.

### Backend (Spring Boot 3)

- [Setup & Installation](./backend/setup.md): How to run the Spring Boot API.
- [API Standards](./backend/api-standards.md): Response wrappers, error handling, and guidelines.
- [DTO Flow](./backend/dto-flow.md): Data Transfer Object patterns (referencing internal DTO docs).

### Deployment & DevOps

- [Docker Guide](./deployment/docker-guide.md): Running with Docker Compose and container management.

## Quick Start

### Prerequisites

- **Docker & Docker Compose** (Required for Database)
- **Node.js 22+** (For native frontend development)
- **JDK 21+** (For native backend development)

### The Recommended Workflow (Hybrid)

1. **Infrastructure**: In the root directory, run `docker-compose up -d postgres adminer`.
2. **Backend**: `cd backend && ./mvnw spring-boot:run`.
3. **Frontend**: `cd frontend && npm run dev`.

### Entire Stack via Docker

```bash
docker-compose up -d --build
```

Access the app at:

- **Frontend**: `http://localhost:3000`
- **Backend API**: `http://localhost:8081`
- **Database Adminer**: `http://localhost:8080` (Server: `postgres`, DB: `vuespringdb`)

---

## Configuration

Configuration is now centralized in the **root `.env` file**. Refer to the [Docker Guide](./deployment/docker-guide.md) for more details.
