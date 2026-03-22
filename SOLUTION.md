# Hotel Room Allocation System

> A production-grade REST API built with **Java 25** and **Spring Boot 4.0** that optimizes hotel room allocation between Premium and Economy categories.

---

## Table of Contents

- [Tech Stack](#-tech-stack)
- [Architecture & Design](#-architecture--design)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Getting Started](#-getting-started)
- [API Reference](#-api-reference)
- [Swagger / OpenAPI](#-swagger--openapi)
- [Business Rules](#-business-rules)
- [Testing Strategy (TDD)](#-testing-strategy-tdd)
- [Code Coverage](#-code-coverage)
- [Non-Functional Requirements (NFRs)](#-non-functional-requirements-nfrs)
- [REST API Design Principles](#-rest-api-design-principles)
- [Configuration Reference](#-configuration-reference)
- [Docker Support](#-docker-support)

---

## 🛠 Tech Stack

| Layer              | Technology                                               |
|--------------------|----------------------------------------------------------|
| Language           | **Java 25** (latest LTS-track release)                   |
| Framework          | **Spring Boot 4.0.3** (Spring Framework 7.x)             |
| Build Tool         | **Apache Maven** (Maven Wrapper included)                |
| Validation         | **Jakarta Bean Validation** (`@NotNull`, `@Min`)         |
| Error Handling     | **RFC 9457 Problem Details** (Spring built-in)           |
| Caching            | **Spring Cache** (`@Cacheable` + `@EnableCaching`)       |
| API Documentation  | **SpringDoc OpenAPI 2.8** + Swagger UI                   |
| Monitoring         | **Spring Boot Actuator** (health, info, caches, metrics) |
| Testing            | **JUnit 5** + **Mockito** + **AssertJ** + **MockMvc**    |
| Code Coverage      | **100% **                        |

---

## 🏗 Architecture & Design

The application follows a **layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────────────────────┐
│                   Controller Layer                   │
│          OccupancyController (thin, @Valid)          │
├─────────────────────────────────────────────────────┤
│                    Service Layer                     │
│    OccupancyService ←── DefaultOccupancyService     │
│                    (@Cacheable)                      │
├─────────────────────────────────────────────────────┤
│                   Strategy Layer                     │
│  RoomAllocationStrategy ←── DefaultRoomAllocation   │
│                 (pure business logic)                │
├─────────────────────────────────────────────────────┤
│                     DTO / Model                      │
│   OccupancyRequest · OccupancyResponse · Result     │
│          (Java Records — immutable)                  │
└─────────────────────────────────────────────────────┘
```

## 📁 Project Structure

```
src/
├── main/java/com/shijin/hotel/room/allocation/
│   ├── AllocationApplication.java              # Spring Boot entry point
│   ├── config/
│   │   └── OpenApiConfig.java                  # Swagger/OpenAPI metadata
│   ├── controller/
│   │   └── OccupancyController.java            # REST endpoint (POST /occupancy)
│   ├── dto/
│   │   ├── OccupancyRequest.java               # Request record with Bean Validation
│   │   └── OccupancyResponse.java              # Response record
│   ├── model/
│   │   └── AllocationResult.java               # Internal domain value object
│   ├── service/
│   │   ├── OccupancyService.java               # Service interface (ISP)
│   │   └── DefaultOccupancyService.java        # Service impl with @Cacheable
│   └── strategy/
│       ├── RoomAllocationStrategy.java          # Strategy interface (OCP)
│       └── DefaultRoomAllocationStrategy.java   # Allocation algorithm
├── main/resources/
│   └── application.properties                   # All configuration
└── test/java/com/shijin/hotel/room/allocation/
    ├── AllocationApplicationTests.java          # Context load test
    ├── OccupancyIntegrationTest.java            # Full end-to-end integration tests
    ├── config/
    │   └── OpenApiConfigTest.java               # OpenAPI config unit test
    ├── controller/
    │   └── OccupancyControllerTest.java         # @WebMvcTest (mocked service)
    ├── service/
    │   ├── DefaultOccupancyServiceTest.java     # Service unit test (mocked strategy)
    │   └── OccupancyServiceCacheIntegrationTest.java  # Cache behaviour verification
    └── strategy/
        └── DefaultRoomAllocationStrategyTest.java  # 19 test cases for allocation logic
```

---

## 📋 Prerequisites

| Tool          | Version   |
|---------------|-----------|
| **Java JDK**  | 25+       |
| **Maven**     | 3.9+ (or use the included `mvnw` / `mvnw.cmd` wrapper) |

> No other dependencies required. Maven Wrapper is included — no need to install Maven globally.

---

## 🚀 Getting Started

### Clone & Build

```bash
git clone https://github.com/shijinraj/Hotel_Room_Allocation.git
cd Hotel_Room_Allocation
```

### Run Tests

```bash
./mvnw clean test
```

### Build & Start Application

```bash
./mvnw clean package -DskipTests
java -jar target/allocation-0.0.1-SNAPSHOT.jar
```

Or use docker:

Windows Powershell :
`docker run --rm -p 8080:8080  -v ${PWD}:/app -w /app eclipse-temurin:25-jdk-jammy ./run.sh`

Windows command line :
`docker run --rm -p 8080:8080  -v $PROJECT_LOCATION:/app -w /app eclipse-temurin:25-jdk-jammy ./run.sh`

Example : `docker run --rm -p 8080:8080  -v C:\project\Hotel_Room_Allocation:/app -w /app eclipse-temurin:25-jdk-jammy ./run.sh`

The application starts on **http://localhost:8080**.

### Quick Smoke Test

```bash
curl -X POST http://localhost:8080/occupancy \
  -H "Content-Type: application/json" \
  -d '{
    "premiumRooms": 7,
    "economyRooms": 5,
    "potentialGuests": [23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209]
  }'
```

**Expected Response:**
```json
{
  "usagePremium": 6,
  "revenuePremium": 1054,
  "usageEconomy": 4,
  "revenueEconomy": 189.99
}
```

---

## 📡 API Reference

### `POST /occupancy`

Calculate optimal room allocation and revenue.

#### Request

| Field            | Type       | Required | Constraint | Description                                    |
|------------------|------------|----------|------------|------------------------------------------------|
| `premiumRooms`   | `Integer`  | ✅       | `≥ 0`      | Number of available Premium rooms              |
| `economyRooms`   | `Integer`  | ✅       | `≥ 0`      | Number of available Economy rooms              |
| `potentialGuests` | `Double[]` | ✅       | not null   | Guest willingness-to-pay values (EUR)          |

#### Response (200 OK)

| Field            | Type     | Description                          |
|------------------|----------|--------------------------------------|
| `usagePremium`   | `int`    | Number of Premium rooms occupied     |
| `revenuePremium` | `number` | Total revenue from Premium rooms (EUR) |
| `usageEconomy`   | `int`    | Number of Economy rooms occupied     |
| `revenueEconomy` | `number` | Total revenue from Economy rooms (EUR) |

#### Error Response (400 Bad Request — RFC 9457)

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "Invalid request content.",
  "instance": "/occupancy"
}
```

#### Other Error Codes

| Code | Reason                     |
|------|----------------------------|
| `405` | Method Not Allowed (e.g. GET instead of POST) |
| `415` | Unsupported Media Type (e.g. XML instead of JSON) |

---

## 📖 Swagger / OpenAPI

Once the application is running, interactive API documentation is available at:

| Resource              | URL                                        |
|-----------------------|--------------------------------------------|
| **Swagger UI**        | http://localhost:8080/swagger-ui.html       |
| **OpenAPI JSON Spec** | http://localhost:8080/api-docs              |

Features:
- Try-it-out functionality to test the API directly from the browser
- Full request/response schema documentation with examples
- RFC 9457 ProblemDetail error schema documented

---

## 📏 Business Rules

1. **Premium guests (≥ EUR 100)** → allocated to Premium rooms only
2. **Economy guests (< EUR 100)** → allocated to Economy rooms by default
3. **Smart Upgrade** — when Economy rooms are full AND Premium rooms remain empty:
   - The highest-paying Economy guests get upgraded to Premium rooms
4. **Overbooking** — when more guests than rooms:
   - Only the highest-paying guests get rooms

### Validated Test Cases

| # | Premium Rooms | Economy Rooms | Premium Usage | Premium Revenue | Economy Usage | Economy Revenue |
|---|:---:|:---:|:---:|:---:|:---:|:---:|
| 1 | 3 | 3 | 3 | EUR 738 | 3 | EUR 167.99 |
| 2 | 7 | 5 | 6 | EUR 1054 | 4 | EUR 189.99 |
| 3 | 2 | 7 | 2 | EUR 583 | 4 | EUR 189.99 |

All test cases use the guest data: `[23, 45, 155, 374, 22, 99.99, 100, 101, 115, 209]`

---

## 🧪 Testing Strategy (TDD)

The project follows **Test-Driven Development** with a comprehensive, multi-layered test suite:

### Test Layers

| Layer | Test Class | Scope | Approach |
|-------|-----------|-------|----------|
| **Unit** | `DefaultRoomAllocationStrategyTest` | Allocation algorithm in isolation | Plain JUnit — no Spring context |
| **Unit** | `DefaultOccupancyServiceTest` | Service logic with mocked strategy | Mockito `@Mock` + `@InjectMocks` |
| **Unit** | `OpenApiConfigTest` | OpenAPI metadata bean | Plain JUnit — no Spring context |
| **Slice** | `OccupancyControllerTest` | Controller + validation + error handling | `@WebMvcTest` with mocked service |
| **Integration** | `OccupancyIntegrationTest` | Full API end-to-end | `@SpringBootTest` + `@AutoConfigureMockMvc` |
| **Integration** | `OccupancyServiceCacheIntegrationTest` | Cache hit/miss behaviour | `@SpringBootTest` with mocked strategy |
| **Smoke** | `AllocationApplicationTests` | Spring context loads | `@SpringBootTest` |

### Test Coverage Breakdown

| Test Class | Test Cases | What It Validates |
|------------|:---:|-------------------|
| `DefaultRoomAllocationStrategyTest` | **19** | All 3 requirement test cases, null/empty guests, zero rooms, premium-only guests, economy-only guests, upgrade logic (4 scenarios), overbooking (2 scenarios), boundary values (100.0 vs 99.99), single-guest cases |
| `OccupancyControllerTest` | **9** | 200 OK responses, content-type enforcement, 415 Unsupported Media Type, 405 Method Not Allowed, 5 validation error scenarios with RFC 9457 ProblemDetail assertions |
| `OccupancyIntegrationTest` | **4** | All 3 requirement test cases end-to-end, empty guests edge case |
| `OccupancyServiceCacheIntegrationTest` | **3** | Cache hit on duplicate request, separate computation for different requests |
| `DefaultOccupancyServiceTest` | **2** | Delegation to strategy, zero rooms with empty guests |
| `AllocationApplicationTests` | **2** | Context loads, `main()` method coverage |
| `OpenApiConfigTest` | **1** | OpenAPI bean metadata |

## 📊 Code Coverage

JaCoCo is configured with **enforced 100% coverage** — the build **fails** if any threshold is not met:

| Metric     | Required | Achieved |
|------------|:--------:|:--------:|
| **Line**   | 100%     | ✅ 100%  |
| **Branch** | 100%     | ✅ 100%  |
| **Method** | 100%     | ✅ 100%  |
| **Class**  | 100%     | ✅ 100%  |

### View Coverage Report

```bash
./mvnw clean test
open target/site/jacoco/index.html
```

---

## ⚡ Non-Functional Requirements (NFRs)

### 1. Performance — Spring Cache (`@Cacheable`)

Identical requests return cached results without re-computing the allocation.

```java
@Cacheable( "calculateOccupancy" )
public OccupancyResponse calculateOccupancy( OccupancyRequest request ) { ... }
```

- Uses Spring Boot's default `ConcurrentMapCacheManager` — zero external dependencies
- Java `record` types provide correct `equals()`/`hashCode()` for cache keys automatically
- Enabled via simple `@EnableCaching` annotation

### 2. Resilience — Graceful Shutdown

```properties
server.shutdown=graceful
spring.lifecycle.timeout-per-shutdown-phase=30s
```

In-flight requests complete before the application shuts down.

### 3. Observability — Spring Boot Actuator

```properties
management.endpoints.web.exposure.include=health,info,caches,metrics
```

| Endpoint                          | Purpose                         |
|-----------------------------------|---------------------------------|
| `GET /actuator/health`            | Application health check        |
| `GET /actuator/info`              | Application info                |
| `GET /actuator/caches`            | Cache status and stats          |
| `GET /actuator/metrics`           | JVM, HTTP, and cache metrics    |

### 4. Standards-Compliant Error Handling — RFC 9457 Problem Details

```properties
spring.mvc.problemdetails.enabled=true
```

All validation errors return structured `application/problem+json` responses per [RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) — no custom exception handlers needed.

### 5. API Documentation — SpringDoc OpenAPI + Swagger UI

- Auto-generated OpenAPI 3.1 specification from controller annotations
- Interactive Swagger UI at `/swagger-ui.html`
- Rich `@Schema` annotations on DTOs with descriptions and examples
- `@Operation` / `@ApiResponse` annotations documenting success and error responses

### 6. Immutability — Java Records

All DTOs and domain objects are Java `record` types:
- **Immutable by design** — no setters, no mutation
- **Built-in** `equals()`, `hashCode()`, `toString()`
- **Thread-safe** without synchronization
- **Compact** — eliminates boilerplate

---

## 🌐 REST API Design Principles

| Principle | Implementation |
|-----------|----------------|
| **Resource-oriented URL** | `/api/v1/occupancy` — noun, not verb |
| **Correct HTTP method** | `POST` — computes a new result from input data |
| **Content negotiation** | `consumes = application/json`, `produces = application/json` — explicit on controller |
| **Structured error responses** | RFC 9457 ProblemDetail with `title`, `status`, `detail`, `instance` |
| **Input validation** | `@Valid` + Jakarta Bean Validation with descriptive error messages |
| **Proper HTTP status codes** | `200` success, `400` validation error, `405` wrong method, `415` wrong content type |
| **Thin controller** | Zero business logic — delegates entirely to the service layer |
| **Constructor injection** | No `@Autowired` on fields — explicit, testable, immutable dependencies |

---

## ⚙ Configuration Reference

All configuration in `src/main/resources/application.properties`:

| Property | Value | Purpose |
|----------|-------|---------|
| `server.port` | `8080` | Application HTTP port |
| `spring.mvc.problemdetails.enabled` | `true` | RFC 9457 error responses |
| `server.shutdown` | `graceful` | Wait for in-flight requests on shutdown |
| `spring.lifecycle.timeout-per-shutdown-phase` | `30s` | Max wait time during shutdown |
| `management.endpoints.web.exposure.include` | `health,info,caches,metrics` | Actuator endpoints |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Swagger UI URL |
| `springdoc.api-docs.path` | `/api-docs` | OpenAPI JSON spec URL |

---

## 🐳 Docker Support

The project includes a `run.sh` script designed for the automated testing Docker container (`eclipse-temurin:25-jdk-jammy`):

Windows Powershell : 
`docker run --rm -p 8080:8080  -v ${PWD}:/app -w /app eclipse-temurin:25-jdk-jammy ./run.sh`

Windows command line :
`docker run --rm -p 8080:8080  -v $PROJECT_LOCATION:/app -w /app eclipse-temurin:25-jdk-jammy ./run.sh`

Example : `docker run --rm -p 8080:8080  -v C:\project\backend-engineer-coding-challenge:/app -w /app eclipse-temurin:25-jdk-jammy ./run.sh`

This script builds the fat JAR and starts the application on port 8080.

---

*Built with using Java 25 and Spring Boot 4.0*

