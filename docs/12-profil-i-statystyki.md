# 12 — Profil i statystyki użytkownika

## Metryka

| | |
|---|---|
| **Moduł** | `home` (`presentation/user_page`) |
| **Typ** | Ekran przeglądowy (profil + statystyki) |
| **Dostępność** | Wszyscy; część danych zależy od konta |
| **Powiązane featury** | [Punktacja i seria](11-punktacja-i-seria.md) · [Ustawienia konta](13-ustawienia-konta.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) · tryby gry |

---

## 1. Cel funkcji

Profil użytkownika prezentuje jego **tożsamość** (nazwa, e-mail, status konta i premium) oraz **statystyki nauki**: łączny wynik, serię, skuteczność w podziale na tryby, a także zestawienia najlepiej i najsłabiej opanowanych pytań. To centralny widok postępów.

Wejście: z [ekranu głównego](04-ekran-glowny.md) (awatar/panel użytkownika).

---

## 2. Prezentowane dane

### 2.1 Nagłówek profilu
- **Nazwa** i **e-mail** (dla zalogowanego konta), status **zalogowania**, oznaczenie **premium**.
- Dla użytkownika niezalogowanego dane osobowe konta nie są dostępne.

### 2.2 Podsumowanie progresji
- **Łączny wynik** (punkty) i **seria** (z jej stanem).
- **Widok tygodniowy serii** — które dni bieżącego tygodnia zostały zaliczone.
- Zbiorcze liczby: łączna liczba poprawnych i błędnych odpowiedzi, liczba **unikalnych** pytań widzianych oraz liczba pytań opanowanych **„idealnie"** (poprawnie i nigdy błędnie).
- **Najlepsze combo** w [trybie Swipe](07-tryb-swipe.md).

### 2.3 Wyniki per tryb
Dla trybów: **główny**, **Swipe**, **tłumaczeń**, **CEM** — prezentowane są osobno:
- wynik trybu, liczba poprawnych odpowiedzi i łączna liczba odpowiedzi,
- oraz **wynik ogólny** (zbiorczy), o ile dostępny.
- Wynik danego trybu jest oznaczany jako „niedostępny", dopóki nie ma dla niego danych.

### 2.4 Najlepsze / najsłabsze pytania
- Dla wybranego trybu prezentowane są zestawienia **najlepiej** i **najsłabiej** opanowanych pytań (po kilka pozycji każde).
- Użytkownik może **przełączać tryb**, dla którego liczone jest zestawienie.
- Zestawienia pojawiają się dopiero, gdy jest **wystarczająco dużo danych** (odpowiednia liczba ocenionych pytań); w przeciwnym razie prezentowany jest stan „za mało danych".

---

## 3. Reguły biznesowe

- **Źródło statystyk:** wszystkie statystyki wyliczane są z [historii pytań i punktacji](11-punktacja-i-seria.md) (widziane/poprawne per pytanie, wynik, seria, rekordy).
- **Skuteczność pytania:** podstawą rankingów najlepsze/najsłabsze jest stosunek poprawnych do wszystkich prób danego pytania.
- **Próg prezentacji rankingów:** zestawienia najlepsze/najsłabsze wymagają minimalnej liczby ocenionych pytań, by były sensowne.
- **Zależność od konta:** dane tożsamości wymagają zalogowania; statystyki progresji bazują na danych progresji (lokalnych lub z konta).
- **Widok tygodniowy** odzwierciedla bieżący tydzień na podstawie serii i daty ostatniego podbicia.

---

## 4. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Użytkownik niezalogowany | Brak danych tożsamości; statystyki wg dostępnych danych progresji. |
| Brak danych dla trybu | Wynik trybu oznaczony jako niedostępny. |
| Za mało ocenionych pytań | Rankingi najlepsze/najsłabsze zastąpione stanem „za mało danych". |
| Błąd/brak sieci przy pobraniu danych | Prezentacja ostatnich znanych danych / stan błędu dla sekcji, których nie udało się pobrać. |
| Przełączanie trybu rankingu | Zestawienie przeliczane dla wybranego trybu. |

---

## 5. Powiązania

- **[Punktacja i seria](11-punktacja-i-seria.md)** — źródło wszystkich statystyk.
- **[Ustawienia konta](13-ustawienia-konta.md)** — dostęp do zarządzania kontem z obszaru profilu.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — status premium.
- **Tryby gry** — wyniki i skuteczność per tryb.
