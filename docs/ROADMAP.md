# Project Roadmap

This document outlines the planned phases of the JavasinoWeb project, from initial planning to a production-ready state.

---

## Phase 1 — Planning *(current)*

**Goal:** Define the full architecture, data model, and feature scope before writing any business logic.

- [x] Choose package structure (Package by Feature)
- [x] Set up IntelliJ project with Gradle
- [x] Configure Spring Profiles (dev / prod)
- [x] Set up `.gitignore` for sensitive files
- [x] Initialize GitHub repository with clean first commit
- [x] Create `/docs` structure with Markdown documentation
- [ ] Define entity diagram (User, Wallet, Game, Transaction, etc.)
- [ ] Define REST API contract (endpoints per module)
- [ ] Define game logic rules for each game

---

## Phase 2 — Core Backend

**Goal:** Implement authentication, user management, and the wallet system.

- [ ] Set up PostgreSQL connection (dev profile)
- [ ] Implement `User` entity + registration + login (Spring Security / JWT)
- [ ] Implement `Wallet` entity with deposit/withdraw logic
- [ ] Unit tests for User and Wallet services (JUnit 5 + Mockito)
- [ ] API documentation (Swagger / OpenAPI)

---

## Phase 3 — Games Implementation

**Goal:** Implement each game module with its own logic and API endpoints.

Order of implementation (by complexity):
1. [ ] **Blackjack** — card comparison logic, win/loss/push conditions
2. [ ] **Roulette** — bet types, spin logic, payout calculations
3. [ ] **Bingo** — card generation, number draw, win detection
4. [ ] **Horse Racing** — race simulation, odds, bet resolution
5. [ ] **Poker** — hand evaluation, betting rounds *(last, most complex)*

Each game module includes:
- [ ] Game entity/model
- [ ] Service with game logic
- [ ] REST controller
- [ ] Integration with Wallet (debit/credit on result)
- [ ] Unit + integration tests

---

## Phase 4 — Frontend

**Goal:** Build a functional, presentable UI for each game.

- [ ] Set up frontend project (framework TBD)
- [ ] Authentication screens (login / register)
- [ ] Dashboard with wallet balance
- [ ] UI for each game (AI-assisted generation + developer review)
- [ ] Connect frontend to Spring Boot REST API

---

## Phase 5 — Testing & Quality

**Goal:** Ensure the project is stable, tested, and ready for public review.

- [ ] Unit tests for all services
- [ ] Integration tests for all controllers
- [ ] End-to-end tests for key user flows
- [ ] Code review pass (clean code, no hardcoded values, proper error handling)
- [ ] Update all documentation

---

## Phase 6 — Deployment *(stretch goal)*

**Goal:** Deploy to a public URL for portfolio purposes.

- [ ] Configure `application-prod.properties` with environment variables
- [ ] Containerize with Docker
- [ ] Deploy backend to AWS (EC2 or Elastic Beanstalk)
- [ ] Configure AWS RDS (PostgreSQL)
- [ ] Deploy frontend (S3 + CloudFront or Vercel)
- [ ] Monitor with AWS CloudWatch

> **Note:** Phase 6 is a stretch goal. The architecture choices made in Phase 1 (Package by Feature, Spring Profiles, PostgreSQL) are intentionally aligned with making this migration as smooth as possible.



