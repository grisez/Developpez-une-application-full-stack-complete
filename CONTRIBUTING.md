# Contributing

This document describes how to contribute to MDD. To run the project locally, see the [README](README.md). For the code organisation, see [ARCHITECTURE.md](ARCHITECTURE.md).

## Branches

- `main`: stable versions, updated only on release
- `develop`: integration branch, base for all new work
- `feature/<issue number>-<description>`: one branch per ticket, created from the issue on GitHub
  (example: `feature/22-git-workflow`)

`main` and `develop` are protected: every change goes through a pull request.

## Ticket lifecycle

Backlog → Ready → In progress → In review → Done

1. Move the card to In progress and create the branch from the issue (Development section), based on `develop`
2. Develop, then open a pull request to `develop` with `Closes #<number>`: the card moves to In review
3. Review and test, then merge: the issue closes and the card moves to Done

## Pull requests

- Fill in the pull request template and tick the Definition of Done before asking for a review.

## Commits

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
