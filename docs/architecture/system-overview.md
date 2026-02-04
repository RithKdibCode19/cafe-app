# System Architecture

## Technology Stack

### Frontend
* **Framework**: [Nuxt 4](https://nuxt.com) (Vue 3)
* **Language**: TypeScript
* **Styling**: TailwindCSS
* **Icons**: Lucide (via SVGs)
* **State Management**: Built-in Nuxt/Vue composables

### Backend
* **Framework**: Spring Boot 3
* **Language**: Java 21/25
* **Database**: PostgreSQL 16
* **Build Tool**: Maven

### Infrastructure
* **Containerization**: Docker & Docker Compose
* **Orchestration**: Docker Compose (Local/Dev)

## High-Level Diagram

```mermaid
graph TD
    Client[Browser / Client]
    
    subgraph Frontend [Nuxt 4 Container]
        Nuxt[Nuxt SSR/CSR]
    end
    
    subgraph Backend [Spring Boot Container]
        API[REST API Controllers]
        Service[Business Logic]
        Repo[JPA Repositories]
    end
    
    subgraph Data [PostgreSQL Container]
        DB[(PostgreSQL DB)]
    end
    
    Client -->|HTTP/HTTPS| Nuxt
    Nuxt -->|/api Proxy| API
    API --> Service
    Service --> Repo
    Repo -->|JDBC| DB
```

## Key Design Decisions
1. **Nuxt 4 `app/` Directory**: We utilize the new Nuxt 4 structure where all application source code lives in `frontend/app/` to keep the root clean.
2. **Dedicated DTO Layer**: The backend strictly uses DTOs for all Controller interactions, never exposing Entities directly.
3. **API Proxy**: In development, Nuxt proxies `/api` requests to the Spring Boot backend to avoid CORS issues and simplify configuration.
