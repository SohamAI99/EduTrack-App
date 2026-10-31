# EduTrack: Study Progress Tracker

EduTrack is an Android application designed for students to set study goals, track study sessions, and visualize study progress. Built for IBM4203 coursework.

## Tech Stack & Architecture
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Flow-driven StateFlow UI state and manual dependency injection (`AppContainer`)
- **Database**: Room Database for persistent local storage of subjects and study sessions
- **Preferences**: Preferences DataStore for theme settings (Light, Dark, System)
- **Navigation**: Navigation Compose with bottom navigation bar and route arguments
- **Testing**: JUnit 4 for pure domain unit testing (`StreakCalculatorTest`)

## Features & Implementation Status (Days 2–23)
- **Home Dashboard**:
  - Daily study time sum and weekly progress summary card.
  - Custom Canvas arc ring displaying overall weekly goal progress percentage.
  - Per-subject progress cards with color-coded progress bars, completion percentages, and remaining goal hours.
  - Recent activity feed showing 3 newest study sessions with navigation to session details.
- **Session History & Filtering**:
  - History list grouped by date with daily totals and session count headers.
  - Horizontally scrollable subject filter chips and date range picker ("Time Window") filter working together.
  - Clear filters button and distinct empty state when no sessions match active filters.
- **Session Detail & Management**:
  - Detail view displaying subject tag, duration, formatted date, and notes.
  - Delete session with confirmation dialog.
  - Log / Edit session form with duration validation (1–720 mins), subject selector, date picker, and notes.
  - Instant Room update and Snackbar feedback upon saving.
- **Subject Management**:
  - Add / Edit / Delete subjects with color picker tags (8 colors) and weekly goal selector (1–168 hrs).
  - Cascading deletion of associated study sessions upon subject deletion.
- **Analytics & Stats**:
  - Total focused time card with comparison against previous week.
  - Custom Box-based horizontal comparison bars showing this week vs last week study hours per subject.
  - Current streak and all-time best streak cards based on consecutive study days.
  - 7-day Monday–Sunday "Weekly Rhythm" checkmark indicator card.
- **Theme & Appearance**:
  - Appearance bottom sheet supporting Light, Dark, and System theme modes.
  - Instant theme switching and DataStore persistence across app restarts.
  - Mascot empty states across all screens matching design guidelines (`HAPPY`, `STUDY`, `SLEEPY`).
  - Adaptive app launcher icon with flame logo and purple background.

## Not Built (Out of Scope)
- User onboarding flow & user profile name editing
- Active focus timer / Pomodoro stopwatch
- Smart insights / AI recommendations

## How to Run
- **Build App**:
  ```powershell
  .\gradlew.bat assembleDebug
  ```
- **Run Unit Tests**:
  ```powershell
  .\gradlew.bat testDebugUnitTest
  ```
