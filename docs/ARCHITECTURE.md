# Score-Count Application Architecture

## Overview

The Score-Count application follows a layered architecture pattern, influenced by Clean Architecture principles, to promote separation of concerns, testability, and maintainability. The architecture implements a **Rich Domain Model** pattern, where business logic resides in pure domain components rather than in data sources or repositories.

## Layers

### 1. UI (Presentation Layer)

*   **Framework**: Jetpack Compose for declarative UI development.
*   **Components**:
    *   **Screens (`ScoreScreen.kt`, `SettingsScreen.kt`, `MatchHistoryScreen.kt`)**: Composables responsible for displaying data and capturing user input. They observe state from ViewModels. The `ScoreScreen` includes conditional logic to display different layouts for portrait and landscape orientations.
    *   **ViewModels (`ScoreViewModel.kt`, `SettingsViewModel.kt`, `MatchHistoryViewModel.kt`)**: Prepare and manage data for the UI. They directly expose repository `StateFlow`s for state-based data (game state, settings) and handle user actions by delegating to UseCases. ViewModels do not transform or copy state unnecessarily.
    *   **Navigation**: Jetpack Navigation Compose (`NavHost`, `Screen.kt` for routes) is used for navigating between screens.
    *   **Preview Data**: Fake/Preview versions of Repositories are used within screen previews to provide sample data and facilitate UI development.
*   **Responsibilities**: Displaying application state, handling user interactions, and delegating business logic to lower layers.

### 2. Domain Layer

*   **Components**:
    *   **Models (`GameState.kt`, `Player.kt`, `GameSettings.kt`, `Match.kt`)**: Kotlin data classes representing the core entities of the application. These are plain Kotlin objects with no Android framework dependencies. Scores are stored within `Player` objects as part of the immutable state.
    *   **Business Logic (`ScoreCalculator.kt`)**: A pure, stateless object containing all game rules and score calculation logic. It has **zero dependencies** and operates purely on domain models. Methods include:
        *   `incrementScore()`: Handles point scoring, set completion, match completion, deuce detection, and server rotation
        *   `decrementScore()`: Reduces player score with bounds checking
        *   `switchServe()`: Manually switches the serving player
        *   `resetGame()`: Creates a new initial game state
    *   **UseCases (e.g., `IncrementScoreUseCase.kt`, `GetMatchesUseCase.kt`, `SaveMatchUseCase.kt`)**: **Orchestrators** that coordinate between repositories and business logic. They follow the pattern:
        1. **Fetch** current state/settings from repositories
        2. **Calculate** new state using `ScoreCalculator` (pure domain logic)
        3. **Save** new state back to repositories
    *   **Repository Interfaces (`ScoreRepository.kt`, `SettingsRepository.kt`, `MatchRepository.kt`)**: Define contracts for data access. State-based repositories return `StateFlow` (e.g., `getGameState(): StateFlow<GameState>`), while event-based repositories return `Flow` (e.g., `getMatchList(): Flow<List<Match>>`).
*   **Responsibilities**: Contains the core business logic and rules of the application. This layer is independent of UI and Data implementation details.

### 3. Data Layer

*   **Components**:
    *   **Repositories (`ScoreRepositoryImpl.kt`, `SettingsRepositoryImpl.kt`, `MatchRepositoryImpl.kt`)**: Implement the Repository interfaces. They are simple pass-through layers that coordinate data from different data sources **without containing business logic**.
    *   **Data Sources**:
        *   **Local**: Manage data persistence locally.
            *   `LocalScoreDataSource`: Persists `GameState` to disk using **Proto DataStore** and exposes it as `StateFlow`. State automatically survives app process death and device restarts. Contains no business logic or dependencies on other repositories.
            *   `SettingsLocalDataSource`: Persists `GameSettings` using Android's **Preferences DataStore** and converts the DataStore `Flow` to `StateFlow` using `stateIn()`.
            *   `LocalMatchDataSource`: Fetches and saves match data from the Room database.
    *   **Database (`AppDatabase.kt`)**: A Room database that holds application data.
        *   `MatchDao`: Defines the data access methods for match history.
        *   `MatchEntity`: Represents a match record in the database.
    *   **Mappers (`MatchMapper.kt`, `GameStateMapper.kt`)**: Convert data between data layer entities/protos and domain layer models.
*   **Responsibilities**: Data retrieval, storage, and management. Abstracts the origin of the data from the Domain layer. **No business logic** resides in this layer.

## Dependency Injection

*   **Framework**: Hilt is used for managing dependencies throughout the application.
*   **Modules (`DataModule.kt`, `RepositoryModule.kt`, `DataSourceModule.kt`)**: Define how dependencies are provided and injected.
*   **Key Decision**: `LocalScoreDataSource` has no injected dependencies, breaking the previous circular dependency with `SettingsRepository`.

## Key Architectural Decisions & Patterns

*   **Rich Domain Model**: Business logic resides in pure domain components (`ScoreCalculator`) rather than in data sources or repositories, avoiding the Anemic Domain Model anti-pattern.
*   **Use Case Orchestration**: UseCases coordinate between repositories and business logic, following a clear fetch → calculate → save pattern.
*   **StateFlow for State**: Repository interfaces return `StateFlow` for state-based data (game state, settings) to provide:
    *   Always-available current value (no null checks needed)
    *   Value conflation (only latest state matters)
    *   Better semantic clarity between state and events
*   **Flow for Events**: Repository interfaces return `Flow` for event streams or collections (match history).
*   **Unidirectional Data Flow (UDF)**: UI observes state from ViewModels, and user actions flow from the UI to ViewModels, which then update the state.
*   **Immutability**: Domain models are handled as immutable data classes.
*   **Repository Pattern**: Decouples the Domain layer from data source implementations.
*   **Room for Persistence**: Room is used for local database storage to persist match history.
*   **Testability**: The architecture promotes testability. Pure business logic is easily tested without mocking. Use cases and ViewModels use fake repositories for testing.

## Project Structure (Simplified)

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/soyvictorherrera/scorecount/
│   │   │   ├── di/                      # Hilt Modules
│   │   │   ├── domain/
│   │   │   │   ├── calculator/          # Pure Business Logic (ScoreCalculator)
│   │   │   │   ├── model/               # Domain Models
│   │   │   │   ├── repository/          # Repository Interfaces
│   │   │   │   └── usecase/             # Use Case Orchestrators
│   │   │   ├── data/
│   │   │   │   ├── database/            # Room Database (AppDatabase, DAOs, Entities)
│   │   │   │   ├── datasource/          # Data Source Implementations
│   │   │   │   ├── mapper/              # Data Mappers
│   │   │   │   └── repository/          # Repository Implementations
│   │   │   ├── ui/
│   │   │   │   ├── scorescreen/         # Score UI + ViewModel
│   │   │   │   ├── settings/            # Settings UI + ViewModel
│   │   │   │   ├── matchhistory/        # Match History UI + ViewModel
│   │   │   │   ├── theme/               # Compose Theme
│   │   │   │   └── Screen.kt            # Navigation Routes
│   │   │   ├── MainActivity.kt
│   │   │   └── ScoreCountApplication.kt
│   │   └── res/
│   ├── test/java/com/soyvictorherrera/scorecount/    # Unit Tests (100 tests)
│   │   ├── domain/
│   │   │   ├── calculator/              # ScoreCalculatorTest (27 tests)
│   │   │   └── usecase/                 # Use Case Tests (22 tests)
│   │   ├── data/
│   │   │   ├── datasource/              # LocalScoreDataSourceTest (14 tests)
│   │   │   └── mapper/                  # Mapper Tests (25 tests)
│   │   └── ui/                          # ViewModel Tests (29 tests)
│   ├── androidTest/java/                # Instrumented Tests (3 tests)
│   └── AndroidManifest.xml
├── build.gradle.kts
└── ARCHITECTURE.md                      # This file
```

## Diagram (Conceptual)

```
┌─────────────────────┐
│         UI          │
│ (Compose, ViewModel)│
└──────────┬──────────┘
           │ delegates actions
           ↓
┌─────────────────────┐
│     Use Cases       │ ← Orchestrators
│  (Fetch-Calc-Save)  │
└─────┬──────────┬────┘
      │          │
      │          ↓
      │     ┌──────────────────┐
      │     │ ScoreCalculator  │ ← Pure Business Logic
      │     │  (Pure Functions)│    (Zero Dependencies)
      │     └──────────────────┘
      │
      ↓
┌─────────────────────┐
│    Repositories     │ ← Simple Pass-Through
│   (Interfaces)      │
└──────────┬──────────┘
           │
           ↓
┌─────────────────────┐
│    Data Sources     │ ← State Holders Only
│  (No Business Logic)│    (No Dependencies)
└─────────────────────┘
           │
           ↓
┌─────────────────────┐
│  Room DB / DataStore│
└─────────────────────┘

   All layers connected via HILT (Dependency Injection)
```

## State Management Pattern

### Repository Layer
Repositories expose state using `StateFlow` for state-based data:

```kotlin
interface ScoreRepository {
    fun getGameState(): StateFlow<GameState>
    suspend fun updateGameState(newState: GameState)
}

interface SettingsRepository {
    fun getSettings(): StateFlow<GameSettings>
    suspend fun saveSettings(settings: GameSettings)
}
```

### ViewModel Layer
ViewModels directly expose repository StateFlows without intermediate copying:

```kotlin
@HiltViewModel
class ScoreViewModel @Inject constructor(
    private val scoreRepository: ScoreRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    // Direct exposure - no transformation needed
    val gameState: StateFlow<GameState> = scoreRepository.getGameState()
    val gameSettings: StateFlow<GameSettings> = settingsRepository.getSettings()
}
```

This ensures:
- No unnecessary state copying
- Single source of truth
- State automatically survives configuration changes
- Type safety (StateFlow guarantees non-null values)

### Data Source Layer
Data sources convert DataStore `Flow` to `StateFlow` when needed:

```kotlin
class SettingsLocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val settings: StateFlow<GameSettings> = dataStore.data
        .map { preferences -> /* mapping */ }
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = GameSettings()
        )
}
```

## Development Workflow

### Adding New Features

Follow this layered approach when adding new functionality:

1. **Define domain models** in `domain/model/` - Plain Kotlin data classes with no Android dependencies
2. **Add business logic** to `domain/calculator/` - Pure functions operating on domain models
3. **Create repository interface** in `domain/repository/` - Contract for data operations (use `StateFlow` for state, `Flow` for events)
4. **Implement use cases** in `domain/usecase/` - Orchestrate: fetch → calculate → save
5. **Implement repository** in `data/repository/` - Simple pass-through to data sources
6. **Implement/update data source** in `data/datasource/` - State holders only, no business logic
7. **Create/update ViewModel** in `ui/` - Expose repository StateFlows and delegate actions to use cases
8. **Build UI** with Compose in `ui/` - Stateless composables driven by ViewModel
9. **Wire dependencies** with Hilt modules in `di/` - Provide implementations
10. **Write tests** - Pure functions, use cases with fakes, ViewModels with fakes

### UI Development

- Use Compose Previews with fake repositories for rapid iteration
- Screen composables should be stateless and receive state as parameters
- Handle orientation changes within composable logic (see `ScoreScreen`)
- ViewModels should never reference Android framework classes except lifecycle-aware components
- Collect StateFlows using `collectAsState()` in composables

### Testing Strategy

The project has **comprehensive test coverage** with **100 tests total**:

**Unit Tests (97 tests):**
- **Domain Layer (49 tests)**:
  - `ScoreCalculatorTest`: 27 tests covering all game rules (pure function tests, no mocking needed)
  - Use Case Tests: 22 tests for orchestration logic using fake repositories
- **Data Layer (39 tests)**:
  - `LocalScoreDataSourceTest`: 14 tests for GameState persistence, state restoration across app restarts
  - `GameStateMapperTest`: 15 tests for bidirectional proto ↔ domain mapping
  - `MatchMapperTest`: 10 tests for bidirectional entity ↔ domain mapping
- **UI Layer (29 tests)**:
  - `ScoreViewModelTest`: 9 tests for state exposure, delegation, auto-save
  - `MatchHistoryViewModelTest`: 5 tests for loading, updates, error handling
  - `SettingsViewModelTest`: 12 tests for all settings mutations
  - All ViewModel tests use fake repositories (no mocking frameworks)

**Instrumented Tests (3 tests):**
- `MainActivityTest`: App launch, screen display, navigation verification

**Testing Approach:**
- **Pure functions** (ScoreCalculator): Test directly with inputs and expected outputs
- **Use cases**: Test with fake repositories to verify orchestration
- **ViewModels**: Test with fake repositories to verify state exposure and delegation
- **Fake repositories > Mocks**: Cleaner, more maintainable, easier to debug
- Test files mirror production structure under `app/src/test/`
- Use `kotlinx-coroutines-test` (`runTest`, `StandardTestDispatcher`) for async testing
- Use JUnit 5 (Jupiter) for unit tests, JUnit 4 for instrumented tests

## Technology Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Dependency Injection**: Hilt
- **Database**: Room (match history)
- **Async Operations**: Kotlin Coroutines & Flow
- **Persistence**:
  - Proto DataStore (GameState - complex nested objects)
  - Preferences DataStore (GameSettings - simple key-value pairs)

### Key Dependencies
- **Compose BOM**: Manages Compose library versions
- **Navigation Compose**: Type-safe navigation
- **Hilt Navigation Compose**: Integration between Hilt and Navigation
- **Material 3**: Material Design components
- **Lifecycle ViewModel Compose**: ViewModel integration with Compose
- **Kotlinx Coroutines Test**: Testing library for coroutines and flows
- **JUnit 5 (Jupiter)**: Testing framework for unit tests
- **JUnit 4**: Testing framework for instrumented tests

### Build Configuration
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **JVM Target**: 11
- **ProGuard**: Enabled for release builds
- **Version Catalog**: `gradle/libs.versions.toml` for centralized dependency management

## Architectural Benefits

This architecture provides:

✅ **Separation of Concerns**: Business logic in domain, persistence in data, presentation in UI
✅ **Testability**: Pure functions easy to test, comprehensive test coverage (100 tests)
✅ **No Circular Dependencies**: Clean dependency flow from UI → Domain → Data
✅ **Better Type Safety**: StateFlow for state guarantees non-null values
✅ **Reduced Complexity**: Clean data layer with focused responsibilities
✅ **Persistence**: GameState survives app process death via Proto DataStore
✅ **Future-Proof**: Easy to add features, change persistence, or refactor

This architecture aims to create a scalable, testable, and maintainable codebase for the Score-Count application following Clean Architecture and Domain-Driven Design principles.
