# LabFlow Lite

![CI](https://github.com/hperumalla-oops/labflow-lite/actions/workflows/ci.yml/badge.svg)

REST API for lab result approval. Technician enters result. Reviewer approves or rejects. Every state change audited. Creator cannot approve own result (maker-checker).

**Stack:** Java 21, Spring Boot, PostgreSQL, Liquibase, Spring Security + JWT, Docker Compose, Testcontainers, GitHub Actions

## Run
```bash
docker compose up -d --build
```
Swagger UI: http://localhost:8080/swagger-ui.html
Dev users (password `password123`): `tech1`, `reviewer1`, `reviewer2`

## Workflow
DRAFT → SUBMITTED → APPROVED | REJECTED

## Design decisions
1. **Workflow in a table, not `if` statements.** `result_transitions` holds legal moves per role. New rule = new row, no code change.
2. **Maker-checker enforced in service layer.** Creator cannot approve or reject own result.
3. **Audit in same transaction as state change.** `Propagation.MANDATORY` on audit writes. Both commit or both roll back.
4. **Optimistic locking (`@Version`).** Two concurrent approvals: one wins, loser rolls back. Covered by integration test.
5. **Liquibase owns schema.** Hibernate `ddl-auto: validate` only checks. No silent drift.
6. **Stateless JWT auth.** Roles in token. `@PreAuthorize` plus table-driven role checks.
7. **Consistent errors.** `@RestControllerAdvice` returns RFC 7807 ProblemDetail.
8. **Package by feature.** `auth`, `results`, `audit`, `common`.

## Tests
```bash
./gradlew test
```
Testcontainers runs real Postgres. Covers happy path, illegal transition, role denial, self-approval, concurrent approval.


## Production gaps
- Dev seed users. Replace with real onboarding.
- JWT secret from env var. Use secret manager.
- No rate limiting or token revocation.