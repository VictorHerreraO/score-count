# AGENTS.md

This file provides guidance to AI Agents when working with code in this repository.

## Critical Evaluation - READ THIS FIRST

**Before implementing ANY task, you MUST:**

1. **Question the premise** - Does this task solve a real problem or is it refactoring for refactoring's sake?
2. **Analyze the trade-offs** - Will this make the code objectively better or just different?
3. **Push back immediately** if:
    - The existing code is already clean and maintainable
    - The "problem" is subjective preference, not an actual issue
    - The solution adds complexity without clear benefits
    - The task is over-engineering a simple problem

**Your job is to provide engineering judgment, not blindly execute tasks.**

If a task doesn't pass critical evaluation, respond with:

- Clear analysis of why the current approach is adequate
- Specific trade-offs the proposed change would introduce
- Recommendation to close/modify the task

**Do NOT waste time implementing unnecessary changes.** Resources are finite.

## Project Overview

Score-Count is an Android table tennis score tracking application built with Kotlin and Jetpack Compose. It follows Clean Architecture with three layers: UI (Compose), Domain (business logic), and Data (repositories/persistence).

**For detailed architecture information, see [ARCHITECTURE.md](./docs/ARCHITECTURE.md).**

## Project Progress Tracking

This project uses dedicated files for tracking work progress.
When working on this project, update these files to maintain continuity across sessions.

## Essential Commands

### Build & Install

```bash
./gradlew assembleDebug          # Build debug APK
./gradlew assembleRelease        # Build release APK
./gradlew installDebug           # Install on device/emulator
./gradlew clean build            # Clean rebuild
```

### Testing

```bash
./gradlew test                   # Run all unit tests
./gradlew app:test               # Run app module tests
./gradlew connectedAndroidTest   # Run instrumented tests (needs device)

# Run specific test class
./gradlew test --tests "com.soyvictorherrera.scorecount.ui.settings.SettingsViewModelTest"
```

### Code Quality

```bash
./gradlew lint                   # Run lint checks
./gradlew lintDebug              # Generate lint report
./gradlew ktlintCheck            # Check Kotlin code formatting
./gradlew ktlintFormat           # Auto-fix Kotlin code formatting
```

### Release Management

```bash
# Trigger release build manually
gh workflow run release-build.yml --ref main

# List recent releases
gh release list

# Download latest release APK
gh release download --pattern '*.apk'

# Create a new version tag
git tag -a v1.1.0 -m "Release v1.1.0"
git push origin v1.1.0
```

## Quick Reference

### Package Structure

```
com.soyvictorherrera.scorecount/
├── di/                 # Hilt DI modules
├── domain/             # Business logic (models, use cases, repo interfaces)
├── data/               # Data layer (repos, data sources, Room DB, mappers)
└── ui/                 # Compose UI (screens, ViewModels, navigation)
```

### Key Files & Locations

- **Navigation routes**: `ui/Screen.kt`
- **Hilt modules**: `di/DataModule.kt`, `di/RepositoryModule.kt`, `di/DataSourceModule.kt`
- **Database**: `data/database/AppDatabase.kt`, `data/database/dao/MatchDao.kt`
- **Domain models**: `domain/model/` (GameState, Player, GameSettings, Match)
- **Use cases**: `domain/usecase/` (IncrementScoreUseCase, SaveMatchUseCase, etc.)

### Configuration

- **Package**: `com.soyvictorherrera.scorecount`
- **Min SDK**: 28 | **Target SDK**: 36 | **JVM**: 11
- **Dependencies**: Managed via `gradle/libs.versions.toml`
- **ProGuard**: Enabled for release builds
- **Code Style**: Enforced by ktlint (see `.editorconfig` for rules)

## Development Workflow

### Release Process

1. Merge feature PR to `main`
2. CI automatically builds signed release APK
3. Download APK from Actions artifacts
4. (Optional) Create git tag for formal release: `git tag -a v1.x.x -m "Release note"`
5. Push tag to trigger GitHub Release: `git push origin v1.x.x`

### Version Catalog

Add dependencies to `gradle/libs.versions.toml` before using them in build files.

## Development Methodology

When working on features or changes:

1. **Reason**: Analyze the goal or situation
2. **Plan**: Break down into actionable steps
3. **Validate**: Review plan and confirm approach
4. **Act**: Execute the steps
5. **Document**: Update `docs/` as appropriate

## Branching strategy

Always update the repository with the latest changes.

Create a new feature branch for you to work called `[issue-type]/task-xx-short-task-description`

Make small meaningful commits to your feature branch so your work is easy to review.

Once you are done **don't** merge your code, allow me to review it and I'll merge it manually.

### Multi-Issue Epics (Feature Branches)

For large features spanning multiple GitHub issues (e.g., epics), use an integration branch:

**Pattern:** `feature/[feature-name]-integration`

**Example:** `feature/spen-integration` (S Pen Air Actions Integration)

**Workflow:**
1. Create integration branch: `git checkout -b feature/spen-integration`
2. Push to remote: `git push origin feature/spen-integration`
3. Implement issues in order, making small commits to this branch
4. When complete, create single PR from integration branch to `develop`
5. After review and approval, merge to `develop` and eventually `main`

**Benefits:**
- Keeps all related work on one branch
- Prevents conflicts from parallel development
- Clean separation from ongoing work on `develop`
- All commits easily associated with the epic
