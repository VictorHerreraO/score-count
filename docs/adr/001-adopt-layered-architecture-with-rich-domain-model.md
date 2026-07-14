# 001-adopt-layered-architecture-with-rich-domain-model

## Status

Accepted

## Context

The application tracks live table tennis scores, sets, and game options. A simple, anemic domain model where business logic is mixed into UI ViewModels or Repository layers would make the rules of table tennis (deuce, server rotation, game/match completion) hard to test, modify, and keep consistent. We need a clear separation of concerns to support rapid UI prototyping and robust unit testing.

## Decision

We implement a three-layer Clean Architecture layout:
- **UI (Presentation)**: Built with Jetpack Compose, driven by lifecycle-aware ViewModels.
- **Domain**: Contains plain Kotlin objects without Android dependencies. All scoring logic resides in a pure, stateless `ScoreCalculator` component. Orchestration is handled by specialized `UseCases` (fetch -> calculate -> save).
- **Data**: Simple repositories and data sources responsible only for CRUD and persistence, with zero business logic.

## Consequences

- **Pros**:
  - Scoring rules (`ScoreCalculator`) can be exhaustively unit tested in milliseconds without Android instrumentation or mocking frameworks.
  - The UI and persistence layers can change independently.
  - No business logic leaks into repositories or data sources.
- **Cons**:
  - Requires writing more boilerplate (e.g. data mappers, use cases, repository interfaces) than a direct UI-to-database architecture.
