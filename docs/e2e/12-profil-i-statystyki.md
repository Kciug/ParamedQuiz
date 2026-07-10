# E2E — Profil i statystyki

> Feature: [12-profil-i-statystyki.md](../12-profil-i-statystyki.md). Konwencja i statusy: [README](README.md).

### E2E-PROFILE-01 — profil prezentuje tożsamość i podsumowanie progresji
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** zalogowany użytkownik z wynikiem, serią i historią pytań (seed).
- **When:** otwiera profil.
- **Then:** widoczne nazwa/e-mail, łączny wynik, seria, widok tygodniowy oraz zbiorcze liczby (poprawne/błędne/unikalne/idealne) i najlepsze combo.

### E2E-PROFILE-02 — przełączanie trybu w rankingu najlepsze/najsłabsze
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** wystarczająca liczba ocenionych pytań w co najmniej dwóch trybach.
- **When:** użytkownik przełącza tryb rankingu.
- **Then:** zestawienie najlepsze/najsłabsze przelicza się dla wybranego trybu.

### E2E-PROFILE-03 — stan „za mało danych" przy niedostatecznej historii
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** zbyt mało ocenionych pytań dla trybu.
- **When:** użytkownik otwiera zestawienie najlepsze/najsłabsze.
- **Then:** zamiast rankingu prezentowany jest stan „za mało danych".

### E2E-PROFILE-04 — wynik trybu bez danych oznaczony jako niedostępny
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** brak danych wyniku dla danego trybu.
- **When:** użytkownik przegląda wyniki per tryb.
- **Then:** wynik tego trybu oznaczony jako niedostępny.
