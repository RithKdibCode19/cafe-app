# Backend Setup Guide

## Prerequisites

- **Java JDK**: Version 21 or 25 (Eclipse Temurin recommended)
- **Maven**: (Wrapper provided in project)
- **PostgreSQL**: Version 15 or 16

## Configuration

### Environment Variables

The backend uses environment variables for sensitive configuration. These can be set via:

- `application-local.properties` (for local development)
- Environment variables (for Docker/production)

| Variable                     | Default                                        | Description             |
| ---------------------------- | ---------------------------------------------- | ----------------------- |
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5432/vuespringdb` | Database connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres`                                     | Database username       |
| `SPRING_DATASOURCE_PASSWORD` | _(none)_                                       | Database password       |
| `BAKONG_API_KEY`             | _(none)_                                       | Bakong payment API key  |
| `JWT_SECRET`                 | _(dev default)_                                | JWT signing secret      |

### Local Development Setup

1. Create a local properties file (gitignored):

   ```bash
   nano src/main/resources/application-local.properties
   ```

2. Add your local secrets:

   ```properties
   spring.datasource.password=your_password
   bakong.api.key=your_bakong_key
   jwt.secret=your_jwt_secret
   ```

3. Run with local profile:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local
   ```

> [!IMPORTANT]
> Never commit `application-local.properties` to Git. It's already in `.gitignore`.

## Running Locally

1. Navigate to the backend directory:

   ```bash
   cd backend
   ```

2. Run with Maven Wrapper:

   ```bash
   # With local profile (recommended)
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=local

   # Or with environment variables
   SPRING_DATASOURCE_PASSWORD=123456 ./mvnw spring-boot:run
   ```

## Building a JAR

To package the application as a standalone executable JAR:

```bash
./mvnw clean package -DskipTests
```

The JAR will be located in `target/backend-0.0.1-SNAPSHOT.jar`.

## Hot Reloading

The project includes `spring-boot-devtools`. If you are using IntelliJ IDEA or VS Code, recompile the changed file (Ctrl+F9 or Save) to trigger a hot reload.
