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

### 3. Configure Backend Secrets

Create local configuration:

```bash
cd backend
nano src/main/resources/application-local.properties
```

Add your local secrets:

```properties
spring.datasource.password=your_db_password
bakong.api.key=your_bakong_key
jwt.secret=your_jwt_secret
```

### 4. Run Backend (Spring Boot)

Open a new terminal:

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local

JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./mvnw clean spring-boot:run -Dspring-boot.run.profiles=local
```

Cleaning

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./mvnw clean compile -DskipTests
```

- API will be at: `http://localhost:8081`

### 5. Run Frontend (Nuxt)

Open another terminal:

```bash
cd frontend
npm install
npm run dev
```

- Web app will be at: `http://localhost:3000`

---

## 🐳 Full Docker Mode

If you just want to run the entire system without installing Java or Node:

1. Copy and configure environment:

   ```bash
   cp .env.example .env
   nano .env  # Fill in your passwords
   ```

2. Start all services:
   ```bash
   docker-compose up -d --build
   ```

Access points:

- **Frontend**: [http://localhost:3000](http://localhost:3000)
- **Backend API**: [http://localhost:8081](http://localhost:8081)
- **Adminer (DB UI)**: [http://localhost:8080](http://localhost:8080)

---

## ⚙️ Configuration

System configuration is managed via environment variables. See the table below:

| Variable            | Description          |
| ------------------- | -------------------- |
| `POSTGRES_DB`       | Database name        |
| `POSTGRES_PASSWORD` | Database password    |
| `BAKONG_API_KEY`    | Payment API key      |
| `JWT_SECRET`        | Token signing secret |

> [!IMPORTANT]
> Never commit `.env` files or `application-local.properties` to Git!

---

## 📂 Documentation

Detailed documentation is available in the [`/docs`](./docs) directory:

- [Architecture Overview](./docs/architecture/system-overview.md)
- [Backend Development](./docs/backend/setup.md)
- [Frontend Development](./docs/frontend/setup.md)
- [Docker & Deployment](./docs/deployment/docker-guide.md)
- [Database Schema](./docs/architecture/database-schema.md)

---

## 🚀 Production Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for VPS deployment instructions including:

- Docker Compose production config
- Nginx reverse proxy
- SSL/TLS with Cloudflare

---

## 🛠 Troubleshooting

- **Maven Build Errors**: If you see missing dependency versions, check `backend/pom.xml`.
- **Database Connection**: Ensure the `postgres` container is healthy before starting the backend natively.
- **Port Conflicts**: If port 5432 or 8081 is taken, update the `.env` file in the root.
- **Missing Secrets**: If backend fails to start, ensure `application-local.properties` exists with required secrets.
