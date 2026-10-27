# Architecture

This document describes how the MDD code base is organised and the rules every change must follow.

## Overview

```mermaid
flowchart LR
    subgraph Client["User device"]
        B["Browser<br/>Angular application"]
    end
    subgraph Compose["Docker Compose"]
        N["nginx<br/>front container"]
        subgraph Back["back container: Spring Boot"]
            SEC["Spring Security<br/>JWT filter"] --> CTRL["Controllers"] --> SRV["Services"] --> REPO["Repositories"]
        end
        DB[("MySQL")]
    end
    B -->|"1. loads the application"| N
    B -->|"2. /api calls, JSON + JWT"| N
    N -->|"proxy /api"| SEC
    REPO -->|"JDBC"| DB
```

- The Angular application runs in the browser. nginx serves it and forwards every `/api` call to the back-end. In development, the Angular dev server plays the same role through `proxy.conf.json`.
- The back-end exposes a JSON REST API secured by JWT.
- MySQL stores the data. Flyway creates and evolves the schema.

## Tech stack

| Area | Tools |
| --- | --- |
| Front-end | Angular 20 (standalone components, esbuild builder), TypeScript 5.9, Angular CLI, RxJS, Signals, Tailwind CSS 4, Angular CDK (accessibility) |
| Back-end | Java 21, Spring Boot 3.5 (Web, Data JPA, Security, Validation), Lombok, Maven |
| Database | MySQL 8.4, Flyway |
| Security | JWT (Spring Security OAuth2 Resource Server), Argon2id password hashing |
| API documentation | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, Spring Boot Test, Karma and Jasmine, Cypress |
| Code quality | ESLint, Prettier, SonarQube, JaCoCo (back-end coverage) |
| Infrastructure | Docker, Docker Compose, nginx |
| CI and project management | GitHub Actions, GitHub Projects |
| API testing | Postman |

## Code map

### Back-end (`back/`)

```
src/main/java/com/openclassrooms/mddapi/
├── config/        cross-cutting configuration (CORS, OpenAPI)
├── controller/    REST controllers: HTTP layer, input validation, DTOs in and out
├── dto/           request and response objects exchanged with the front-end
├── exception/     business exceptions and the global error handler
├── mapper/        entity ↔ DTO conversion (MapStruct)
├── model/         JPA entities
├── repository/    Spring Data JPA repositories
├── security/      Spring Security configuration and JWT handling
└── service/       business rules: interfaces, implementations in service/impl/

src/main/resources/
├── application.properties
└── db/
    ├── migration/     Flyway migrations, all environments
    └── dev-data/      demo data, loaded with the dev profile only
```

A request goes through: JWT filter → controller → service → repository → MySQL.

### Front-end (`front/`)

```
src/app/
├── core/        app-wide services, HTTP interceptor, route guards, models
├── features/    one folder per feature: auth, feed, topics, posts, profile
└── shared/      reusable UI components
```

Files follow the Angular 20 naming convention: a component lives in `name.ts` with its template in `name.html` (class `Name`). Services keep the `.service.ts` suffix so they never clash with the model of the same name. Routes are lazy loaded.

## Session

| Aspect | Choice |
| --- | --- |
| Token | JWT signed in HS256, holding the user id, valid for **7 days** (`mdd.security.jwt.expiration`) |
| Storage | `mdd_token` cookie: `HttpOnly`, `SameSite=Strict`, `Secure` in production, limited to `/api`, with the same 7-day lifetime |
| Persistence | The cookie outlives the browser session. At startup, the front-end calls `GET /api/auth/me` before the first navigation to restore the logged-in user |
| Route protection | `authGuard` sends visitors to `/login`; `guestGuard` sends logged-in users from the home, login and registration pages to `/feed` |
| Expiration | The browser deletes the cookie when it expires. Any 401 answer then clears the user and opens `/login` (`errorInterceptor`) |

The token is kept in an HttpOnly cookie rather than in `localStorage`: JavaScript can read `localStorage`, so a single XSS flaw would let an attacker steal the token. The browser sends the cookie on its own, which is why every state-changing request is protected against CSRF.

## Architecture rules

- Controllers only return DTOs, never JPA entities.
- Business rules live in services, never in controllers.
- Every route requires a JWT, except `/api/auth/register`, `/api/auth/login` and `/api/auth/logout`. These three ignore the token cookie, so that an invalid token never prevents a user from logging in or out.
- The JWT travels in an HttpOnly cookie that JavaScript cannot read. Every state-changing request must carry the CSRF token: Angular copies the `XSRF-TOKEN` cookie into the `X-XSRF-TOKEN` header.
- Passwords are hashed with Argon2id, never stored or logged in plain text.
- The schema only changes through a new Flyway migration. A migration that has already run is never edited.
- Demo data is only loaded with the `dev` profile.
- Secrets come from environment variables, never from the code.
- Errors are handled in one place and never expose internal details.

## Design patterns

| Pattern | Where | Purpose |
| --- | --- | --- |
| Layered architecture | controller → service → repository | Each layer has a single responsibility |
| Dependency injection | Spring, constructor injection (`@RequiredArgsConstructor`) | Classes receive their dependencies, which makes them easy to test with mocks |
| Repository | `repository/` (Spring Data JPA) | Isolates data access |
| DTO | `dto/` | The API never exposes JPA entities |
| Mapper | `mapper/` (MapStruct) | Entity to DTO conversion generated at compile time |
| Strategy | `CookieBearerTokenResolver`, `PasswordEncoder`, `ProblemDetailSecurityHandler` | Implementations plugged into Spring Security without changing the framework: read the token from a cookie, hash with Argon2id, answer 401 and 403 errors as problem details |

## Testing strategy

| Level | Back-end | Front-end |
| --- | --- | --- |
| Unit | Services with JUnit 5 and Mockito, real MapStruct mappers | Components, services, guards, interceptor, directive and validators with Jasmine |
| Web layer | Controllers and security rules with `@WebMvcTest` and MockMvc | — |
| Integration | Whole application against a real MySQL started by Testcontainers: authentication, subscriptions, profile, articles, comments, demo data | HTTP calls checked with `HttpTestingController` |
| End-to-end | Cypress, through the user journey in a real browser | |

Rules followed by every test:

- The name states the expected behaviour, for example `loginRejectsAWrongPassword` or `should log in and open the feed`.
- Each test follows the Arrange / Act / Assert structure, marked with comments.
- Tests are isolated: mocks for unit tests, unique data per integration test, a disposable MySQL container.
- A bug found by a test is fixed together with the test that reproduces it, so that it cannot come back.
- The CSRF protection is checked with real token cookies: the `jwt()` helper of spring-security-test disables it on the simulated request.

The front-end keeps Karma and Jasmine, the test stack generated and supported by the Angular CLI 20 builder. Jest is only available as an experimental builder in Angular 20; migrating would add risk without a functional gain for this project.
