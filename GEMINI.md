# ParamedQuiz - Project Overview

## Project Summary
ParamedQuiz is a multi-module Android application developed in Kotlin, using Jetpack Compose for the UI. It is designed as a quiz application for paramedics, featuring various modes and Firebase integration.

### Modules
- **app**: Main application module, entry point.
- **core**: Contains shared UI components (`com.rafalskrzypczyk.core.composables`), theme (`com.rafalskrzypczyk.core.ui.theme`), utilities, and dependency injection modules.
- **auth**: Authentication features and logic.
- **firestore**: Firebase Firestore integration and data handling.
- **home**: Home screen feature.
- **main_mode**: Core quiz mode feature.
- **swipe_mode**: Swipe-based quiz mode feature.
- **score**: Score management and display features.
- **signup**: User registration feature.
- **translation_mode**: New quiz mode for vocabulary learning.

## Tech Stack
- **Language**: Kotlin 2.0.0
- **UI Framework**: Jetpack Compose (BOM 2025.03.01)
- **Design System**: Material 3
- **Dependency Injection**: Hilt (implied)
- **Backend/Data**: Firebase Firestore

## Build Configuration
- **App ID**: `com.rafalskrzypczyk.paramedquiz`
- **Min SDK**: 24
- **Target SDK**: 35
- **Compile SDK**: 35
- **Version**: 1.0.9 (10009)

## Design System
The design system is centralized in the `core` module. All new UI MUST use these definitions.

### Theme
- **Theme Name**: `ParamedQuizTheme`
- **Location**: `com.rafalskrzypczyk.core.ui.theme`

### Colors (`Color.kt`)
- **Primary**: `0xFF6464FF`
- **Secondary**: `0xFFFF6478`
- **Tertiary**: `0xFF00BFA6`
- **Background**: `0xFFF8F9FF` (Light), `0xFF0F0F1A` (Dark)
- **Surface**: `0xFFF0F2FA` (Light), `0xFF1A1A26` (Dark)
- **Custom**: `MQYellow` (`0xFFFFD400`), `MQRed` (`0xFFFF2600`), `MQGreen` (`0xFF009900`)

### Dimensions (`Dimens.kt`)
- **Corner Radius**: `RADIUS_DEFAULT` (32.dp), `RADIUS_SMALL` (16.dp)
- **Padding**: `DEFAULT_PADDING` (15.dp), `LARGE_PADDING` (30.dp), `SMALL_PADDING` (5.dp)
- **Spacing**: `ELEMENTS_SPACING` (15.dp), `ELEMENTS_SPACING_SMALL` (5.dp)
- **Stroke**: `OUTLINE_THICKNESS` (2.dp)

### Reusable Components (`com.rafalskrzypczyk.core.composables`)

#### Buttons (`Buttons.kt`)
- **ButtonPrimary**: Main action button (Filled, rounded). Use for primary actions like "Login", "Start Quiz".
- **ButtonSecondary**: Secondary action (Outlined). Use for alternative actions.
- **ButtonTertiary**: Text-only button (Transparent background). Use for less prominent actions.
- **ActionButton**: Icon-only button.
- **ActionButtonImage**: Icon-only button taking a `Painter`.

#### Texts (`Texts.kt`)
- **TextPrimary**: Standard body text. Supports `maxLines` and `textAlign`.
- **TextHeadline**: Small headlines (`headlineSmall`).
- **TextTitle**: Large titles (`headlineLarge`), bold.
- **TextCaption**: Small descriptive text (`labelSmall`), tertiary color.
- **TextCaptionLink**: Clickable link text.
- **TextScore**: Specialized text for displaying scores (Bold, 18.sp).

#### Inputs (`TextFields.kt`)
- **TextFieldPrimary**: Standard single-line text input.
- **TextFieldMultiLine**: Multi-line text input (min 5 lines).
- **PasswordTextFieldPrimary**: Password input with visibility toggle.

#### Top Bars
- **MainTopBar**: `com.rafalskrzypczyk.core.composables.top_bars.MainTopBar`
- **NavTopBar**: `com.rafalskrzypczyk.core.composables.top_bars.NavTopBar`
- **QuizTopBar**: `com.rafalskrzypczyk.core.composables.top_bars.QuizTopBar`

#### Other
- **Loaders**: `Loading` (in `Loading.kt`).
- **Dialogs**: `BaseDialogs.kt`.

## DI (Hilt)
- **CoreModule**: Provides global singletons like `ResourceProvider`, `FirebaseError`, `SharedPreferences`, `UserManager`, and `CoroutineScope` (IO).

## AI Instructions
1.  **Component Reuse**: **MANDATORY**. Always prefer using components from the `core` module over standard Compose Material3 components. Check `core/src/main/java/com/rafalskrzypczyk/core/composables` before creating new ones.
2.  **Module Awareness**: Respect the multi-module architecture.
    -   Place new features in appropriate modules or create new ones if necessary.
    -   Manage dependencies via `build.gradle.kts` and `libs.versions.toml`.
3.  **Theming**: Ensure all new UI elements use `ParamedQuizTheme`.
4.  **Compose**: Use Jetpack Compose for all new UI development.
5.  **Git Management**:
    -   Always add newly created or modified files to the git stage using `git add`.
    -   **DO NOT COMMIT**. Do not use `git commit`. Leave the committing to the user.
6.  **Verification**: After major changes, build the project (e.g., `./gradlew assembleDebug`) to ensure no regressions.
7.  **Architecture**:
    -   **ViewModel Injection**: ViewModels MUST be injected in the navigation graph (e.g., `AppNavHost` or `ApplicationNavigation.kt`), NEVER passed as arguments to Screen Composables. Screen Composables should only receive state and event callbacks.
    -   **Use Cases**: When a ViewModel requires multiple Use Cases, group them into a single data class wrapper (e.g., `FeatureUseCases`).
    -   **Clean Architecture**: Follow the standard layers: Data (Repository Impl) -> Domain (Repository Interface, Use Cases) -> Presentation (ViewModel, Screen).
    -   **UI Split**: Avoid monolithic Screen files. Split UI into smaller, reusable components in a `components` package within the feature module.
8.  **Code Quality & Testing**:
    -   **Avoid Comments**: Do not add comments describing *what* the code does. Only comment *why* if the logic is complex and non-obvious.
    -   **Testing**: Write unit tests for critical business logic, specifically for **ViewModels** and **Use Cases**.
    -   **String Resources**: **MANDATORY**. Never hardcode UI strings. Always extract them to `strings.xml` and use `stringResource()`.