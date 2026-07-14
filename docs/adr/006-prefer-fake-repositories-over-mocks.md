# 006-prefer-fake-repositories-over-mocks

## Status

Accepted

## Context

Unit testing UseCases and ViewModels requires isolates. Using mocking frameworks (like Mockk or Mockito) often leads to fragile tests that are heavily coupled to implementation details (such as verifying specific method calls or matching exact argument constraints). These tests can easily break during refactoring, even if the underlying business logic behavior remains correct.

## Decision

We write and maintain explicit **Fake implementations** of repository and data source interfaces under test packages (e.g., `FakeScoreRepository`, `FakeSettingsRepository`, `FakeMatchRepository`). Use these fakes in ViewModel and UseCase unit tests rather than mocking frameworks.

## Consequences

- **Pros**:
  - Tests are more robust and less prone to breaking during refactorings that don't affect public APIs.
  - Fakes provide a predictable, stateful implementation that behaves like a real database/source.
  - Tests are easier to read and debug without mock configuration boilerplate.
- **Cons**:
  - Requires writing and maintaining additional test code (the fakes themselves).
