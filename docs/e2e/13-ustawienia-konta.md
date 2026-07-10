# E2E — Ustawienia konta

> Feature: [13-ustawienia-konta.md](../13-ustawienia-konta.md). Konwencja i statusy: [README](README.md).

### E2E-SETTINGS-01 — zmiana hasła (happy path)
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** zalogowane konto e-mail/hasło.
- **When:** użytkownik podaje poprawne stare hasło, nowe hasło (≥6 znaków, różne od starego) i identyczne powtórzenie; zatwierdza.
- **Then:** hasło zmienione; prezentowane potwierdzenie.

### E2E-SETTINGS-02 — walidacja zmiany hasła
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** dialog zmiany hasła.
- **When:** nowe hasło jest za krótkie / niezgodne z powtórzeniem / takie samo jak stare.
- **Then:** komunikat walidacyjny; brak zmiany hasła.

### E2E-SETTINGS-03 — zmiana nazwy użytkownika
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** zalogowane konto.
- **When:** użytkownik podaje nową (inną) nazwę i zatwierdza.
- **Then:** nazwa zaktualizowana; potwierdzenie; nazwa taka sama jak obecna → komunikat walidacyjny.

### E2E-SETTINGS-04 — skasowanie postępu zeruje progresję, konto zostaje
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** konto z niezerowym wynikiem/serią.
- **When:** użytkownik potwierdza skasowanie postępu.
- **Then:** wynik/seria/historia wyzerowane; użytkownik pozostaje zalogowany.

### E2E-SETTINGS-05 — usunięcie konta wymaga re-auth
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** zalogowane konto e-mail/hasło.
- **When:** użytkownik inicjuje usunięcie konta i podaje hasło.
- **Then:** po powodzeniu konto usunięte, użytkownik wyprowadzony z ekranu (wylogowany).

### E2E-SETTINGS-06 — ustawienia powiadomień
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** ekran ustawień.
- **When:** użytkownik włącza powiadomienia i ustawia godzinę, potem wyłącza.
- **Then:** przełącznik i godzina zapamiętane; włączenie planuje przypomnienia, wyłączenie je anuluje.
