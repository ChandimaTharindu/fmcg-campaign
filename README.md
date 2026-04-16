# FMCG Campaign Service

Spring Boot REST API for Admin and Agent campaign journeys with JWT-based security.

## Stack
- Java 17
- Spring Boot 3
- Spring Web + Validation + JPA + Security
- H2 in-memory DB
- JWT (jjwt)

## Default Login Users
- `admin / admin123` (ROLE_ADMIN)
- `agent / agent123` (ROLE_AGENT)

## Run
```bash
mvn spring-boot:run
```

## Test
```bash
mvn test
```

## API Docs
See [API_DOCUMENTATION.md](API_DOCUMENTATION.md).
