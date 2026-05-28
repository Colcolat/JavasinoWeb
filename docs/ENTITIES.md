# Entity Diagram & Data Model

This document defines all database entities, their attributes, relationships, and implementation phases.

---

## Non-database classes (shared/model/)

These are pure Java classes — they are **not persisted** in the database. They exist to encapsulate reusable game logic.

| Class | Location | Responsibility |
|-------|----------|----------------|
| `Card` | `shared/model/Card.java` | Represents a single card: suit (♠♥♦♣) and value (A, 2...K) |
| `Deck` | `shared/model/Deck.java` | Holds 52 cards, handles shuffling and dealing |

**Why separate?** Single Responsibility Principle — `Card` knows what it *is*, `Deck` knows what to *do* with cards. Both are reused by Blackjack and Poker without duplication.

---

## Core entities

### USER
Central entity. Every player must be registered.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `username` | String | Unique |
| `email` | String | Unique, used for login |
| `password` | String | Stored hashed (BCrypt) |
| `role` | Enum | `PLAYER`, `ADMIN` |

---

### WALLET
One wallet per user. Tracks current balance.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `balance` | Long | Integer credits (no decimals) |
| `user_id` | UUID | FK → USER (1:1) |

**Why Long?** Casino credits are large integers — no decimal precision needed.

---

### TRANSACTION
Every credit movement is recorded here for full audit history.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `amount` | Long | Amount moved |
| `type` | Enum | `DEPOSIT`, `WITHDRAWAL`, `BET`, `WIN` |
| `game_type` | Enum | `BLACKJACK`, `ROULETTE`, `BINGO`, `HORSES`, `POKER`, `NONE` |
| `created_at` | Timestamp | When it happened |
| `wallet_id` | UUID | FK → WALLET |

**Design note:** Win/loss counters are not stored on USER — they are derived from TRANSACTION records to avoid data inconsistency.

---

## Game session entities

### GAME_SESSION (generic)
Shared session entity used by Blackjack. Stores the high-level result of any game round.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `user_id` | UUID | FK → USER |
| `game_type` | Enum | Which game was played |
| `bet_amount` | Long | How much was wagered |
| `result` | Enum | `WIN`, `LOSS`, `DRAW` |
| `status` | Enum | `IN_PROGRESS`, `FINISHED` |
| `created_at` | Timestamp | When the session occurred |

---

### BLACKJACK_SESSION
Stores the card detail of a Blackjack round. Extends GAME_SESSION.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `player_cards` | String | Serialized (e.g. `"AS,KH,7D"`) |
| `dealer_cards` | String | Serialized |
| `player_score` | Int | Final hand value |
| `dealer_score` | Int | Final hand value |
| `session_id` | UUID | FK → GAME_SESSION |

---

### ROULETTE_SESSION
Stores the result of one roulette spin.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `user_id` | UUID | FK → USER |
| `winning_number` | Int | 0–36 |
| `result` | Enum | `WIN`, `LOSS` |
| `payout` | Long | Total credits won or lost |
| `created_at` | Timestamp | |

---

### ROULETTE_BET
One or more bets placed per roulette spin. A player can bet on multiple outcomes in a single round.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `bet_type` | Enum | `NUMBER`, `COLOR`, `EVEN_ODD`, `DOZEN` |
| `bet_value` | String | e.g. `"17"`, `"RED"`, `"EVEN"` |
| `amount` | Long | Amount placed on this bet |
| `session_id` | UUID | FK → ROULETTE_SESSION |

**Why separate from ROULETTE_SESSION?** A player can place multiple simultaneous bets per spin. Storing them as a list requires a separate entity.

---

### BINGO_SESSION
Stores one Bingo game (player vs machine).

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `user_id` | UUID | FK → USER |
| `drawn_numbers` | String | Numbers revealed during the game (serialized) |
| `bet_amount` | Long | Entry wager |
| `result` | Enum | `WIN`, `LOSS` |
| `status` | Enum | `IN_PROGRESS`, `FINISHED` |
| `payout` | Long | Credits won or lost |
| `created_at` | Timestamp | |

---

### BINGO_CARD
The player's card for a Bingo session.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `numbers` | String | 25 numbers serialized (e.g. `"5,12,23,44,61..."`) |
| `user_id` | UUID | FK → USER |
| `session_id` | UUID | FK → BINGO_SESSION |

**Game mode:** Single player vs machine (Option A). Machine draws numbers until the player completes their card or a draw limit is reached.

---

### HORSE
Represents a horse available in races. Static catalog — not generated per session.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `name` | String | e.g. `"Thunder"`, `"Shadow"` |
| `odds` | Float | Probability — affects payout calculation |

---

### HORSE_RACE_SESSION
Stores the result of one race.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `user_id` | UUID | FK → USER |
| `first_place` | UUID | FK → HORSE |
| `second_place` | UUID | FK → HORSE |
| `third_place` | UUID | FK → HORSE |
| `result` | Enum | `WIN`, `LOSS` |
| `payout` | Long | |
| `created_at` | Timestamp | |

---

### HORSE_RACE_BET
One or more bets placed per race. Supports WIN / PLACE / SHOW bet types.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `horse_id` | UUID | FK → HORSE |
| `amount` | Long | Amount wagered |
| `bet_type` | Enum | `WIN` (1st), `PLACE` (top 2), `SHOW` (top 3) |
| `session_id` | UUID | FK → HORSE_RACE_SESSION |

---

### POKER_SESSION
Stores one Texas Hold'em hand (player vs dealer).

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `user_id` | UUID | FK → USER |
| `hand_id` | UUID | FK → POKER_HAND |
| `initial_bet` | Long | Opening wager |
| `total_pot` | Long | Total accumulated across betting rounds |
| `result` | Enum | `WIN`, `LOSS`, `FOLD` |
| `status` | Enum | `IN_PROGRESS`, `FINISHED` |
| `payout` | Long | |
| `created_at` | Timestamp | |

---

### POKER_HAND
Stores the card detail of a Poker hand.

| Field | Type | Notes |
|-------|------|-------|
| `id` | UUID | Primary key |
| `player_cards` | String | 2 hole cards (serialized) |
| `dealer_cards` | String | 2 dealer cards (serialized) |
| `community_cards` | String | 5 community cards — flop, turn, river (serialized) |
| `player_hand_rank` | Enum | `PAIR`, `TWO_PAIR`, `FLUSH`, `FULL_HOUSE`, etc. |
| `dealer_hand_rank` | Enum | Same enum |

**Why separate from POKER_SESSION?** Single Responsibility — POKER_SESSION manages money and flow, POKER_HAND manages cards and combinations.

---

## Relationships summary

| Relationship | Cardinality |
|-------------|-------------|
| USER → WALLET | 1:1 |
| WALLET → TRANSACTION | 1:N |
| USER → GAME_SESSION | 1:N |
| GAME_SESSION → BLACKJACK_SESSION | 1:0..1 |
| USER → ROULETTE_SESSION | 1:N |
| ROULETTE_SESSION → ROULETTE_BET | 1:N |
| USER → BINGO_SESSION | 1:N |
| BINGO_SESSION → BINGO_CARD | 1:1 |
| USER → HORSE_RACE_SESSION | 1:N |
| HORSE_RACE_SESSION → HORSE_RACE_BET | 1:N |
| HORSE_RACE_BET → HORSE | N:1 |
| USER → POKER_SESSION | 1:N |
| POKER_SESSION → POKER_HAND | 1:1 |

---

## Implementation phases

| Phase | Entities |
|-------|---------|
| Phase 2 — Core | USER, WALLET, TRANSACTION |
| Phase 3a — Blackjack | GAME_SESSION, BLACKJACK_SESSION |
| Phase 3b — Roulette | ROULETTE_SESSION, ROULETTE_BET |
| Phase 3c — Bingo | BINGO_SESSION, BINGO_CARD |
| Phase 3d — Horse Racing | HORSE, HORSE_RACE_SESSION, HORSE_RACE_BET |
| Phase 3e — Poker | POKER_SESSION, POKER_HAND |
