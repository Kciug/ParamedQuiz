---
name: code-developer
description: Ekspert od czystego kodu i Jetpack Compose w ParamedQuiz. Używaj tego skilla do KAŻDEJ modyfikacji lub dodania kodu, aby zapewnić najwyższą jakość, brak komentarzy, poprawny build i granulację plików UI.
---

# Code Developer - Procedura Implementacji

Jako Code Developer, Twoim priorytetem jest dostarczenie kodu produkcyjnej jakości, który jest zgodny ze wszystkimi standardami projektu ParamedQuiz.

## KROK 1: Granulacja UI (Compose)
W projekcie obowiązuje rygorystyczna zasada: **KAŻDY plik Compose musi być mały.**
- **ZAKAZ:** Tworzenia dużych plików agregujących wiele komponentów (monolitów).
- **ZASADA:** Rozbijaj UI na mniejsze, atomowe funkcje Composable.
- **LOKALIZACJA:** Każdy większy komponent logiczny (Header, Button, Item) musi znajdować się w osobnym pliku w pakiecie `components` danego modułu.

## KROK 2: Standardy Kodowania (Clean & Lean)
- **NO COMMENTS:** Absolutny zakaz komentarzy w kodzie. Jeśli kod jest trudny do zrozumienia, zmień nazewnictwo lub refaktoryzuj.
- **THEME & DIMENS:** Używaj wyłącznie `ParamedQuizTheme` i stałych z `Dimens` (np. `DEFAULT_PADDING`, `RADIUS_DEFAULT`).
- **STRINGS:** Nigdy nie hardkoduj stringów. Wszystkie teksty muszą trafić do `strings.xml`.
- **HILT:** ViewModel musi być wstrzykiwany w NavGraph, a nie w Composable.

## KROK 3: Proces Weryfikacji (Validation Workflow)
Po zakończeniu zmian w kodzie, WYKONAJ:
1. **BUILD:** Uruchom `./gradlew assembleDebug`. Jeśli build się zawiesi, użyj `./gradlew --stop` i spróbuj ponownie.
2. **CORE CATALOG:** Jeśli modyfikowałeś cokolwiek w module `core/src/main/java/com/rafalskrzypczyk/core/composables`, ZAKTUALIZUJ plik `core/CORE_COMPONENTS.md`.
3. **CLEANUP:** Napraw wszystkie błędy kompilacji i warningi.
4. **GIT:** Dodaj nowe pliki do repozytorium (`git add`).
5. **ANALYSIS:** Na koniec przedstaw analizę swojego rozwiązania: co zostało zrobione, czy są jakieś długi techniczne i co można poprawić w przyszłości.

## ZAKAZY:
- **ZAKAZ:** Dodawania jakichkolwiek komentarzy (`//` lub `/* */`).
- **ZAKAZ:** Używania standardowych komponentów Material3, jeśli w `core` istnieje ich odpowiednik.
- **ZAKAZ:** Pozostawiania nieużywanych importów.
