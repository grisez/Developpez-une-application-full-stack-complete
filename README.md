# MDD (Monde de Dév)

[![CI](https://github.com/grisez/Developpez-une-application-full-stack-complete/actions/workflows/ci.yml/badge.svg?branch=develop)](https://github.com/grisez/Developpez-une-application-full-stack-complete/actions/workflows/ci.yml)

A social network for developers: subscribe to topics, read a news feed, write posts and comment on them.

| Part | Technologies | Folder |
| --- | --- | --- |
| Front-end | Angular 20, TypeScript, Tailwind CSS 4, Angular CDK | `front/` |
| Back-end | Spring Boot 3.5, Java 21, Spring Data JPA | `back/` |
| Database | MySQL 8.4 | Docker container |

## Project structure

```text
.
├── back/                  Spring Boot API
│   └── src/main/java/…    controller, service, repository, model, dto, mapper, security, exception, …
├── front/                 Angular application
│   ├── src/app/core/      services, guards, interceptor, models
│   ├── src/app/features/  one folder per page
│   ├── src/app/shared/    reusable components, directive, validators
│   └── cypress/           end-to-end tests
├── .github/workflows/     CI on GitHub Actions
├── docker-compose.yml     MySQL, back-end and front-end
└── .env.example           template of the environment variables
```

The detailed code map and the architecture rules are in [ARCHITECTURE.md](ARCHITECTURE.md).

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
| `prod` | Production: reduced logging, API documentation disabled. |

## Deployment

The application is not deployed: this section describes how it would be put into production with Docker Compose on a single server.

### Server prerequisites

- A Linux server (2 vCPU, 2 GB of RAM are enough for the MVP) with Docker Engine and Docker Compose v2
- A domain name pointing to the server, for the HTTPS certificate
- A firewall that only lets SSH (22), HTTP (80) and HTTPS (443) in

### Steps

1. Check out the released version: `git clone` the repository, then `git checkout v1.0.0`.
2. Create `.env` from `.env.example` with new, random secrets (`openssl rand -base64 48`), then restrict it to its owner: `chmod 600 .env`.
3. Back up the database if it already exists (see the production rules below).
4. Build and start: `docker compose -f docker-compose.prod.yml up -d --build`.
5. Check that the services are healthy: `docker compose -f docker-compose.prod.yml ps`, then the logs of the back-end. Flyway applies the new migrations on startup.
6. Open the application over HTTPS and log in to check the main journey.

### `docker-compose.prod.yml`

The production file is not part of the repository. It would differ from `docker-compose.yml` as follows:

```yaml
services:
  db:
    image: mysql:8.4
    restart: always
    env_file: .env
    # No "ports": the database is only reachable by the back-end, on the internal Docker network
    volumes:
      - mdd-db-data:/var/lib/mysql
    # healthcheck: same as in docker-compose.yml

  back:
    image: mdd-back:1.0.0            # Versioned image, so that the previous version can be restarted
    build: ./back
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod   # No demo data, no Swagger, reduced logging, Secure cookie
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/${MYSQL_DATABASE}
      SPRING_DATASOURCE_USERNAME: ${MYSQL_USER}
      SPRING_DATASOURCE_PASSWORD: ${MYSQL_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    # No "ports": nginx reaches the API through the internal network
    depends_on:
      db:
        condition: service_healthy

  front:
    image: mdd-front:1.0.0
    build: ./front
    restart: always
    ports:
      - "80:80"
      - "443:443"                    # nginx terminates HTTPS with a Let's Encrypt certificate
    volumes:
      - /etc/letsencrypt:/etc/letsencrypt:ro

volumes:
  mdd-db-data:
```

The nginx configuration would also redirect HTTP to HTTPS and listen on 443 with the certificate.

### Development and production

| | Development | Production |
| --- | --- | --- |
| Compose file | `docker-compose.yml` | `docker-compose.prod.yml` |
| Spring profile | `dev` | `prod` |
| Demo data | Loaded | Never loaded |
| Swagger UI | Enabled | Disabled |
| Logs | SQL queries displayed | Warnings only |
| Exposed ports | 4200, 9000, 3306 | 80 and 443 only |
| HTTPS | No | Yes, required by the `Secure` session cookie |
| Images | Built locally | Versioned tags (`1.0.0`) |
| Restart policy | `unless-stopped` | `always` |

### Production rules

- **Secrets**: only in the server's `.env`, never in Git or in the images. Changing `JWT_SECRET` logs every member out, which is the way to revoke all sessions.
- **Closed ports**: MySQL and the API are never exposed; only nginx is reachable from outside.
- **Backups**: a daily `mysqldump` of the database, copied to another location, and a restore tested regularly.
- **Rollback**: restart the previous image tag (`mdd-back:<previous version>`). Flyway migrations only go forward, so a release that changed the schema is rolled back by restoring the backup taken before the deployment.

## API

The interactive documentation is available outside production at http://localhost:9000/swagger-ui.html, and the OpenAPI description at http://localhost:9000/v3/api-docs. To try a protected endpoint, call `POST /api/auth/login` first: the authentication cookie is then sent with the next requests.

Every endpoint requires the authentication cookie, except register, login and logout. Every `POST`, `PUT` and `DELETE` request must carry the CSRF token in the `X-XSRF-TOKEN` header. Errors follow RFC 9457 (problem details).

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/auth/register` | Create an account and log in |
| `POST` | `/api/auth/login` | Log in with an email or a username |
| `POST` | `/api/auth/logout` | Log out |
| `GET` | `/api/auth/me` | Current user, to restore the session |
| `GET` | `/api/me` | My profile and subscriptions |
| `PUT` | `/api/me` | Update my email, username or password |
| `GET` | `/api/topics` | All topics, with my subscription state |
| `POST` | `/api/topics/{id}/subscribe` | Subscribe to a topic |
| `DELETE` | `/api/topics/{id}/subscribe` | Unsubscribe from a topic |
| `GET` | `/api/articles?sort=desc\|asc` | News feed: articles of my topics |
| `GET` | `/api/articles/{id}` | An article with its comments |
| `POST` | `/api/articles` | Publish an article |
| `POST` | `/api/articles/{id}/comments` | Comment on an article |

## Tests

Every pull request and every push to `develop` or `main` runs the [CI workflow](.github/workflows/ci.yml) on GitHub Actions: back-end build and tests with the coverage check, front-end lint, build and tests. The coverage reports are attached to each run as artifacts.

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

The end-to-end tests run with [Cypress](https://www.cypress.io/) against the whole application: start the back-end and the front-end (`npm start`) first, then:

```bash
cd front
npm run e2e
```

`npm run e2e:open` opens the Cypress interface to follow the tests step by step. Each run registers a new account, so the tests can be replayed on the same database.

The screenshots of the [UI/UX annex](UI-UX.md) are generated the same way, on desktop and mobile, into `docs/screenshots/`. The back-end must run with the `dev` profile, as they use the demo account `alice`:

```bash
cd front
npm run screenshots
```

## Code quality

| Tool | Command | Result |
| --- | --- | --- |
| JaCoCo (back-end coverage) | `cd back && ./mvnw verify` | Report in `back/target/site/jacoco/index.html`; the build fails below 70% of lines covered |
| Karma coverage (front-end) | `cd front && npm run test:coverage` | Report in `front/coverage/mdd-client/index.html` |
| ESLint with angular-eslint (front-end) | `cd front && npm run lint` | TypeScript, Angular and template accessibility rules |
| SonarLint (back-end) | IntelliJ plugin: right-click on `back/src`, then **Analyze with SonarQube for IDE** | Code smells and bugs reported in the IDE |

## Documentation

| Document | Content |
| --- | --- |
| [ARCHITECTURE.md](ARCHITECTURE.md) | Overview, tech stack, code map and architecture rules |
| [CONTRIBUTING.md](CONTRIBUTING.md) | Branches, ticket lifecycle, pull requests and commit conventions |
| [TEST-REPORT.md](TEST-REPORT.md) | Test results, coverage and anomalies fixed |
| [COMPLIANCE.md](COMPLIANCE.md) | Legal notice, privacy policy, cookies and security of the logs |
| [FAQ.md](FAQ.md) | User FAQ, in French like the application: account, topics, articles, profile, common errors |
| [UI-UX.md](UI-UX.md) | Screen-by-screen analysis with screenshots, user journeys, responsive and accessibility choices; screenshots regenerated with `npm run screenshots` |
