# bootcamp API (Reactive, Clean Architecture) — **Java**

**Stack:** Spring Boot 3 + WebFlux (Java), Functional Routing (RouterFunctions/Handlers), springdoc-openapi, Java 17, Docker.

## Run locally
```bash
# 1) Start MySQL and the app (Docker)
docker compose up --build
# Or run only MySQL and start app from IDE
# docker compose up mysql
# ./gradlew bootRun
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Endpoints (high level)
- `POST /api/v1/bootcamp` — create bootcamp `{ name }` → `{ id }`
- `PATCH /api/v1/bootcamp/{bootcampId}/name` — update bootcamp name
- `POST /api/v1/bootcamp/{bootcampId}/branches` — add branch → `{ id }`
- `PATCH /api/v1/bootcamp/{bootcampId}/branches/{branchId}/name` — update branch name
- `POST /api/v1/bootcamp/{bootcampId}/branches/{branchId}/products` — add product → `{ id }`
- `PATCH /api/v1/bootcamp/{bootcampId}/branches/{branchId}/products/{productId}/stock` — update stock
- `PATCH /api/v1/bootcamp/{bootcampId}/branches/{branchId}/products/{productId}/name` — update product name
- `DELETE /api/v1/bootcamp/{bootcampId}/branches/{branchId}/products/{productId}` — remove product
- `GET /api/v1/bootcamp/{bootcampId}/branches/max-stock` — product with max stock per branch

## Architecture notes
- **domain/**: pure models (Java records), use cases, domain errors
- **infrastructure/**: adapters (Reactive MySQL via R2DBC) and mappers
- **web/**: entry points (handlers + router + DTOs)

Errors are JSON: `{ "message": "..." }`.

## OpenAPI
- springdoc autogenerates at `/v3/api-docs` and Swagger UI at `/swagger-ui.html`.
