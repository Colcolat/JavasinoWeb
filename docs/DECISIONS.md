# Architectural Decision Records (ADR)

This document tracks all major technical decisions made throughout the project, including the reasoning behind each choice.

---

## ADR-001 — Package by Feature over Package by Layer

**Date:** 2026-05-23
**Status:** Accepted

**Context:**
The project needed a package structure. The default approach in Spring Boot tutorials is Package by Layer (`controllers/`, `services/`, `repositories/` at the root level).

**Decision:**
Use **Package by Feature** (`user/`, `wallet/`, `games/blackjack/`, etc.), with layered sub-packages inside each feature module.

**Reasoning:**
- Accenture works on enterprise projects where teams scale and modules can evolve into microservices. Package by Feature mirrors that thinking.
- Each feature module is self-contained — easier to maintain, test, and eventually extract.
- Demonstrates domain-driven thinking in interviews, not just "I followed a tutorial."

**Structure inside each module:**
```
user/
├── controller/
├── dto/
├── model/
├── repository/
└── service/
```

---

## ADR-002 — Spring Boot with Spring Web (MVC)

**Date:** 2026-05-23
**Status:** Accepted

**Context:**
Backend framework selection for the REST API.

**Decision:**
Use **Spring Boot + Spring Web (MVC)** with a RESTful API architecture.

**Reasoning:**
- Spring Boot is the industry standard for Java backend development at enterprise companies like Accenture.
- Spring MVC is well-understood, well-documented, and aligns with the internship role requirements.
- IoC/DI, Controller/Service/Repository pattern, and annotations (e.g., `@RestController`, `@Service`, `@Repository`) are core concepts already studied.

---

## ADR-003 — PostgreSQL as the Database

**Date:** 2026-05-23
**Status:** Accepted

**Context:**
Database selection for persistence.

**Decision:**
Use **PostgreSQL**.

**Reasoning:**
- Already being studied at university.
- Best-in-class support on **AWS RDS**, which opens the door to a future cloud deployment.
- The layered architecture chosen (ADR-001) makes a future migration to AWS RDS straightforward — no structural changes needed, only configuration.

---

## ADR-004 — Spring Profiles for Dev/Prod Environments

**Date:** 2026-05-23
**Status:** Accepted

**Context:**
The project needs to run differently in local development vs. a production environment.

**Decision:**
Use **Spring Profiles** with separate property files:
- `application.properties` — shared base config (no sensitive data)
- `application-dev.properties` — local development (not committed to Git)
- `application-prod.properties` — production (not committed to Git)
- `application-dev.properties.example` — committed as a setup template

**Reasoning:**
- Demonstrates awareness of the full software lifecycle beyond "make it work locally."
- Separating environments is standard practice at enterprise companies.
- Prevents accidental exposure of credentials in version control.

---

## ADR-005 — Credentials Never Committed to Git

**Date:** 2026-05-23
**Status:** Accepted

**Decision:**
Both `application-dev.properties` and `application-prod.properties` are listed in `.gitignore`. A `.example` template is committed instead, documenting the required keys without exposing values.

**Reasoning:**
- Security best practice — credentials belong in environment variables or secret managers, not in source control.
- Signals professional awareness of security to any reviewer of the public repository.

---

## ADR-006 — Documentation in Markdown (not Word)

**Date:** 2026-05-23
**Status:** Accepted

**Decision:**
All project documentation lives in `/docs` as `.md` files committed to the repository.

**Reasoning:**
- GitHub renders Markdown natively — no downloads required for reviewers.
- Documentation is versioned alongside the code, making the decision history traceable.
- Industry standard for open-source and enterprise projects.
- A Word document can be generated later from these files if needed for a portfolio.

---

## ADR-007 — AI-Assisted Frontend Development

**Date:** 2026-05-23
**Status:** Accepted

**Context:**
The project includes game UIs (Blackjack, Roulette, Bingo, Horse Racing, Poker) that require interactive interfaces. Deep frontend expertise is outside the current skill set.

**Decision:**
Use AI tooling to generate and iterate on the frontend (HTML/CSS/JS), while the developer owns the backend architecture, game logic, and API design fully.

**Reasoning:**
- Reflects how modern developers actually work — knowing when and how to leverage AI is a valued skill.
- Allows focus on backend depth (Java, Spring Boot, PostgreSQL) which is the primary goal for the Accenture role.
- The resulting UI can still be understood, explained, and maintained by the developer.