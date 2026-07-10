# E2E — Ocena aplikacji

> Feature: [17-ocena-aplikacji.md](../17-ocena-aplikacji.md). Konwencja i statusy: [README](README.md).

### E2E-RATING-01 — wysoka ocena kieruje do oceny w sklepie
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** użytkownik spełnia kryteria prośby (≥3 dni od instalacji, ≥5 ukończonych quizów, odstęp od ostatniej prośby); prośba widoczna na ekranie głównym.
- **When:** wybiera ocenę ≥ 4.
- **Then:** prezentowana jest zachęta do oceny w sklepie; wybór uruchamia proces oceny; prośba nie wraca.

### E2E-RATING-02 — niska ocena kieruje do prywatnego feedbacku
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** widoczna prośba o ocenę.
- **When:** użytkownik wybiera ocenę < 4 i wysyła feedback.
- **Then:** prezentowany formularz feedbacku; po wysłaniu aplikacja traktuje prośbę jako obsłużoną (nie wraca).

### E2E-RATING-03 — „nie pytaj więcej" trwale wyłącza prośby
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** widoczna prośba o ocenę.
- **When:** użytkownik wybiera „nie pytaj więcej".
- **Then:** prośba znika i nie pojawia się ponownie.
