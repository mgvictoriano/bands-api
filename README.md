# 🎧 Bands API

Production-ready REST API built with **Java 21** and **Spring Boot 3**, designed to expose a music bands catalog with caching, resilience, and clean architecture principles.

---

## 🚀 Highlights

* Clean architecture with clear separation of concerns
* External API integration (client adapter pattern)
* In-memory caching with Caffeine for performance optimization
* Global error handling with standardized response contract
* OpenAPI (Swagger) documentation
* Dockerized for consistent environments
* Unit and web layer tests
* Production-ready features (timeouts, validation, logging)

---

## 🧠 Architecture Overview

This project follows a layered and modular approach:

```text
controller  → HTTP layer (REST endpoints)
service     → business logic
client      → external API integration
mapper      → DTO ↔ domain conversion
model       → domain entities
config      → infrastructure configuration
exception   → domain + infrastructure errors
```

### Key Design Decisions

* **RestClient over WebClient**
  Simpler and sufficient for synchronous external communication.

* **DTO vs Domain separation**
  Prevents external contract leakage into core business logic.

* **Caching layer (Caffeine)**
  Reduces latency and avoids unnecessary external API calls.

* **Fail-fast validation**
  Input is validated early to prevent inconsistent states.

* **Global exception handler**
  Ensures consistent and predictable API error responses.

---

## 🌍 External Data Source

Data is fetched from:

```
https://bands-api.vercel.app/api/bands
```

---

## 📡 API Endpoints

| Method | Endpoint                         | Description                  |
| ------ | -------------------------------- | ---------------------------- |
| GET    | /api/v1/bands                    | List all bands               |
| GET    | /api/v1/bands/id/{id}            | Get band by ID               |
| GET    | /api/v1/bands/{name}             | Get band by exact name       |
| GET    | /api/v1/bands/search?name={term} | Search bands by partial name |

---

## ⚙️ Configuration

Configured via `application.yml`:

* External API:

    * `clients.band-catalog.base-url`
    * `connect-timeout`
    * `read-timeout`

* Cache:

    * `cache.bands.ttl`
    * `cache.bands.maximum-size`

---

## 🐳 Running the Application

### ▶️ Local

```bash
mvn spring-boot:run
```

---

### 🐳 Docker

```bash
docker compose up --build -d
```

Logs:

```bash
docker compose logs -f bands-api
```

Stop:

```bash
docker compose down
```

---

## 🔍 Quick Test

```bash
curl http://localhost:8080/api/v1/bands
```

---

## 🧪 Running Tests

```bash
mvn test
```

---

## 📊 Observability

Spring Boot Actuator endpoints available:

* `/actuator/health`
* `/actuator/metrics`
* `/actuator/caches`

---

## 📄 API Documentation

Swagger UI available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔮 Future Improvements

* Add circuit breaker (Resilience4j)
* Introduce integration tests with WireMock
* Add CI/CD pipeline (GitHub Actions)
* Deploy to cloud (Railway / Render)
* Add request tracing and structured logging

---

## 👩‍💻 Author

Developed as a production-grade backend challenge, focusing on clean architecture, maintainability, and real-world engineering practices.
