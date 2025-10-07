# Bootcamp API (Reactive Clean Architecture)

A sample Java Spring Boot 3 project that demonstrates a reactive, clean architecture approach for a bootcamp management API. The service exposes HTTP endpoints via functional routing (RouterFunctions/Handlers) and persists data in MySQL through R2DBC.

## Features
- Reactive Spring Boot 3 application targeting Java 17
- Functional routing with handlers instead of traditional annotations
- Clean architecture structure separating domain, infrastructure, and web layers
- MySQL persistence via R2DBC
- OpenAPI 3 documentation generated automatically with springdoc

## Requirements
- Java 17 JDK
- Docker & Docker Compose
- Optional: Gradle 8 wrapper included in repo (`./gradlew`)

## Getting Started
1. **Clone the repository**
   ```bash
   git clone <repo-url>
   cd co-java-bootcamp-api
   ```
2. **Start dependencies and the application with Docker Compose**
   ```bash
   docker compose up --build
   ```
   This spins up MySQL and the Spring Boot service. Once the container logs show the app has started, the API will be available at `http://localhost:8080`.
3. **Run the application from your IDE (optional)**
   - Start only the database:
     ```bash
     docker compose up mysql
     ```
   - Launch the Spring Boot application through your IDE or with the Gradle wrapper:
     ```bash
     ./gradlew bootRun
     ```

## Running Tests
Execute the unit test suite via Gradle:
```bash
./gradlew test
```

## Project Structure
```
src/main/java
├── domain          # Pure domain models (records), use cases, and errors
├── infrastructure  # Database adapters (Reactive MySQL via R2DBC) and mappers
└── web             # HTTP handlers, router configuration, and DTOs
```

## API Documentation
- Swagger UI: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs)

Errors are returned as JSON payloads with the shape:
```json
{ "message": "..." }
```

## Useful Scripts
- `./gradlew build` – Compile and package the application
- `./gradlew bootRun` – Run the Spring Boot service locally
- `docker compose down` – Stop Docker containers when finished

## License
This project is provided for educational purposes as part of a bootcamp curriculum. Adapt and extend it to fit your learning needs.
