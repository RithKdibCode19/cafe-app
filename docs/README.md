# Cafe POS System Documentation

Welcome to the technical documentation for the Cafe POS application. This project is a modern Point of Sale system built with **Spring Boot 3** (Backend) and **Nuxt 4** (Frontend).

## Documentation Sections

### Architecture
* [System Overview](./architecture/system-overview.md): High-level architecture and technology stack.
* [Database Schema](./architecture/database-schema.md): ER diagrams and data models.

### Frontend (Nuxt 4)
* [Setup & Installation](./frontend/setup.md): How to run the Nuxt frontend.
* [Directory Structure](./frontend/directory-structure.md): Understanding the `app/` folder organization.
* [Theming & UI](./frontend/theming.md): TailwindCSS design system and component usage.

### Backend (Spring Boot 3)
* [Setup & Installation](./backend/setup.md): How to run the Spring Boot API.
* [API Standards](./backend/api-standards.md): Response wrappers, error handling, and guidelines.
* [DTO Flow](./backend/dto-flow.md): Data Transfer Object patterns (referencing internal DTO docs).

### Deployment & DevOps
* [Docker Guide](./deployment/docker-guide.md): Running with Docker Compose and container management.

## Quick Start

### Prerequisites
* Node.js 22+
* JDK 21+
* Docker & Docker Compose

### Running Locally
1. **Start Backend**: `cd backend && ./mvnw spring-boot:run`
2. **Start Frontend**: `cd frontend && npm run dev`

### Running with Docker
```bash
docker-compose up --build
```

Access the app at:
* Frontend: `http://localhost:3000`
* Backend API: `http://localhost:8081`
* Database Adminer: `http://localhost:8080`


docker-compose up -d postgres backend

cd frontend
npm install
npm run dev