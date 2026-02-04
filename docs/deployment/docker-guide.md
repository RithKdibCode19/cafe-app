# Docker & Deployment Guide

## Docker Compose Structure

We use a root-level `docker-compose.yml` to orchestrate the entire application stack.

### Services

| Service      | Container Name  | Internal Port | Host Port | Description         |
| ------------ | --------------- | ------------- | --------- | ------------------- |
| **frontend** | `cafe-frontend` | 3000          | 3000      | Nuxt 4 Application  |
| **backend**  | `cafe-backend`  | 8081          | 8081      | Spring Boot API     |
| **postgres** | `cafe-postgres` | 5432          | 5432      | Main Database       |
| **adminer**  | `cafe-adminer`  | 8080          | 8080      | Database GUI Client |

## Running the Stack

### 1. Build and Start

To build fresh images and start all containers in the background:

```bash
docker-compose up -d --build
```

### 2. View Logs

To follow logs for all services:

```bash
docker-compose logs -f
```

To follow logs for a specific service (e.g., backend):

```bash
docker-compose logs -f backend
```

### 3. Stop Services

To stop containers but keep data (volumes):

```bash
docker-compose down
```

To stop and **delete volumes** (RESET DATABASE):

```bash
docker-compose down -v
```

## Environment Variables

We use a root-level `.env` file to manage configuration across all services. This file is automatically loaded by Docker Compose.

### Root Configuration (`/.env`)

| Variable                | Description                                |
| ----------------------- | ------------------------------------------ |
| `POSTGRES_DB`           | Name of the PostgreSQL database.           |
| `POSTGRES_USER`         | Database username.                         |
| `POSTGRES_PASSWORD`     | Database password.                         |
| `BACKEND_PORT`          | Port exposed to the host for the Backend.  |
| `FRONTEND_PORT`         | Port exposed to the host for the Frontend. |
| `SPRING_DATASOURCE_URL` | JDBC connection string for the backend.    |

### Service-Specific Config

- **Frontend**: Uses `NUXT_PUBLIC_API_BASE` for client-side calls and `NUXT_API_BASE_INTERNAL` for SSR (Server Side Rendering) inside the Docker network.
- **Backend**: Uses standard Spring properties mapped from environment variables in `docker-compose.yml`.

## Production Builds

### Frontend

The frontend uses a multi-stage Dockerfile:

1. **Build Stage**: Compiles the Nuxt app using `npm run build`.
2. **Runtime Stage**: Copies `.output/` to a lightweight Node Alpine image.

### Backend

The backend uses a multi-stage Dockerfile:

1. **Build Stage**: Compiles Java code using Maven wrapper + Eclipse Temurin JDK.
2. **Runtime Stage**: Runs the JAR file on a lightweight JRE Alpine image.

## Database Access

### 1. Via Terminal (CLI)

You can access the `psql` shell directly inside the container.

**For Root Docker Compose (Container: `cafe-postgres`):**

```bash
docker exec -it cafe-postgres psql -U postgres -d vuespringdb
```

**For Backend-only Docker Compose (Container: `postgres-db`):**

```bash
docker exec -it postgres-db psql -U postgres -d vuespringdb
```

_Note: The password is `123456`. If asked, enter it._

### 2. Via Adminer (Web GUI)

The stack includes **Adminer**, a lightweight database management tool.

- **URL**: [http://localhost:8080](http://localhost:8080)
- **System**: PostgreSQL
- **Server**: `postgres` (internal docker hostname)
- **Username**: `postgres`
- **Password**: `123456`
- **Database**: `vuespringdb`

### 3. Via External Clients (DBeaver, TablePlus)

Connect using the host port `5432`.

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: `vuespringdb`
- **User**: `postgres`
- **Password**: `123456`
