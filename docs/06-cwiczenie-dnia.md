# 06 — Ćwiczenie dnia (Daily Exercise)

> Ten tryb korzysta ze [wspólnej mechaniki quizu](00-mechanika-quizu.md). Poniżej opisano tylko elementy **specyficzne** dla ćwiczenia dnia.

## Metryka

| | |
|---|---|
| **Moduł** | `main_mode` (`daily_exercise`) |
| **Typ** | Codzienna, krótka aktywność (dodatek) |
| **Dostępność** | Wszyscy; **raz dziennie** |
| **Powiązane featury** | [Ekran główny](04-ekran-glowny.md) · [Tryb główny](05-tryb-glowny.md) · [Punktacja i seria](11-punktacja-i-seria.md) · [Wspólna mechanika quizu](00-mechanika-quizu.md) |

---

## 1. Cel funkcji

Ćwiczenie dnia to krótki, codzienny zestaw pytań mający budować **nawyk** regularnej nauki i podtrzymywać **serię** (streak). Losowany jest z pełnej puli pytań trybu głównego i celowo jest krótki (kilka pytań).

---

## 2. Dostęp i warunki

- Ćwiczenie jest dostępne **raz na dobę** (kalendarzowo).
- **Dostępność** ustalana jest na podstawie **daty ostatniego wykonania**:
  - jeśli nigdy nie wykonano → dostępne,
  - jeśli ostatnie wykonanie było **przed dzisiejszym dniem** → dostępne,
  - jeśli wykonano **dzisiaj** → niedostępne do następnego dnia.
- Wejście z [ekranu głównego](04-ekran-glowny.md) (dodatki). Gdy niedostępne, próba wejścia pokazuje komunikat „już wykonane".

---

## 3. Przepływ użytkownika

1. Na ekranie głównym element „Ćwiczenie dnia" jest **wyróżniony**, gdy dostępne.
2. Po wejściu ładowany jest **krótki, losowy** zestaw pytań (domyślnie **3**; liczba z [konfiguracji zdalnej](19-konfiguracja-zdalna.md)).
3. Rozgrywka przebiega zgodnie ze [wspólną mechaniką quizu](00-mechanika-quizu.md).
4. Po zakończeniu:
   - aktualizowana jest **seria** (jeśli dotyczy),
   - zapisywana jest **data wykonania** (dziś) — blokuje kolejne wejście tego dnia,
   - wynik jest utrwalany (wymuszona synchronizacja),
   - prezentowany jest ekran wyniku.

---

## 4. Reguły biznesowe (specyficzne dla trybu)

- **Jedna sesja dziennie:** po ukończeniu ćwiczenie jest niedostępne do następnego dnia (data ostatniego wykonania = dziś).
- **Dobór pytań:** losowe pytania z **całej puli** trybu głównego (bez podziału na kategorie), ograniczone do skonfigurowanej liczby.
- **Reklamy — brak progu zakończenia:** ćwiczenie dnia **ignoruje próg** reklamy zakończeniowej (ze względu na krótkość sesji); pozostałe zasady reklam jak w [mechanice wspólnej](00-mechanika-quizu.md).
- **Synchronizacja wyniku:** po zakończeniu następuje wymuszona synchronizacja stanu (punkty/postęp).

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Wejście po wykonaniu dzisiaj | Komunikat „już wykonane"; brak rozpoczęcia. |
| Błąd/brak sieci przy ładowaniu pytań | Sesja pokazuje stan błędu. |
| Zbyt mała pula pytań w backendzie | Zestaw ograniczony do dostępnej liczby pytań. |
| Wyjście przed zakończeniem | Zgodnie z [mechaniką wspólną](00-mechanika-quizu.md); data wykonania zapisywana dopiero przy pełnym zakończeniu. |
| Zmiana daty/strefy czasowej urządzenia | Dostępność liczona wg daty kalendarzowej urządzenia. |

---

## 6. Powiązania

- **[Ekran główny](04-ekran-glowny.md)** — wejście i wyróżnienie dostępności.
- **[Tryb główny](05-tryb-glowny.md)** — źródło puli pytań.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — aktualizacja serii i punktów.
- **[Konfiguracja zdalna](19-konfiguracja-zdalna.md)** — liczba pytań ćwiczenia.
