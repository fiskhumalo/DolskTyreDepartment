# Dolsk Tyre Department — Backend

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Spring Boot REST API for the Dolsk Tyre platform. Handles authentication, tyre catalogue
management, cart, and orders.

> **Frontend** lives in a separate repository: `dolsk-tyre-frontend`

---

## Tech Stack

| Layer       | Technology                         |
|-------------|------------------------------------|
| Runtime     | Java 17                            |
| Framework   | Spring Boot 3.2.4                  |
| Security    | Spring Security + JWT (JJWT 0.11)  |
| Persistence | Spring Data JPA + Hibernate        |
| Database    | PostgreSQL                         |
| Build       | Maven                              |

---

## Project Structure

```
dolsk-tyre-backend/
├── src/
│   └── main/
│       ├── java/com/dolsk/tyres/
│       │   ├── config/          # SecurityConfig (CORS, JWT filter chain)
│       │   ├── controller/      # REST controllers
│       │   ├── dto/             # Request / response DTOs
│       │   ├── exception/       # Custom exceptions + GlobalExceptionHandler
│       │   ├── model/           # JPA entities
│       │   ├── repository/      # Spring Data repositories
│       │   ├── security/        # JwtUtil, JwtAuthFilter, UserDetailsService
│       │   └── service/         # Service interfaces + implementations
│       └── resources/
│           └── application.properties
└── pom.xml
```

---

## API Endpoints

### Auth — public, no token required

| Method | Path               | Description        |
|--------|--------------------|--------------------|
| POST   | /api/auth/signup   | Register new user  |
| POST   | /api/auth/login    | Login, returns JWT |

Response shape for both:
```json
{
  "success": true,
  "data": { "token": "eyJ...", "userId": 1, "role": "ROLE_USER" },
  "message": null
}
```

### Tyres — authenticated

| Method | Path            | Role  | Description      |
|--------|-----------------|-------|------------------|
| GET    | /api/tyres      | Any   | List all tyres   |
| GET    | /api/tyres/{id} | Any   | Get tyre by ID   |
| POST   | /api/tyres      | ADMIN | Create tyre      |
| PUT    | /api/tyres/{id} | ADMIN | Update tyre      |
| DELETE | /api/tyres/{id} | ADMIN | Delete tyre      |

### Cart — authenticated

| Method | Path                         | Description       |
|--------|------------------------------|-------------------|
| GET    | /api/cart/{userId}           | Get cart          |
| POST   | /api/cart/{userId}/add       | Add item          |
| DELETE | /api/cart/{userId}/item/{id} | Remove item       |
| DELETE | /api/cart/{userId}/clear     | Clear cart        |

### Orders — authenticated

| Method | Path             | Role  | Description         |
|--------|------------------|-------|---------------------|
| POST   | /api/orders      | Any   | Place order         |
| GET    | /api/orders      | Any   | Get my orders       |
| GET    | /api/orders/all  | ADMIN | Get all orders      |
| DELETE | /api/orders/{id} | ADMIN | Delete order        |

---

## Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL (default: `localhost:5433`, database `dolskTyresProject`)

### Configuration

All sensitive values are read from environment variables with local fallbacks:

| Property        | Env var         | Default                                      |
|-----------------|-----------------|----------------------------------------------|
| DB URL          | `DB_URL`        | `jdbc:postgresql://localhost:5433/dolskTyresProject` |
| DB username     | `DB_USERNAME`   | `postgres`                                   |
| DB password     | `DB_PASSWORD`   | *(set locally)*                              |
| JWT secret      | `JWT_SECRET`    | *(set locally, min 32 chars)*                |
| JWT expiry (ms) | `JWT_EXPIRATION_MS` | `604800000` (7 days)                     |
| CORS origins    | `ALLOWED_ORIGINS` | `http://localhost:8081`                    |

### Run locally

```bash
mvn spring-boot:run
```

API available at `http://localhost:8080/api/...`

### Deploy to Render

1. Push this repo (Java only, no Node files)
2. Render auto-detects Java via `pom.xml`
3. Build command: `mvn clean package -DskipTests`
4. Start command: `java -jar target/dolsk-tyre-backend-1.0.0.jar`
5. Set environment variables in Render dashboard

---

## Authentication

All protected endpoints require:
```
Authorization: Bearer <token>
```

`@PreAuthorize("hasRole('ADMIN')")` enforces admin-only routes server-side.
The filter chain uses `SessionCreationPolicy.STATELESS` — no server sessions.

---

## Response Envelope

Every endpoint returns the same wrapper:

```json
{ "success": true,  "data": { ... }, "message": null   }
{ "success": false, "data": null,    "message": "reason" }
```

---

## License

MIT — see [LICENSE](LICENSE).
