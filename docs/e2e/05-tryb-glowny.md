# E2E — Tryb główny

> Feature: [05-tryb-glowny.md](../05-tryb-glowny.md) · mechanika: [00-mechanika-quizu.md](../00-mechanika-quizu.md). Konwencja i statusy: [README](README.md).

### E2E-MAIN-01 — rozegranie quizu kategorii do ekranu wyniku
- **Priorytet:** P0
- **Status:** ✅ zautomatyzowany — `app/src/test/.../e2e/MainModeQuizHarnessTest`
- **Given:** darmowa kategoria z kilkoma pytaniami (seed); reklamy wyłączone.
- **When:** użytkownik wchodzi w kategorię; dla każdego pytania zaznacza odpowiedź i zatwierdza; przechodzi do końca.
- **Then:** po ostatnim pytaniu pojawia się ekran wyniku z liczbą pytań, poprawnych odpowiedzi i zdobytymi punktami.

### E2E-MAIN-02 — zablokowana kategoria otwiera okno zakupu
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** płatna kategoria, do której użytkownik nie ma dostępu (brak pełnego pakietu).
- **When:** użytkownik wybiera zablokowaną kategorię.
- **Then:** zamiast quizu otwiera się okno zakupu (z ceną, jeśli dostępna).

### E2E-MAIN-03 — poprawne naliczenie punktów za poprawną odpowiedź
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** pytanie odpowiadane po raz pierwszy w historii.
- **When:** użytkownik zaznacza dokładnie poprawny zbiór odpowiedzi i zatwierdza.
- **Then:** naliczony jest bonus za pierwszą poprawną odpowiedź (wg konfiguracji); wynik rośnie odpowiednio.

### E2E-MAIN-04 — odblokowanie kategorii po zakupie pełnego pakietu
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** użytkownik kupuje pełny pakiet (fake billing).
- **When:** wraca na listę kategorii trybu głównego.
- **Then:** wcześniej zablokowane kategorie są odblokowane bez ponownego wejścia.

### E2E-MAIN-05 — wyjście po części pytań finalizuje sesję
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** trwająca sesja, użytkownik odpowiedział na ≥1 pytanie.
- **When:** próbuje wyjść i potwierdza.
- **Then:** sesja finalizowana; prezentowany ekran wyniku z dotychczasowym wynikiem.

### E2E-MAIN-06 — błąd ładowania pokazuje stan błędu
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** backend zwraca błąd dla pytań kategorii.
- **When:** użytkownik wchodzi w kategorię.
- **Then:** ekran pokazuje stan błędu zamiast pytań.
