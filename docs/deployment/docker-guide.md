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

> [!IMPORTANT]
> The `.env` file is gitignored. Copy from `.env.example` and fill in your values.

### Root Configuration (`/.env`)

| Variable                | Description                                |
| ----------------------- | ------------------------------------------ |
| `POSTGRES_DB`           | Name of the PostgreSQL database.           |
| `POSTGRES_USER`         | Database username.                         |
| `POSTGRES_PASSWORD`     | Database password.                         |
| `BACKEND_PORT`          | Port exposed to the host for the Backend.  |
| `FRONTEND_PORT`         | Port exposed to the host for the Frontend. |
| `SPRING_DATASOURCE_URL` | JDBC connection string for the backend.    |
| `BAKONG_API_KEY`        | Bakong payment API key.                    |
| `JWT_SECRET`            | Secret for JWT token signing.              |

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
docker exec -it cafe-postgres psql -U $POSTGRES_USER -d $POSTGRES_DB
```

### 2. Via Adminer (Web GUI)

The stack includes **Adminer**, a lightweight database management tool.

- **URL**: [http://localhost:8080](http://localhost:8080)
- **System**: PostgreSQL
- **Server**: `postgres` (internal docker hostname)
- **Username**: _(from your .env file)_
- **Password**: _(from your .env file)_
- **Database**: _(from your .env file)_

### 3. Via External Clients (DBeaver, TablePlus)

Connect using the host port `5432`.

- **Host**: `localhost`
- **Port**: `5432`
- **Database**: _(from your .env file)_
- **User**: _(from your .env file)_
- **Password**: _(from your .env file)_
