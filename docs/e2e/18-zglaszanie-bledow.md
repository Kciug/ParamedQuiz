# E2E — Zgłaszanie błędów i feedback

> Feature: [18-zglaszanie-bledow.md](../18-zglaszanie-bledow.md). Konwencja i statusy: [README](README.md).

### E2E-REPORT-01 — zgłoszenie problemu z pytaniem z poziomu quizu
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** trwająca sesja quizu z widocznym pytaniem.
- **When:** użytkownik otwiera zgłoszenie, wpisuje opis i wysyła.
- **Then:** prezentowane potwierdzenie; okno zamknięte i pole opisu wyczyszczone; zgłoszenie zawiera kontekst pytania (id/treść/tryb).

### E2E-REPORT-02 — błąd wysyłki pozostawia okno otwarte
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** backend zwraca błąd zapisu zgłoszenia.
- **When:** użytkownik wysyła zgłoszenie.
- **Then:** zgłoszenie niewysłane; okno pozostaje otwarte z możliwością ponowienia.

### E2E-REPORT-03 — kontekst pytania zgodny z widocznym w chwili otwarcia
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** sesja quizu.
- **When:** użytkownik otwiera zgłoszenie dla bieżącego pytania.
- **Then:** dołączony kontekst dotyczy pytania widocznego w chwili otwarcia zgłoszenia.
