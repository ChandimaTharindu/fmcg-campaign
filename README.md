# FMCG Campaign Service

Spring Boot REST API for FMCG campaign/product management with JWT security.
=======
Spring Boot REST API for Admin and Agent campaign journeys.

## Stack
- Java 17
- Spring Boot 3
- Spring Web, Validation, Data JPA, Security
- H2 in-memory DB
- JWT (jjwt)

## Default users
- `admin/admin123` → ROLE_ADMIN
- `agent/agent123` → ROLE_AGENT
=======
- Spring Web + Validation + JPA
- H2 in-memory DB

## Run
```bash
mvn spring-boot:run
```

## Test
```bash
mvn test
```

## API Docs
See `API_DOCUMENTATION.md`.
=======
See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for endpoint contract and examples.
