# E2E — Ćwiczenie dnia

> Feature: [06-cwiczenie-dnia.md](../06-cwiczenie-dnia.md). Konwencja i statusy: [README](README.md).

### E2E-DAILY-01 — ćwiczenie dnia podbija serię i blokuje się do jutra
- **Priorytet:** P0
- **Status:** ✅ zautomatyzowany — `app/src/test/.../e2e/DailyExerciseHarnessTest` (z kontrolowanym `TimeProvider`)
- **Given:** ćwiczenie dnia dostępne (nie wykonano dziś); sterowalny czas ustawiony na „dziś".
- **When:** użytkownik wchodzi w ćwiczenie dnia, rozwiązuje zestaw do końca.
- **Then:** seria zostaje podbita; data wykonania ustawiona na dziś; po powrocie element „ćwiczenie dnia" nie jest już wyróżniony, a ponowne wejście pokazuje komunikat „już wykonane".

### E2E-DAILY-02 — dostępność odnawia się następnego dnia
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** ćwiczenie wykonane „wczoraj" (sterowalny czas).
- **When:** czas przesuwa się na „dziś"; użytkownik wchodzi na ekran główny.
- **Then:** ćwiczenie dnia znów dostępne i wyróżnione.

### E2E-DAILY-03 — rozmiar zestawu zgodny z konfiguracją
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** konfiguracja liczby pytań ćwiczenia = N; pula pytań ≥ N.
- **When:** użytkownik rozpoczyna ćwiczenie dnia.
- **Then:** zestaw liczy dokładnie N pytań.

### E2E-DAILY-04 — zbyt mała pula ogranicza zestaw
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** pula pytań mniejsza niż skonfigurowana liczba.
- **When:** użytkownik rozpoczyna ćwiczenie.
- **Then:** zestaw ograniczony do dostępnej liczby pytań (bez błędu).
