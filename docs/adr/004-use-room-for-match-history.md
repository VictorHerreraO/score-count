# 004-use-room-for-match-history

## Status

Accepted

## Context

Unlike active game state, which is a single nested object, match history is an unbounded list of completed matches that requires query capability, sorting, and efficient inserts.

## Decision

We use Jetpack **Room Database** to store match history records. Define a SQLite database with a `MatchEntity` mapper and a `MatchDao` for database access.

## Consequences

- **Pros**:
  - SQLite query capability for sorting, filtering, and indexing match lists.
  - Seamless integration with coroutines and flows (`Flow<List<MatchEntity>>`).
  - Standard Android database framework, widely supported.
- **Cons**:
  - Introduces SQL query writing and schema migration overhead if database schema changes in the future.
