# 002-persist-game-state-with-proto-datastore

## Status

Accepted

## Context

The `GameState` contains complex, nested configurations (such as sets won, scores for players, current server, and status). It must persist across application restarts and process death so users don't lose match progress. Using a relational SQLite database (Room) for a single active game state is over-engineered, while standard SharedPreferences does not natively support type-safe, nested, or structured data formats.

## Decision

We use Android's **Proto DataStore** to serialize and persist the active `GameState` on disk. To prevent circular dependencies, the `LocalScoreDataSource` (which manages `GameState` serialization) has zero injected dependencies, decoupling it from the settings repository.

## Consequences

- **Pros**:
  - High performance, type-safe serialization using Protocol Buffers.
  - Survived process death natively through reactive streams.
  - Solved the circular dependency issue by keeping the data source completely decoupled.
- **Cons**:
  - Requires defining a `.proto` schema file and compiling it, adding compile-time steps.
  - Requires writing custom serializers and mappers.
