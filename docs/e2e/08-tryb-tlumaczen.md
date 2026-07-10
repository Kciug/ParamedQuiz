# E2E — Tryb Tłumaczeń

> Feature: [08-tryb-tlumaczen.md](../08-tryb-tlumaczen.md). Konwencja i statusy: [README](README.md).

### E2E-TRANS-01 — rozegranie sesji tłumaczeń do ekranu wyniku
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** tryb odblokowany (zakup/pełny pakiet); seed pytań z listą tłumaczeń.
- **When:** użytkownik wpisuje odpowiedzi i zatwierdza kolejne pytania do końca.
- **Then:** ekran wyniku z liczbą pytań, poprawnych i punktami; reklamy nie pojawiają się w trakcie.

### E2E-TRANS-02 — dopasowanie odpowiedzi bez wielkości liter i spacji
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** pytanie z listą dopuszczalnych tłumaczeń.
- **When:** użytkownik wpisuje poprawne tłumaczenie z inną wielkością liter i spacjami wiodącymi/końcowymi.
- **Then:** odpowiedź uznana za poprawną.

### E2E-TRANS-03 — odpowiedź jest ostateczna po zatwierdzeniu
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** bieżące pytanie w sesji.
- **When:** użytkownik zatwierdza odpowiedź, po czym próbuje ją zmienić.
- **Then:** pole jest zablokowane; wynik pytania nie zmienia się.

### E2E-TRANS-04 — synonim spoza listy uznany za błędny
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** pytanie z zamkniętą listą tłumaczeń.
- **When:** użytkownik wpisuje sensowny synonim, którego nie ma na liście.
- **Then:** odpowiedź uznana za błędną (0 punktów).

### E2E-TRANS-05 — dostęp tylko po odblokowaniu
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** użytkownik bez zakupu trybu i bez pełnego pakietu.
- **When:** próbuje wejść w tryb tłumaczeń z ekranu głównego.
- **Then:** prezentowany panel zakupu zamiast rozgrywki (brak wersji próbnej).
