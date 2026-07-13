# Dźwięk i Haptic — plan wpięcia

Dokument śledzący miejsca, w które warto wpleść feedback dźwiękowy i/lub haptyczny.
Status: **analiza zakończona, implementacja nierozpoczęta.**

Legenda: 🔊 dźwięk · 📳 haptic · 🔊📳 oba

---

## Stan wyjściowy (audyt)

- **Dźwięk:** nie istnieje nigdzie w projekcie (brak `SoundPool`/`MediaPlayer`/`res/raw`).
- **Haptic:** tylko jedno wystąpienie — `core/.../composables/rating/AppRatingCard.kt:197`
  (`performHapticFeedback(LongPress)` przy ocenie gwiazdką).
- **Centralny punkt kliknięć:** `core/.../utils/DebounceClick.kt` (`rememberDebouncedClick`) —
  przechodzą przez niego wszystkie przyciski/karty/dialogi. Jedno wpięcie = pokrycie całej apki.
- **Rdzeń quizu współdzielony:** `main_mode` (`BaseQuizVM`, `MMQuizScreen`, `QuizGameContent`,
  `QuizSubmittedSection`) jest reużywany przez `cem_mode`.

---

## Poziom 0 — infrastruktura (fundament) ✅ ZROBIONE

- [x] `FeedbackManager` (interfejs + `FeedbackManagerImpl` na `Vibrator`; warstwa dźwięku
      `SoundPool` gotowa, ale no-op do czasu dostarczenia plików `res/raw`) —
      `core/.../feedback/`, wiązanie w `CoreModuleBinds`
- [x] Dwa niezależne przełączniki w ustawieniach (Dźwięk / Wibracje) — flagi
      `KEY_SOUND_ENABLED` / `KEY_HAPTIC_ENABLED` (domyślnie `true`) w `SharedPreferencesApi`,
      UI w `UserSettingsScreen` (kategoria „Aplikacja")
- [x] 📳 globalny lekki tick na każdym kliknięciu — wpięcie w `rememberDebouncedClick`
      (`core/.../utils/DebounceClick.kt`, `FeedbackEvent.CLICK`)
- [x] `CompositionLocal` `LocalFeedbackManager` (no-op domyślnie dla `@Preview`),
      dostarczany w `MainActivity`; uprawnienie `VIBRATE` w manifeście

> **Weryfikacja:** `:core` + `:home` + wszystkie moduły biblioteczne kompilują się
> (`BUILD SUCCESSFUL`). Pełny `assembleDebug` modułu `:app` blokuje **istniejący,
> niezwiązany** problem `processDebugGoogleServices` (namespace `com.frontfolks.mediquiz`
> nie ma wpisu w `google-services.json`).

## Iteracja 2 — dźwięk + zdarzenia quizu ✅ ZROBIONE

- [x] Warstwa dźwięku aktywna — mapa `soundResources` (`fb_correct/wrong/complete/record`),
      eager preload `SoundPool` w `init` (`FeedbackManagerImpl`)
- [x] 🔊📳 poprawna/błędna + koniec quizu wyzwalane z ViewModeli WSZYSTKICH trybów
      (main/cem/daily przez `BaseQuizVM`, plus `RevisionsQuizVM`, `SwipeModeVM`,
      `TranslationQuizViewModel`)
- [x] 🔊📳 swipe: nowy rekord combo → `NEW_RECORD` (zamiast `QUIZ_COMPLETED`)

> **Weryfikacja:** `:core` + `:main_mode` + `:cem_mode` + `:swipe_mode` + `:translation_mode`
> + `:revisions` → `BUILD SUCCESSFUL`. Pełna walidacja grafu Hilt i `assembleDebug` `:app`
> nadal blokowane przez niezwiązany `processDebugGoogleServices` (namespace).

## Poziom 1 — rdzeń quizu (main_mode + cem_mode)

- [ ] 📳 zaznaczenie odpowiedzi — `QuizGameContent.kt:260` `AnswerButton.onClick` (lekki tick)
- [x] 🔊📳 **poprawna / błędna odpowiedź** — wyzwalane w `BaseQuizVM.submitAnswer()`
      (`perform(ANSWER_CORRECT/ANSWER_WRONG)`); dźwięk + haptic
- [ ] 📳 „Następne pytanie" — `QuizSubmittedSection.kt:90` (pokryte globalnym hookiem)
- [x] 🔊📳 **koniec quizu** — `BaseQuizVM.finishQuiz()` (`perform(QUIZ_COMPLETED)`)

## Poziom 1 — swipe_mode

- [ ] 📳 przekroczenie progu przeciągnięcia — `SwipeQuizCard.kt:89` (`positionalThreshold 0.7`) — tick „złapało"
- [ ] 📳 wylot karty (commit) — `SwipeQuizCard.kt:106-112` (`settledValue` → `onSubmit`) — „whoosh" tick
- [x] 🔊📳 poprawna / błędna — `SwipeModeVM.submitAnswer()` (`perform(ANSWER_CORRECT/ANSWER_WRONG)`)
- [x] 🔊📳 **nowy rekord combo** — `SwipeModeVM.finishQuiz()` (`isNewComboRecord` → `NEW_RECORD`)
- [ ] 🔊📳 duży feedback końcowy — `isLastAnswerFeedbackVisible` (ostatnia odpowiedź sesji)

## Poziom 2 — translation_mode

- [ ] 📳 wysłanie tłumaczenia — `TranslationQuizContent.kt:145` (przycisk Check / klawiatura Done → `OnSubmitAnswer`)
- [x] 🔊📳 poprawna / błędna — `TranslationQuizViewModel.submitAnswer()` (`perform(ANSWER_CORRECT/ANSWER_WRONG)`)
- [x] 🔊📳 koniec sesji — `TranslationQuizViewModel.finishQuiz()` (`perform(QUIZ_COMPLETED)`)

## Poziom 3 — nawigacja i ekran główny

- [ ] 📳 wybór trybu — `home/.../home_page/components/QuizModeButton.kt:29` (pokryte globalnym hookiem)
- [ ] 🔊📳 przyrost streaka / dzienny streak — `home/.../user_page/statistics/components/StreakTile.kt:39`;
      logika w `score` (`IncreaseStreakInstantlyUC`)
- [ ] 🔊📳 ukończenie „Ćwiczenia dnia" — `DailyExerciseVM.finishQuiz()`

## Poziom 4 — dialogi i akcje krytyczne (delikatnie)

- [ ] 📳 błąd — `core/.../composables/BaseDialogs.kt` `ErrorDialog` (error-pattern, bez dźwięku)
- [ ] 📳 potwierdzenie destrukcyjne — `ConfirmationDialog` (np. wyjście z quizu)
- [ ] 🔊📳 zgłoszenie błędu wysłane — `QuizSideEffect.ShowReportSuccess` (`MMQuizScreen.kt:59`)
- [ ] 🔊📳 udany zakup premium — `PurchaseCategoryDialog.onSuccessConfirm`

---

## Czego NIE ozdźwiękawiać / oharaptycznić

- każde wpisanie znaku w polu tekstowym
- scroll
- zwykłe „wstecz" / nawigacja bez znaczenia
- pojawienie się reklamy

---

## Priorytet MVP (80% wrażenia)

1. Poprawna/błędna odpowiedź (oba) — jeden mechanizm dla wszystkich 4 trybów
2. Koniec quizu (oba) — współdzielony `QuizFinishScreen`
3. Globalny lekki haptic na kliknięciach (`rememberDebouncedClick`)
4. Swipe: próg + wylot karty (haptic)
