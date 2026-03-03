# Spring Boot Workspace

A multi-module Maven project built with Spring Boot 4.0, Java 21, and PostgreSQL.

## Project Structure

```
spring-boot-workspace/
├── applications/
│   └── rest-service          # Spring Boot REST service (REST API, entry point)
├── libraries/
│   ├── library              # Core business logic (entities, services, repositories)
│   └── spring-library       # Spring Boot auto-configuration library
├── shared/
│   ├── archunit-rules       # Shared ArchUnit architecture test rules
│   ├── build-tools          # Build resources (JaCoCo dark theme CSS, Maven version rules)
│   ├── liquibase            # Database changelog files
│   └── test-fixtures        # Shared test base classes and Testcontainers utilities
├── boms/
│   ├── external-bom         # Manages third-party dependency versions (Spring Boot, Micrometer, Lombok, ArchUnit)
│   └── internal-bom         # Manages internal module versions; imports external-bom
├── parents/
│   ├── shared-parent        # Parent POM for shared modules
│   ├── library-parent       # Parent POM for library modules
│   └── application-parent   # Parent POM for application modules
├── coverage/                # Aggregated JaCoCo coverage reports
└── docker/                  # Docker support files (Prometheus, Grafana)
```

### Module Overview

| Module             | Description                                                                                                                        |
|--------------------|------------------------------------------------------------------------------------------------------------------------------------|
| **rest-service** | Spring Boot REST service exposing a REST API under `/api/greetings`. Depends on `library` and `spring-library`.                  |
| **library**        | Contains the `Greeting` JPA entity, `GreetingRepository`, and `GreetingService`. Framework-agnostic business logic.                |
| **spring-library** | Provides `MyService` via Spring Boot `@AutoConfiguration`. Registered through `META-INF` auto-configuration.                       |
| **archunit-rules** | Shared ArchUnit rules and an abstract base test class for architecture enforcement.                                                |
| **build-tools**    | Build-time resources: a dark-themed CSS for JaCoCo reports and Maven version rules.                                                |
| **liquibase**      | Liquibase changelog files used for database schema management.                                                                     |
| **test-fixtures**  | Shared test infrastructure: Testcontainers PostgreSQL holder, Liquibase migration runner, and abstract database test base classes. |
| **coverage**       | Aggregation-only module that produces combined JaCoCo reports (unit, integration, and merged) across all code modules.             |

### BOM and Parent Hierarchy

The root POM (`spring-boot-workspace`) acts as the grandparent for all modules and defines shared plugin and property management.

- **external-bom** imports the Spring Boot BOM, Micrometer BOM, Lombok, and ArchUnit.
- **internal-bom** imports `external-bom` and declares versions for all internal modules.
- **shared-parent**, **library-parent**, and **application-parent** each import the appropriate BOM and provide common dependencies (Lombok, ArchUnit, test-fixtures) to their child modules.

## Prerequisites

- Java 21+
- Maven 3.9+
- Docker and Docker Compose (for the local environment)

## Build

The project includes a Maven wrapper. To build all modules and run tests:

```bash
./mvnw clean verify
```

## Running Locally with Docker Compose

The `docker-compose.yml` defines the following services:

| Service         | Port                          | Description                                                            |
|-----------------|-------------------------------|------------------------------------------------------------------------|
| **postgres**    | `5432`                        | PostgreSQL 17 database                                                 |
| **liquibase**   | —                             | Runs schema migrations on startup, then exits                          |
| **rest-service** | `8080` (HTTP), `5005` (debug) | Spring Boot REST service with remote debug enabled                   |
| **prometheus**  | `9090`                        | Scrapes metrics from the application's `/actuator/prometheus` endpoint |
| **grafana**     | `3000`                        | Pre-configured with a Prometheus datasource and a project dashboard    |

### Service Links

Once the environment is running, the following URLs are available on the host:

| Service      | URL                                                                            |
|--------------|--------------------------------------------------------------------------------|
| Application  | [http://localhost:8080](http://localhost:8080)                                 |
| Swagger UI   | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| OpenAPI Spec | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)         |
| Actuator     | [http://localhost:8080/actuator](http://localhost:8080/actuator)               |
| Prometheus   | [http://localhost:9090](http://localhost:9090)                                 |
| Grafana      | [http://localhost:3000](http://localhost:3000)                                 |

### Start the environment

Build the application JAR first, then start Docker Compose:

```bash
./mvnw clean package -DskipTests
docker compose up -d
```

### Connect a debugger

The application container starts with JDWP enabled on port `5005`. In IntelliJ IDEA, create a **Remote JVM Debug** run configuration pointing to `localhost:5005`.

### Connect to the database

The PostgreSQL instance is accessible from the host:

- **Host:** `localhost`
- **Port:** `5432`
- **Database:** `workspace`
- **Username:** `postgres`
- **Password:** `postgres`

## REST API

The application exposes the following endpoints:

| Method | Path                                      | Description                                           |
|--------|-------------------------------------------|-------------------------------------------------------|
| `POST` | `/api/greetings?name={name}`              | Create a greeting                                     |
| `GET`  | `/api/greetings/{id}`                     | Find a greeting by ID                                 |
| `GET`  | `/api/greetings`                          | List all greetings                                    |
| `GET`  | `/api/greetings/search?keyword={keyword}` | Search greetings by keyword                           |
| `GET`  | `/api/greetings/greet`                    | Returns a greeting from `MyService` (auto-configured) |

## Database Migrations

Schema changes are managed with Liquibase. Changelog files are located in `shared/liquibase/src/main/resources/db/changelog/`. During local development with Docker Compose, the `liquibase` service runs migrations before the application starts.

## Code Coverage

[JaCoCo](https://www.jacoco.org/) is configured across the project:

- **Per-module reports** — Each code module produces unit test, integration test, and merged coverage reports under `target/site/`.
- **Aggregated reports** — The `coverage` module generates combined reports for all code modules under `coverage/target/site/jacoco-aggregate-ut/`, `jacoco-aggregate-it/`, and `jacoco-aggregate-merged/`.
- **Dark theme** — A custom CSS from `build-tools` is applied to all JaCoCo HTML reports.
- **Skipping** — Modules without source code (BOMs, parents, shared config) have JaCoCo skipped via the `jacoco.skip` property.

## Monitoring

- **Prometheus** scrapes the application's `/actuator/prometheus` endpoint every 5 seconds.
- **Grafana** is pre-provisioned with a Prometheus datasource and a dashboard (`docker/grafana/dashboards/spring-boot-workspace.json`). Access it at [http://localhost:3000](http://localhost:3000) (default credentials: `admin` / `admin`).

## Architecture Testing

Shared [ArchUnit](https://www.archunit.org/) rules are defined in `shared/archunit-rules` and inherited by all library and application modules through their parent POMs.

## Documentation

PlantUML diagrams are available in the `docs/` directory:

- `module_diagram.puml` — Module structure
- `dependency_diagram.puml` — Module dependencies
- `class_diagram.puml` — Class relationships
- `inheritance_diagram.puml` — Inheritance hierarchy
- `sequence_diagram_create.puml` — Create greeting flow
- `sequence_diagram_find_by_id.puml` — Find by ID flow
- `sequence_diagram_search.puml` — Search flow

