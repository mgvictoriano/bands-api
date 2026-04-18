# Bands API

REST API built with **Java 21** and **Spring Boot 3.3** that exposes a read-only music bands catalog backed by an external data source, with in-memory caching, standardized error contracts, OpenAPI documentation and a lightweight HTML catalog UI.

**Live demo:** https://labs.mgaia.tech/bands-api
**Swagger UI:** https://labs.mgaia.tech/bands-api/swagger-ui.html

---

## Table of contents

- [Features](#features)
- [Tech stack](#tech-stack)
- [Architecture](#architecture)
- [API reference](#api-reference)
- [Error contract](#error-contract)
- [Configuration](#configuration)
- [Running locally](#running-locally)
- [Running with Docker](#running-with-docker)
- [Testing](#testing)
- [Observability](#observability)
- [Web UI](#web-ui)
- [API documentation](#api-documentation)
- [Project layout](#project-layout)

---

## Features

- Read-only REST API over an external bands catalog.
- In-memory caching with **Caffeine** (TTL + max size), exposing the catalog as an indexed value object for `O(1)` lookups by id or name.
- Consistent error contract (`ApiErrorResponse`) via `@RestControllerAdvice`, mapping domain and validation errors to proper HTTP status codes.
- Bean Validation on HTTP inputs (`jakarta.validation`).
- Configurable HTTP client (base URL, connect/read timeouts) via `@ConfigurationProperties`.
- OpenAPI 3 spec + Swagger UI out of the box.
- Spring Boot Actuator for health, metrics and cache introspection.
- Lightweight static HTML page served at the context root that consumes the API and shows the bands catalog.
- Multi-stage Docker build and Compose file for reproducible deployments.
- Unit and web-layer tests written in BDD style (Given / When / Then with nested classes).

---

## Tech stack

| Area                | Choice                                     |
| ------------------- | ------------------------------------------ |
| Language / runtime  | Java 21                                    |
| Framework           | Spring Boot 3.3.x (Web, Validation, Cache) |
| HTTP client         | `RestClient` (synchronous)                 |
| Cache               | Caffeine                                   |
| API docs            | springdoc-openapi 2.x                      |
| Observability       | Spring Boot Actuator                       |
| Build / packaging   | Maven, multi-stage Dockerfile              |
| Tests               | JUnit 5, Mockito, AssertJ, MockMvc         |
| Boilerplate         | Lombok                                     |

---

## Architecture

Layered package structure with clear separation between the HTTP edge, the application service, the external integration and the domain model.

```text
controller   HTTP endpoints + global exception handler
service      application logic and cached catalog loader
client       adapter for the external bands catalog (RestClient)
mapper       DTO ↔ domain conversion
model        domain entities and in-memory catalog (BandCatalog)
dto          HTTP request/response and external API contracts
config       @ConfigurationProperties, cache, HTTP client, OpenAPI
exception    domain and infrastructure exceptions
```

### Key design decisions

- **Indexed catalog (`BandCatalog`)** — The cached entry is an immutable value object that pre-builds `Map<String, Band>` indices by id and by normalized name, so per-request lookups are `O(1)` and string normalization lives in a single place.
- **Separate `BandCatalogLoader`** — The `@Cacheable` method lives on a dedicated bean so the cache proxy is always applied (no self-invocation pitfalls).
- **External API isolation** — The external contract (`ExternalBandResponse`) is fully decoupled from the public contract (`BandResponse`) and from the domain (`Band`); mapping happens at the edges.
- **Fail-loud external errors** — The HTTP client propagates upstream failures (including `404`) as `ExternalServiceException`, which the advice translates into `502 Bad Gateway`. Nothing is silently swallowed.
- **Validation at the edge** — `@NotBlank` on `@PathVariable` / `@RequestParam` combined with `@Validated` on the controller is the single source of truth for input validation. `ConstraintViolationException` is mapped to `400`.
- **Sanitized error responses** — Unhandled exceptions return a generic `Internal server error` message; full stack traces are logged server-side only.

---

## API reference

All endpoints are served under the servlet context path `/bands-api` and are versioned under `/api/v1`.

Base URL (local): `http://localhost:8080/bands-api`
Base URL (prod):  `https://labs.mgaia.tech/bands-api`

| Method | Path                                   | Description                                        | Success | Errors          |
| ------ | -------------------------------------- | -------------------------------------------------- | ------- | --------------- |
| GET    | `/api/v1/bands`                        | List the full bands catalog (cached)               | 200     | 502             |
| GET    | `/api/v1/bands/id/{id}`                | Get a single band by its UUID                      | 200     | 400, 404, 502   |
| GET    | `/api/v1/bands/by-name/{name}`         | Get a single band by exact name (case-insensitive) | 200     | 400, 404, 502   |
| GET    | `/api/v1/bands/search?name={term}`     | Search bands whose name contains `term`            | 200     | 400, 502        |

### Example requests

```bash
# List all bands
curl https://labs.mgaia.tech/bands-api/api/v1/bands

# Get by id
curl https://labs.mgaia.tech/bands-api/api/v1/bands/id/bc710bcf-8815-42cf-bad2-3f1d12246aeb

# Get by exact name
curl https://labs.mgaia.tech/bands-api/api/v1/bands/by-name/Nickelback

# Partial-name search
curl "https://labs.mgaia.tech/bands-api/api/v1/bands/search?name=the"
```

### Response shape

```json
{
  "id": "bc710bcf-8815-42cf-bad2-3f1d12246aeb",
  "name": "Nickelback",
  "image": "https://...",
  "genre": "rock",
  "biography": "Short biography of the band",
  "numPlays": 17605491
}
```

---

## Error contract

All error responses share the same body:

```json
{
  "timestamp": "2025-01-01T12:34:56Z",
  "status": 404,
  "error": "Not Found",
  "message": "Band not found: Nirvana",
  "path": "/bands-api/api/v1/bands/by-name/Nirvana"
}
```

| Status | When                                                                       |
| ------ | -------------------------------------------------------------------------- |
| 400    | Blank path variable or query parameter (`ConstraintViolationException`)    |
| 404    | Band not found by id or by name                                            |
| 502    | Upstream catalog failure (timeout, 5xx, 4xx, malformed response)           |
| 500    | Unexpected error (sanitized message; full stack trace in server logs)      |

---

## Configuration

All configuration lives in `src/main/resources/application.yml` and can be overridden via environment variables following Spring's [relaxed binding](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding).

| Property                                | Default                         | Description                           |
| --------------------------------------- | ------------------------------- | ------------------------------------- |
| `server.servlet.context-path`           | `/bands-api`                    | Servlet context path                  |
| `clients.band-catalog.base-url`         | `https://bands-api.vercel.app`  | Upstream bands API base URL           |
| `clients.band-catalog.connect-timeout`  | `2s`                            | TCP connect timeout                   |
| `clients.band-catalog.read-timeout`     | `3s`                            | Socket read timeout                   |
| `cache.bands.ttl`                       | `10m`                           | Catalog cache time-to-live            |
| `cache.bands.maximum-size`              | `500`                           | Catalog cache max entries             |

Example override via environment variables:

```bash
CLIENTS_BANDCATALOG_BASEURL=https://my-mirror.example.com \
CACHE_BANDS_TTL=30m \
java -jar app.jar
```

---

## Running locally

Requirements: **JDK 21** and **Maven 3.9+** (or use the Docker instructions below).

```bash
mvn spring-boot:run
```

App will be available at `http://localhost:8080/bands-api`.

Quick smoke test:

```bash
curl http://localhost:8080/bands-api/api/v1/bands | jq '. | length'
```

---

## Running with Docker

```bash
docker compose up --build -d
docker compose logs -f bands-api
docker compose down
```

The image is produced by a multi-stage build (Maven + Temurin 21 → JRE 21 only at runtime). Container-aware JVM flags are set via `JAVA_OPTS`:

```
-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

Port `8080` is exposed and mapped to the host.

---

## Testing

```bash
mvn test
```

Tests follow a BDD-style layout using nested JUnit classes and `@DisplayNameGeneration(ReplaceUnderscores.class)`:

- `BandServiceImplTest` — application logic against a mocked `BandCatalogLoader`.
- `BandCatalogClientTest` — external adapter against a mocked `RestClient` (success, null body, `RestClientException` → `ExternalServiceException`).
- `BandControllerTest` — `@WebMvcTest` covering the HTTP layer and response serialization.

---

## Observability

Spring Boot Actuator exposes the following endpoints under the application context path:

| Endpoint                                | Purpose                              |
| --------------------------------------- | ------------------------------------ |
| `GET /bands-api/actuator/health`        | Liveness / readiness                 |
| `GET /bands-api/actuator/info`          | App metadata (name, version)         |
| `GET /bands-api/actuator/metrics`       | Micrometer metrics catalog           |
| `GET /bands-api/actuator/caches`        | Inspect Caffeine caches              |

> In production, consider restricting `health` details (`show-details: when-authorized`) and putting actuator behind authentication.

---

## Web UI

A static HTML catalog page is served at the context root and consumes the API in the browser:

- Local: http://localhost:8080/bands-api/
- Prod:  https://labs.mgaia.tech/bands-api/

Features: band grid, quick text filter, detail view. The page auto-detects whether the API is mounted under `/bands-api` or at the host root.

---

## API documentation

The API is documented with OpenAPI 3 (springdoc-openapi) and ships with an embedded Swagger UI.

| Resource    | Local                                                     | Production                                                  |
| ----------- | --------------------------------------------------------- | ----------------------------------------------------------- |
| Swagger UI  | http://localhost:8080/bands-api/swagger-ui.html           | https://labs.mgaia.tech/bands-api/swagger-ui.html           |
| OpenAPI JSON| http://localhost:8080/bands-api/v3/api-docs               | https://labs.mgaia.tech/bands-api/v3/api-docs               |

---

## Project layout

```
.
├── Dockerfile                       # Multi-stage build (Maven → JRE 21)
├── compose.yaml                     # Local container orchestration
├── pom.xml
└── src
    ├── main
    │   ├── java/com/challenge/bandsapi
    │   │   ├── BandsApiApplication.java
    │   │   ├── client/              # External API adapter
    │   │   ├── config/              # Properties, cache, HTTP client, OpenAPI
    │   │   ├── controller/          # REST controllers + global exception handler
    │   │   ├── dto/                 # HTTP + external DTOs
    │   │   ├── exception/           # Domain + infrastructure exceptions
    │   │   ├── mapper/              # Domain ↔ DTO mappers
    │   │   ├── model/               # Band entity + BandCatalog VO
    │   │   └── service/             # Application services + cache loader
    │   └── resources
    │       ├── application.yml
    │       └── static/index.html    # Catalog UI
    └── test/java/...                # BDD-style unit and web-layer tests
```

---

## License

Educational / portfolio project. No warranty.
