# 11 — Punktacja, punkty i seria (streak)

## Metryka

| | |
|---|---|
| **Moduł** | `score` |
| **Typ** | Warstwa wspólna (system progresji) |
| **Dostępność** | Wszyscy (także bez konta — dane lokalne do czasu rejestracji) |
| **Powiązane featury** | [Wspólna mechanika quizu](00-mechanika-quizu.md) · wszystkie tryby gry · [Profil i statystyki](12-profil-i-statystyki.md) · [Ustawienia konta](13-ustawienia-konta.md) · [Powiadomienia](16-powiadomienia.md) |

---

## 1. Cel funkcji

System punktacji i serii to wspólna warstwa progresji użytkownika: nalicza **punkty** za odpowiedzi, utrzymuje dzienną **serię** (streak), zbiera **historię pytań** (co i jak dobrze użytkownik opanował) oraz **rekordy** (np. najlepsze combo w trybie Swipe). Dane te zasilają wszystkie tryby, [profil/statystyki](12-profil-i-statystyki.md) oraz [powtórki](10-tryb-powtorek.md).

---

## 2. Dane progresji użytkownika

| Dane | Znaczenie |
|------|-----------|
| **Punkty (score)** | Łączna liczba zdobytych punktów. |
| **Seria (streak)** | Liczba kolejnych dni z zaliczoną aktywnością. |
| **Data ostatniego podbicia serii** | Służy do wyznaczania stanu serii i jej wygaśnięcia. |
| **Data ostatniego ćwiczenia dnia** | Steruje dostępnością [ćwiczenia dnia](06-cwiczenie-dnia.md). |
| **Historia pytań (seen questions)** | Per pytanie: ile razy widziane i ile razy poprawnie. |
| **Najlepsze combo Swipe** | Rekord serii poprawnych w [trybie Swipe](07-tryb-swipe.md). |

---

## 3. Punkty

- **Poprawna odpowiedź:** punkty dodatnie.
  - **Pierwsza w historii** poprawna odpowiedź na dane pytanie → **bonus** (domyślnie **300**).
  - Kolejna poprawna odpowiedź na to pytanie → wartość podstawowa (domyślnie **100**).
- **Błędna odpowiedź:** 0 punktów.
- Wartości pochodzą z [konfiguracji zdalnej](19-konfiguracja-zdalna.md) (domyślne = fallback).
- Punkty są **wspólne** dla wszystkich trybów i sumują się do jednego wyniku.

---

## 4. Seria (streak)

### 4.1 Stany serii
| Stan | Warunek | Znaczenie |
|------|---------|-----------|
| **DONE** | Seria podbita **dzisiaj** | Dzień zaliczony. |
| **PENDING** | Ostatnie podbicie **wczoraj** | Seria „w toku" — wymaga aktywności dziś, inaczej wygaśnie. |
| **MISSED** | Ostatnie podbicie **wcześniej** (lub brak) | Seria przepadła i jest zerowana. |

### 4.2 Zasady naliczania
- Seria rośnie o **1** za każdy dzień z zaliczoną aktywnością i podbijana jest **maksymalnie raz dziennie**.
- Jeśli od ostatniego podbicia minął więcej niż jeden dzień → seria jest **zerowana** (przejście w MISSED).
- **Moment zaliczenia dnia zależy od trybu:**
  - część trybów (np. główny, CEM, Swipe) zalicza dzień po osiągnięciu **progu aktywności** w sesji (próg z [konfiguracji zdalnej](19-konfiguracja-zdalna.md), domyślnie **3**),
  - tryb **tłumaczeń** zalicza dzień po odpowiedniej liczbie **poprawnych** odpowiedzi,
  - **ćwiczenie dnia** i **powtórki** zaliczają dzień **bezpośrednio** (za wykonanie).
- W [powtórkach](10-tryb-powtorek.md) seria może zostać podbita tylko przy **pierwszym podejściu** do pytania.

---

## 5. Historia pytań i rekordy

- Dla każdego pytania zapisywane jest, **ile razy** było widziane i **ile razy** odpowiedziano poprawnie.
- Historia ta jest podstawą:
  - metryki „pierwszej poprawnej" (bonus punktowy),
  - [statystyk profilu](12-profil-i-statystyki.md) (najlepsze/najgorsze pytania, skuteczność per tryb),
  - kwalifikacji pytań do [powtórek](10-tryb-powtorek.md).
- **Najlepsze combo Swipe** jest utrwalane jako rekord i pokazywane w profilu.

---

## 6. Przechowywanie i synchronizacja

- Dane progresji są utrzymywane **lokalnie** i **synchronizowane** z kontem użytkownika (z opóźnieniem/debounce; możliwa jest też synchronizacja wymuszona, np. po ćwiczeniu dnia).
- **Bez konta** progresja działa **lokalnie**. Przy **rejestracji** lokalny dorobek jest przenoszony na konto.
- Przy **logowaniu** dane konta są pobierane; przy **wylogowaniu** dane są synchronizowane i czyszczone lokalnie.
- Skasowanie postępu lub usunięcie konta czyści dane progresji (patrz [Ustawienia konta](13-ustawienia-konta.md)).

---

## 7. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Aktywność następnego dnia po PENDING | Seria podbita (+1), stan DONE. |
| Przerwa dłuższa niż 1 dzień | Seria wyzerowana (MISSED). |
| Wielokrotna aktywność tego samego dnia | Seria podbijana tylko raz; kolejne aktywności nie zwiększają jej ponownie. |
| Brak sieci | Progresja liczona lokalnie; synchronizacja nastąpi później. |
| Rejestracja po grze bez konta | Lokalny dorobek przenoszony na konto. |
| Powtórki — kolejne podejścia do pytania | Brak dodatkowych punktów i brak ponownego podbicia serii. |

---

## 8. Powiązania

- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** — moment naliczania punktów/serii.
- **[Profil i statystyki](12-profil-i-statystyki.md)** — prezentacja punktów, serii, rekordów i statystyk.
- **[Powtórki](10-tryb-powtorek.md)** — wykorzystanie historii pytań.
- **[Konfiguracja zdalna](19-konfiguracja-zdalna.md)** — wartości punktów i próg serii.
- **[Ustawienia konta](13-ustawienia-konta.md)** — kasowanie postępu / usunięcie konta.
