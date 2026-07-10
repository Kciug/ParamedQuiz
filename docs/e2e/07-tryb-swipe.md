# E2E — Tryb Swipe

> Feature: [07-tryb-swipe.md](../07-tryb-swipe.md). Konwencja i statusy: [README](README.md).

### E2E-SWIPE-01 — trial → wyczerpanie → panel zakończenia → zakup → pełny tryb
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** użytkownik bez zakupu Swipe; wersja próbna z kilkoma darmowymi pytaniami (seed).
- **When:** wchodzi w trial, ocenia wszystkie darmowe pytania; na panelu zakończenia triala inicjuje zakup (fake billing) i kończy go sukcesem.
- **Then:** po zakupie tryb automatycznie przeładowuje pełną pulę i kontynuuje jako pełny (bez ponownego wejścia).

### E2E-SWIPE-02 — poprawna ocena odpowiedzi i combo
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** pełny tryb Swipe (odblokowany), znane poprawne wartości pytań.
- **When:** użytkownik odpowiada kilka razy poprawnie, potem raz błędnie.
- **Then:** combo rośnie za kolejne poprawne i zeruje się po błędnej; liczniki poprawnych/combo aktualizują się.

### E2E-SWIPE-03 — nowy rekord combo w podsumowaniu
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** rekord combo użytkownika = R; pełny tryb.
- **When:** w sesji użytkownik osiąga combo > R i kończy sesję.
- **Then:** ekran wyniku sygnalizuje nowy rekord combo, a rekord jest utrwalony.

### E2E-SWIPE-04 — werdykt wystawiany dopiero po min. liczbie błędów
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** pełny tryb Swipe.
- **When:** użytkownik kończy sesję z liczbą błędów poniżej progu / powyżej progu.
- **Then:** poniżej progu — brak werdyktu; powyżej — werdykt (impulsywny/wahający) zgodny z relacją czasów.

### E2E-SWIPE-05 — onboarding trybu tylko przy pierwszym wejściu
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** pierwsze wejście w tryb Swipe.
- **When:** użytkownik wchodzi, potem wychodzi i wchodzi ponownie.
- **Then:** onboarding trybu pokazany raz; drugie wejście go pomija.

### E2E-SWIPE-06 — anulowanie zakupu pozostawia trial
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** panel zakończenia triala.
- **When:** użytkownik inicjuje zakup i go anuluje.
- **Then:** brak odblokowania; użytkownik pozostaje w kontekście triala; możliwość ponowienia.
