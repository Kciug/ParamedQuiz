# E2E — Tryb Powtórek (Revisions)

> Feature: [10-tryb-powtorek.md](../10-tryb-powtorek.md). Konwencja i statusy: [README](README.md).

### E2E-REV-01 — konfiguracja i rozegranie sesji powtórek do wyniku
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** użytkownik ma historię odpowiedzianych pytań w trybie głównym (seed skuteczności per pytanie).
- **When:** wchodzi w powtórki; wybiera tryb źródłowy, kategorię, kryterium i rozmiar sesji; rozpoczyna i rozwiązuje sesję.
- **Then:** sesja startuje z pytaniami zgodnymi z konfiguracją; po zakończeniu pojawia się ekran wyniku.

### E2E-REV-02 — błędne pytanie wraca i wypada po 3 błędach
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** sesja z co najmniej jednym pytaniem.
- **When:** użytkownik odpowiada na dane pytanie błędnie 3 razy.
- **Then:** po 1. i 2. błędzie pytanie wraca do kolejki; po 3. jest usuwane i oznaczone jako niezaliczone w podsumowaniu.

### E2E-REV-03 — punkty i seria tylko za pierwsze podejście
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** sesja z pytaniem, na które użytkownik odpowie błędnie, potem (przy powtórce) poprawnie.
- **When:** przechodzi pierwsze podejście i fazę korekty.
- **Then:** punkty/seria naliczane wyłącznie przy pierwszym podejściu; powtórka nie dodaje punktów ani nie odnawia serii.

### E2E-REV-04 — kryterium „poniżej 50%" filtruje pulę
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** historia z pytaniami o różnej skuteczności (część <50%, część ≥50%).
- **When:** użytkownik wybiera kryterium „poniżej 50%".
- **Then:** do sesji trafiają tylko pytania ze skutecznością < 50%.

### E2E-REV-05 — tryb tłumaczeń niedostępny poniżej progu odpowiedzi
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** liczba odpowiedzianych pytań tłumaczeń poniżej progu (10).
- **When:** użytkownik przegląda wybór trybu źródłowego.
- **Then:** tryb tłumaczeń jest niedostępny do wyboru.

### E2E-REV-06 — brak kwalifikujących się pytań → stan pusty
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** konfiguracja, dla której nie ma kwalifikujących się pytań.
- **When:** użytkownik ustawia taką konfigurację.
- **Then:** prezentowany stan pusty; sesji nie da się rozpocząć.
