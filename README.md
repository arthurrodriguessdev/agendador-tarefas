# Agendador de Tarefas

REST API for creating and managing scheduled tasks. This repository contains the task service only; authentication/user management and email delivery belong to collaborating services in the larger system.

## Architecture

```text
BFF / API client
      |
      v
Agendador de Tarefas ---- HTTP/JWT ----> User service (external)
      |          |
      |          +---- JSON event ----> Kafka: tarefa-criada
      |                                      |
      v                                      v
  PostgreSQL                         Notification service (external)
```

The task service validates JWTs using the external user service, persists tasks in PostgreSQL, and publishes a JSON representation of a created `TarefaDTO` to Kafka. The notification consumer is not part of this repository.

### Kafka contract

| Item | Current behavior |
| --- | --- |
| Topic | `tarefa-criada` (created with one partition and one replica) |
| Producer | This service, after saving a task |
| Value | JSON serialization of `TarefaDTO`: `nomeTarefa`, `descricaoTarefa`, `dataCriacao`, `dataEvento`, `emailUsuario`, `status` |
| Consumer | External notification service; its group ID and deserialization settings are configured there |

The producer uses `acks=all` and idempotence for broker-level producer retries. This does not make the database write and Kafka publish atomic, and it does not provide end-to-end exactly-once delivery. A publish failure is logged, but task creation still returns `201` after the database save; there is no transactional outbox, application-level retry, or dead-letter topic in this service. The event contract and topic name are kept compatible with the existing notification integration.

## Technology

- Java 21 and Spring Boot
- Spring MVC, Spring Security, Spring Data JPA, and OpenFeign
- PostgreSQL
- Apache Kafka
- Gradle
- Docker and Docker Compose

## Run locally with Docker Compose

Prerequisites: Docker Engine/Desktop with the Compose plugin, and a running user/authentication service reachable from the task-service container.

1. Copy `.env.example` to `.env`.
2. Replace `SECRET_KEY_JWT` with a random Base64-encoded key of at least 32 bytes. For example, with OpenSSL: `openssl rand -base64 32`.
3. Set `USUARIO_URL` to the user service URL reachable from Docker. `host.docker.internal` is provided as a host-gateway alias by the Compose configuration.
4. Start the stack: `docker compose up --build -d`.
5. Follow application logs: `docker compose logs -f agendador-tarefas`.
6. Stop the stack: `docker compose down`. Add `-v` only when you intentionally want to delete the local PostgreSQL data volume.

Compose starts this service, PostgreSQL, and a single-node Kafka broker. Kafka is reachable at `localhost:9092` from the host by default and `kafka:29092` from containers. If host port `9092` is already in use, set `KAFKA_EXTERNAL_PORT=9093` (or another available port) in `.env`; the in-network broker address remains unchanged. PostgreSQL and Kafka data are kept in named volumes. The single-node Kafka and PostgreSQL credentials are for local development only; do not expose these ports or reuse local values in production.

To run the Spring Boot app outside Docker, provide `SPRING_DATASOURCE_PASSWORD` and `SECRET_KEY_JWT` in the environment. Local defaults point to PostgreSQL on `localhost:5432`, Kafka on `localhost:9092`, the user service on `localhost:8080`, and the API on port `8081`.

## Configuration

| Variable | Purpose | Default |
| --- | --- | --- |
| `SPRING_DATASOURCE_URL` | JDBC connection URL | `jdbc:postgresql://localhost:5432/db_agendador` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | Required |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker addresses | `localhost:9092` |
| `USUARIO_URL` | Base URL of the external user service | `http://localhost:8080` |
| `SECRET_KEY_JWT` | Base64-encoded JWT signing/verification key | Required |
| `SERVER_PORT` | HTTP port | `8081` |
| `JPA_DDL_AUTO` | Hibernate schema handling | `update` |
| `JPA_SHOW_SQL` | SQL statement logging | `false` |

`JPA_DDL_AUTO=update` is convenient for development; use managed schema migrations and a non-`update` setting for production deployments.

## API

Base URL: `http://localhost:8081`. All routes require a valid `Authorization: Bearer <token>` header.

| Method | Path | Behavior |
| --- | --- | --- |
| `POST` | `/tarefas` | Create a task and publish `tarefa-criada`; returns `201` |
| `GET` | `/tarefas` | List tasks belonging to the authenticated user |
| `GET` | `/tarefas/eventos?dataInicio=2026-10-25T00:00:00&dataFim=2026-10-26T00:00:00` | List that user's tasks in the inclusive date/time range |
| `PUT` | `/tarefas/{id}` | Update a task belonging to the authenticated user |
| `PATCH` | `/tarefas/{id}?status=PENDENTE` | Change a task status (`PENDENTE`, `NOTIFICADO`, or `CANCELADO`) |
| `DELETE` | `/tarefas/{id}` | Delete a task belonging to the authenticated user |

Request and response bodies use `TarefaDTO`; date/time fields are formatted as `dd-MM-yyyy HH:mm:ss`. The authenticated user's email is taken from the JWT rather than trusted from the request body.

## Build and tests

- Run tests and build: `./gradlew build` (Windows: `.\gradlew.bat build`)
- Run tests only: `./gradlew test` (Windows: `.\gradlew.bat test`)
- Build the container image: `docker build -t agendador-tarefas .`

## Known boundaries

- The user/authentication and notification services are external dependencies and are not started by this Compose file.
- Kafka uses a single broker, one topic partition, and one replica for local development; this is not a highly available production topology.
- Database persistence and Kafka publication are separate operations. Use an outbox and consumer-side idempotency if reliable event delivery across service/database failures becomes a production requirement.
- There is no consumer in this service; notification consumer-group, retry, and dead-letter behavior must be implemented and documented by the notification service.
