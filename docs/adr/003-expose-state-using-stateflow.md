# 003-expose-state-using-stateflow

## Status

Accepted

## Context

In Jetpack Compose, UI components need to reactively observe state changes. Traditional `LiveData` is tied to Android lifecycles, and standard `Flow` is cold, meaning it doesn't hold a current value and starts emitting from scratch for every collector, leading to duplicate database reads or network calls. ViewModels and screens need direct access to the latest state value synchronously and reactively.

## Decision

We expose state-based data (like `GameState` and `GameSettings`) from repositories and data sources using Kotlin's `StateFlow`. ViewModels expose the repository `StateFlow` directly to the Compose UI, avoiding duplicate state mapping, copying, or caching.

## Consequences

- **Pros**:
  - `StateFlow` always holds the latest state, which can be read synchronously.
  - Guarantees non-null initial values, preventing type checking boilerplate in Compose.
  - Automatic conflation ensures only the latest update is processed by the UI.
- **Cons**:
  - ViewModels must ensure lifecycle collection is safe (e.g., using `collectAsStateWithLifecycle` in Compose or caching appropriately).
