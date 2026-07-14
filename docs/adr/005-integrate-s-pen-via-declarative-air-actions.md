# 005-integrate-s-pen-via-declarative-air-actions

## Status

Accepted

## Context

We want to support hands-free score incrementing using S Pen gestures (e.g., button clicks) on supported Samsung tablets (like the Galaxy Tab S10+). We need a stable contract with the Samsung system framework that avoids polling, has minimal CPU/battery footprint, and activates only when the user is actively on the scoring screen.

## Decision

We use Samsung's **Declarative Air Actions** framework:
1. Define actions in `remote_actions_config.xml` mapping single click to `KEYCODE_PAGE_DOWN` and double click to `KEYCODE_PAGE_UP`.
2. Reference this configuration via `AndroidManifest.xml` meta-data.
3. Catch these key events by overriding `onKeyDown` in `MainActivity`.
4. Use Compose's `DisposableEffect` in `ScoreScreen` to register the active `ScoreViewModel` to `MainActivity` on entry, and clean it up on exit, ensuring key presses only increment scores when the scoring screen is active.

## Consequences

- **Pros**:
  - Low latency, system-level click discrimination with zero battery impact.
  - Integrates automatically with Samsung's Air Actions settings UI.
  - Clear separation: key events are ignored when not on the scoring screen.
- **Cons**:
  - Limited to keycodes sent by the S Pen system (cannot easily pass arbitrary custom payloads).
  - Tight coupling between `MainActivity` key event routing and the active Compose screen's ViewModel.
