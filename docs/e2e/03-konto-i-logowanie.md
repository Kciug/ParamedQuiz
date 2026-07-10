# E2E — Konto i logowanie

> Feature: [03-konto-i-logowanie.md](../03-konto-i-logowanie.md). Konwencja i statusy: [README](README.md).

### E2E-AUTH-01 — rejestracja e-mail + hasło (happy path)
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** użytkownik niezalogowany, na ekranie rejestracji; e-mail nie jest zajęty.
- **When:** podaje nazwę, e-mail, hasło i identyczne powtórzenie hasła; zatwierdza.
- **Then:** rejestracja się powodzi; użytkownik uwierzytelniony i wraca do miejsca startu.

### E2E-AUTH-02 — logowanie e-mail + hasło (happy path)
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** istnieje konto o poprawnych danych; użytkownik na ekranie logowania.
- **When:** podaje poprawny e-mail i hasło; zatwierdza.
- **Then:** logowanie się powodzi; sesja aktywna; powrót do ekranu wyjścia.

### E2E-AUTH-03 — przycisk rejestracji nieaktywny przy niezgodnych hasłach
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** ekran rejestracji.
- **When:** hasło i powtórzenie różnią się (lub hasło puste).
- **Then:** przycisk rejestracji pozostaje nieaktywny.

### E2E-AUTH-04 — reset hasła wysyła e-mail
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** ekran resetu hasła.
- **When:** użytkownik podaje e-mail i zatwierdza.
- **Then:** prezentowane jest potwierdzenie wysłania instrukcji resetu.

### E2E-AUTH-05 — błędne dane logowania pokazują komunikat
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** ekran logowania.
- **When:** użytkownik podaje błędny e-mail/hasło.
- **Then:** komunikat o błędzie; brak aktywnej sesji.

### E2E-AUTH-06 — korzystanie bez konta jest możliwe
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** świeży start, użytkownik pomija logowanie.
- **When:** korzysta z aplikacji.
- **Then:** dostęp do funkcji niewymagających konta; funkcje zależne od tożsamości pozostają ograniczone do zalogowania.
