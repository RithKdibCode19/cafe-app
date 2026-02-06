# Cafe POS System

A modern Point of Sale system built with **Spring Boot 3** (Backend) and **Nuxt 4** (Frontend).

## 🚀 Quick Start (Hybrid Mode - Recommended)

For the best development experience, we recommend running the **Infrastructure** (Database) in Docker and the **Applications** (Backend & Frontend) natively on your host machine. This allows for instant hot-reload and debugger support.

### 1. Prerequisites
- **Docker & Docker Compose**
- **Java 21 JDK** (for Backend)
- **Node.js 22+** (for Frontend)

### 2. Setup Infrastructure
Start the database and management tools:
```bash
docker-compose up -d postgres adminer
```

### 3. Run Backend (Spring Boot)
Open a new terminal:
```bash
cd backend
./mvnw spring-boot:run
```
- API will be at: `http://localhost:8081`
- Swagger/OpenAPI docs (if configured): `http://localhost:8081/swagger-ui.html`

### 4. Run Frontend (Nuxt)
Open another terminal:
```bash
cd frontend
npm install
npm run dev
```
- Web app will be at: `http://localhost:3000`

---

## 🐳 Full efore, database queries may be performed during view rendering. Explicitly configure spring.jpa.open-inDocker Mode

If you just want to run the entire system without installing Java or Node:

```bash
docker-compose up -d --build
```

Access points:
- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **Backend API**: [http://localhost:8081](http://localhost:8081)
- **Adminer (DB UI)**: [http://localhost:8080](http://localhost:8080)

---

## ⚙️ Configuration

System configuration is managed via the **root `.env` file**. You can change ports, database names, and credentials there.

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_DB` | `vuespringdb` | Database name |
| `BACKEND_PORT` | `8081` | External port for API |
| `FRONTEND_PORT` | `3000` | External port for Web UI |

---

## 📂 Documentation

Detailed documentation is available in the [`/docs`](./docs) directory:

- [Architecture Overview](./docs/architecture/system-overview.md)
- [Backend Development](./docs/backend/setup.md)
- [Frontend Development](./docs/frontend/setup.md)
- [Docker & Deployment](./docs/deployment/docker-guide.md)
- [Database Schema](./docs/architecture/database-schema.md)

---

## 🛠 Troubleshooting

- **Maven Build Errors**: If you see missing dependency versions, it is likely an incorrect artifact name. Check `backend/pom.xml`.
- **Database Connection**: Ensure the `postgres` container is healthy before starting the backend natively.
- **Port Conflicts**: If port 5432 or 8081 is taken, update the `.env` file in the root.
