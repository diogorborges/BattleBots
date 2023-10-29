# BattleBots: An Android Strategy Game

<img src=https://github.com/diogorborges/BattleBots/assets/12548332/69dce3cd-1786-44be-8568-6afd80394b66 width=300>


## Overview

BattleBots is a captivating Android game, combining strategic gameplay with the latest in Android development practices. Watch robots navigate a board in their quest for prizes and immerse yourself in a world where strategy meets Android prowess.

## Game Mechanics & Requirements

### Dynamic Start
- At the start of every round, a prize token is randomly placed on the game board with robots starting from opposite corners.

### Concurrent yet Sequential Moves
- Robots take alternate moves every half-second. Kotlin Coroutines ensure a smooth and turn-based experience.

### Leaving a Trail
- Robots leave a trail that becomes an obstacle for the opponent, creating a dynamic maze.

### Movement Logic
- Robots move vertically or horizontally. If trapped, they remain stationary till round's end.

### Scoring Mechanism
- Robots race for the prize. The first to secure it wins a point. An ongoing scoreboard tracks the scores.

### Concurrency with Coroutines
- The game leverages Kotlin Coroutines for concurrency, providing fluid gameplay while ensuring turn order.

### Creative Flair
- The "current time mechanism" and "current robot turn" adds a layer of engagement and charm to the gameplay.

## Technical Details

### Jetpack Compose
- The modern UI toolkit for Android, offering a reactive and user-centric interface.

### MVVM Pattern
- A clear separation between the game's logic and UI, facilitated by LiveData or StateFlows.

### Dagger Hilt
- Simplifying dependency injection for a scalable codebase.

### State Management
- Constructs like `GameBoardState` and `GameBoardViewState` capture and respond to game scenarios.

### Repository Pattern
- `GameBoardRepository` ensures a clean data layer, interfacing with core game logic.

### Hot Flow GameBoard Stream
- Continuously emits game board states, keeping the game dynamic and live.

### Testing
- Comprehensive unit tests using MockK validate game logic, state transitions, robot movements, and overall gameplay.

## Getting Started

1. Ensure you have **Android Studio Giraffe | 2022.3.1 Patch 2** with **Java 17** installed due to the game's requirement for Gradle 8.
2. Open in Android Studio.
3. Build and run on your preferred emulator or physical device.
