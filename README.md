# MDD (Monde de Dév)

A social network for developers: subscribe to topics, read a news feed, write posts and comment on them.

| Part | Technologies | Folder |
| --- | --- | --- |
| Front-end | Angular 20, TypeScript, Tailwind CSS 4, Angular CDK | `front/` |
| Back-end | Spring Boot 3.5, Java 21, Spring Data JPA | `back/` |
| Database | MySQL 8.4 | Docker container |

## Running with Docker

### Prerequisites

- Docker Desktop (or Docker Engine) with Docker Compose v2

No local installation of MySQL, Java or Node.js is required to run the application.

### 1. Configure the environment variables

Copy the template, then replace the passwords in `.env`:

```bash
cp .env.example .env
```

| Variable | Purpose |
| --- | --- |
| `MYSQL_DATABASE` | Database name |
| `MYSQL_USER` / `MYSQL_PASSWORD` | Account used by the back-end |
| `MYSQL_ROOT_PASSWORD` | MySQL administrator password |
| `MYSQL_PORT` | Port exposed on the host (3306 by default) |
| `JWT_SECRET` | Key that signs the authentication tokens, at least 32 characters (`openssl rand -base64 48`) |

The `.env` file is ignored by Git and must never be committed.

### 2. Start

The whole application (database, back-end, front-end):

```bash
docker compose --profile app up -d --build
```

The database only, to run the back-end and front-end from the IDE:

```bash
docker compose up -d
```

When run from the IDE, the back-end needs these environment variables (credentials from `.env`):

| Variable | Value |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `dev` |
| `SPRING_DATASOURCE_USERNAME` | value of `MYSQL_USER` |
| `SPRING_DATASOURCE_PASSWORD` | value of `MYSQL_PASSWORD` |
| `JWT_SECRET` | value of `JWT_SECRET` |

The back-end refuses to start if `JWT_SECRET` is missing or shorter than 32 characters.

The database URL defaults to `jdbc:mysql://localhost:3306/mdd`.

The front-end, run from the IDE, requires Node.js 22:

```bash
cd front
npm install
npm start
```

The Angular dev server forwards `/api` calls to the back-end on port 9000 (`front/proxy.conf.json`), so the front-end and the API share the same origin, as they do with nginx in Docker.

### 3. Access the application

| Service | URL |
| --- | --- |
| Front-end | http://localhost:4200 |
| API | http://localhost:9000 |
| MySQL | `localhost:3306` |

The front-end is served by nginx, which forwards `/api/` calls to the back-end.

### Useful commands

| Action | Command |
| --- | --- |
| Show service status | `docker compose --profile app ps` |
| Follow back-end logs | `docker compose logs -f back` |
| Stop the services | `docker compose --profile app down` |
| Stop and **delete the database data** | `docker compose --profile app down -v` |

## Database

The schema is created and versioned by Flyway when the back-end starts. Hibernate only validates that the entities match it (`ddl-auto=validate`).

| Location | Content | Loaded |
| --- | --- | --- |
| `back/src/main/resources/db/migration` | Schema (`V1`) and reference topics (`V2`) | Every environment |
| `back/src/main/resources/db/dev-data` | Demo accounts, subscriptions, articles and comments (`R__seed_dev_data.sql`) | `dev` profile only |

A migration that has already run is never edited: every schema change goes through a new `V<n>__<description>.sql` file. The demo data script is repeatable: Flyway replays it when it changes, without creating duplicates.

### Demo accounts (development only)

These accounts only exist with the `dev` profile. They all use the password `Demo!Mdd2026`.

| Username | Email | Subscriptions |
| --- | --- | --- |
| `alice` | `alice@mdd.dev` | Java, Angular, Spring |
| `bob` | `bob@mdd.dev` | Java, JavaScript, DevOps |
| `carol` | `carol@mdd.dev` | None, to show the empty feed |

If your local database was created before Flyway was introduced, reset it once with `docker compose --profile app down -v`.

## Spring profiles

| Profile | Usage |
| --- | --- |
| `dev` | Local development: SQL logged, demo data loaded. Enabled by Docker Compose. |
| `prod` | Production: reduced logging. |

## Tests

The back-end tests run against a real MySQL database started by [Testcontainers](https://testcontainers.com/). They require Java 21 and a running Docker.

```bash
cd back
./mvnw test
```

The front-end tests run with Karma and Jasmine in a headless Chrome:

```bash
cd front
npm run test:ci
```

## Documentation

| Document | Content |
| --- | --- |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Overview, tech stack, code map and architecture rules |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Branches, ticket lifecycle, pull requests and commit conventions |
