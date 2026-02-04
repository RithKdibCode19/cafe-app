# Backend Setup Guide

## Prerequisites
* **Java JDK**: Version 21 or 25 (Eclipse Temurin recommended)
* **Maven**: (Wrapper provided in project)
* **PostgreSQL**: Version 15 or 16

## Configuration
Database settings are in `src/main/resources/application.properties` (or `application.yml`).
By default, they are set for the Docker environment but can be overridden.

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/vuespringdb
spring.datasource.username=postgres
spring.datasource.password=123456
```

## Running Locally
1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Run with Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```

## Building a JAR
To package the application as a standalone executable JAR:
```bash
./mvnw clean package -DskipTests
```
The JAR will be located in `target/backend-0.0.1-SNAPSHOT.jar`.

## Hot Reloading
The project includes `spring-boot-devtools`. If you are using IntelliJ IDEA or VS Code, recompile the changed file (Ctrl+F9 or Save) to trigger a hot reload.
