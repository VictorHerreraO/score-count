# 007-implement-challenger-queue-system

## Status

Accepted

## Context

Table tennis matches are often played in a "winner-stays-on" format where multiple players wait in a queue. When the active game ends, the loser moves to the end of the queue, the winner remains as an active player, and the next player in the queue steps up to play. We need a system that persists this queue, manages setting toggles for this mode, allows adding/skipping/removing players, and rotates players seamlessly upon game reset.

## Decision

We implement a Challenger Queue System spanning all Clean Architecture layers:
1. **Data Layer Persistence**:
   - Persist player profiles in a Room table (`player_profiles`) with unique display names.
   - Seed default player profiles ("Zack" and "Zoe") during database creation/open for testing.
   - Update `game_state.proto` to include a persistent list of queued players (`repeated PlayerProto challenger_queue = 8`).
   - Persist the `challengerMode` configuration key in the Preferences DataStore.
2. **Domain Logic Rotation**:
   - Update `ScoreCalculator.resetGame` and `ResetGameUseCase` to execute the player rotation when `challengerMode` is enabled, the match is finished, a winner is provided, and the queue is not empty.
   - Return the new match state (sets/scores reset, loser rotated to the end of the queue, next queue player stepped up).
3. **UI Layer**:
   - Implement `ManageQueueBottomSheet` allowing the user to toggle challenger mode, add players from profiles with autocomplete, and skip/remove queued players.
   - Integrate horizontal next-player display components and "Next: [Name]" indicators into scoreboard screens.

## Consequences

- **Pros**:
  - Automatically handles player rotations without manual configuration between games.
  - Maintains persistent state of the queue across app restarts via DataStore Proto.
  - Full autocomplete list matches existing player profiles stored in Room.
- **Cons**:
  - Increased complexity in `ScoreCalculator` to determine serving rules and player assignments during active rotation.
  - Requires Room migration v1 to v2 to introduce the `player_profiles` schema.
