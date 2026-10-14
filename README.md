# MDD (Monde de Dév)

A social network for developers: subscribe to topics, read a news feed, write posts and comment on them.

| Part | Technologies | Folder |
| --- | --- | --- |
| Front-end | Angular 20, TypeScript | `front/` |
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

When run from the IDE, the back-end must receive the `.env` credentials as environment variables: `SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/mdd`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD`.

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

## Git workflow

### Branches
- `main`: stable versions, updated only on release
- `develop`: integration branch, base for all new work
- `feature/<issue number>-<description>`: one branch per ticket, created from the issue on GitHub
  (example: `feature/22-git-workflow`)

`main` and `develop` are protected: every change goes through a pull request.

### Ticket lifecycle
Backlog → Ready → In progress → In review → Done
1. Move the card to In progress and create the branch from the issue (Development section), based on `develop`
2. Develop, then open a pull request to `develop` with `Closes #<number>`: the card moves to In review
3. Review and test, then merge: the issue closes and the card moves to Done

### Commits
[Conventional Commits](https://www.conventionalcommits.org/) format: `<type>(<optional scope>): <description>`

| Type | Usage |
| --- | --- |
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation |
| `build` | Docker, dependencies, build |
| `test` | Tests |
| `refactor` | Restructuring without behaviour change |
| `chore` | Maintenance |
