# bands-api

Spring Boot backend challenge starter with Java 21, clean architecture folders, REST endpoints, outbound HTTP client adapter, and Caffeine caching.

## Stack

- Java 21
- Spring Boot 3.3.x
- Maven
- Caffeine cache

## Project structure

```text
src/main/java/com/challenge/bandsapi
|-- controller      ← REST endpoints + GlobalExceptionHandler
|-- service         ← BandService (interface) + BandServiceImpl
|-- client          ← BandCatalogClient (HTTP adapter)
|-- dto             ← BandResponse, ExternalBandResponse, ApiErrorResponse
|-- config          ← CacheConfig, HttpClientConfig, Properties
|-- exception       ← BandNotFoundException, ExternalServiceException
|-- mapper          ← BandResponseMapper, BandCatalogMapper
\-- model           ← Band (domain record)
```

## Data source

- `https://bands-api.vercel.app/api/bands`

## Endpoints

- `GET /api/v1/bands`
- `GET /api/v1/bands/id/{id}`
- `GET /api/v1/bands/{name}`
- `GET /api/v1/bands/search?name={term}`

## Configuration

`src/main/resources/application.yml` includes:

- `clients.band-catalog.base-url`
- `clients.band-catalog.connect-timeout`
- `clients.band-catalog.read-timeout`
- `cache.bands.ttl`
- `cache.bands.maximum-size`

## Run

```bash
mvn spring-boot:run
```

## Run with Docker (Java 21)

```bash
docker compose up --build -d
docker compose logs -f bands-api
```

Quick check:

```bash
curl "http://localhost:8080/api/v1/bands"
```

Stop containers:

```bash
docker compose down
```

## Test

```bash
mvn test
```
