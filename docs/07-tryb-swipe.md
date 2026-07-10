# 07 — Tryb Swipe

## Metryka

| | |
|---|---|
| **Moduł** | `swipe_mode` |
| **Typ** | Tryb gry |
| **Dostępność** | Wersja próbna (darmowa) + pełny tryb płatny |
| **Powiązane featury** | [Punktacja i seria](11-punktacja-i-seria.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) · [Reklamy](15-reklamy.md) · [Zgłaszanie błędów](18-zglaszanie-bledow.md) · [Onboarding](01-onboarding.md) |

---

## 1. Cel funkcji

Tryb Swipe to szybka, „lekka" forma ćwiczenia opartego o ocenę stwierdzeń **prawda/fałsz**. Użytkownikowi wyświetlana jest karta z tezą medyczną, a on ocenia ją gestem przesunięcia karty (swipe) w jedną ze stron. Tryb nastawiony jest na tempo, refleks i budowanie serii poprawnych odpowiedzi (combo), a nie na dogłębną analizę — stanowi uzupełnienie trybu głównego.

Dodatkowo tryb zbiera **statystyki jakościowe sesji** (czasy odpowiedzi, typy błędów), na podstawie których wystawia użytkownikowi „werdykt" o jego stylu odpowiadania.

---

## 2. Dostęp i warunki

- Tryb jest **płatny**, ale udostępnia **wersję próbną (trial)** dostępną bez zakupu.
- **Wersja próbna** obejmuje wyłącznie pytania oznaczone jako **darmowe** (`free`). Reszta puli jest niedostępna do momentu zakupu.
- **Pełny tryb** odblokowuje się przez:
  - zakup samego trybu Swipe, **lub**
  - posiadanie **pełnego pakietu** (odblokowuje wszystkie płatne treści).
- Po zakupie (lub gdy zakup jest już posiadany) trial zostaje **automatycznie przełączony** w pełny tryb w trakcie działania — bez konieczności ponownego wchodzenia.
- Przy **pierwszym wejściu** do trybu wyświetlany jest onboarding tego trybu (jednorazowo; kolejne wejścia go pomijają).

### Reklamy a dostęp
- Dla użytkowników **bez premium** w trybie pojawiają się reklamy pełnoekranowe (patrz [Reklamy](15-reklamy.md)).
- Posiadanie premium/pełnego pakietu wyłącza reklamy.

---

## 3. Przepływ użytkownika

### 3.1 Wejście i sesja (happy path)
1. Użytkownik wchodzi w tryb Swipe (z ekranu głównego / sklepu).
2. Przy pierwszym wejściu widzi onboarding; następnie ładowana jest pula pytań (wersja próbna albo pełna, zależnie od statusu zakupu).
3. Pytania prezentowane są jako **stos kart**; widoczna jest bieżąca karta i podgląd następnej pod spodem.
4. Dla każdej karty użytkownik decyduje: stwierdzenie jest **prawdziwe** albo **fałszywe** (gest przesunięcia karty).
5. Po odpowiedzi natychmiast pojawia się **informacja zwrotna** (poprawnie / błędnie), aktualizują się liczniki (poprawne, combo, punkty), po czym pojawia się kolejna karta.
6. Sesja trwa do wyczerpania puli pytań. Po ostatnim pytaniu prezentowana jest końcowa informacja zwrotna, a następnie **ekran podsumowania**.

### 3.2 Podsumowanie sesji
Ekran końcowy prezentuje m.in.:
- liczbę przejrzanych pytań i poprawnych odpowiedzi,
- zdobyte punkty oraz łączny wynik użytkownika,
- informację o aktualizacji serii (streak),
- **rekord combo** oraz informację, czy w tej sesji padł nowy rekord,
- statystyki czasowe i jakościowe (patrz Reguły 5.3),
- „momentum strip" — uporządkowaną historię trafień/pomyłek w sesji.

### 3.3 Zakończenie wersji próbnej
- Gdy w **wersji próbnej** wyczerpią się darmowe pytania, zamiast standardowego podsumowania pojawia się **panel zakończenia triala** z zachętą do zakupu pełnego trybu (oraz ceną, jeśli dostępna).
- Z panelu użytkownik może zainicjować zakup lub wyjść.

### 3.4 Zakup w trakcie triala
1. Użytkownik inicjuje zakup (z panelu triala).
2. Uruchamiany jest proces płatności (patrz [Sklep i zakupy](14-sklep-i-zakupy.md)).
3. Po udanym zakupie tryb **automatycznie przeładowuje pełną pulę pytań** i kontynuuje jako pełny.
4. Zakup w stanie **oczekującym** (pending) sygnalizowany jest osobno; pełny dostęp aktywuje się po zatwierdzeniu płatności.

### 3.5 Wyjście z sesji
- Próba wyjścia w trakcie sesji wyświetla **potwierdzenie wyjścia**.
- Jeśli użytkownik nie odpowiedział jeszcze na żadne pytanie — wyjście następuje od razu (bez podsumowania).
- Jeśli odpowiedział na co najmniej jedno pytanie — sesja jest **finalizowana** (naliczone punkty/statystyki pozostają), użytkownik trafia do podsumowania.

### 3.6 Zgłoszenie problemu z pytaniem
- Z poziomu sesji użytkownik może zgłosić problem z aktualnie widocznym pytaniem (treść pytania dołączana jest automatycznie, tryb oznaczany jako „Swipe Mode").
- Po udanym zgłoszeniu wyświetlane jest potwierdzenie. Szczegóły: [Zgłaszanie błędów](18-zglaszanie-bledow.md).

---

## 4. Reguły biznesowe

### 4.1 Ocena odpowiedzi
- Każde pytanie ma zdefiniowaną poprawną wartość logiczną (prawda/fałsz).
- Odpowiedź jest **poprawna**, gdy ocena użytkownika zgadza się z wartością pytania.

### 4.2 Combo (seria poprawnych w sesji)
- **Combo** rośnie o 1 za każdą kolejną poprawną odpowiedź.
- Błędna odpowiedź **zeruje** combo.
- Najwyższe combo w sesji porównywane jest z **rekordem combo** użytkownika; nowy rekord jest zapisywany trwale i sygnalizowany w podsumowaniu.

### 4.3 Punkty
- Za poprawną odpowiedź naliczane są punkty (domyślnie **100**).
- Za **pierwszą w historii** poprawną odpowiedź na dane pytanie przyznawany jest bonus (domyślnie **300** zamiast 100).
- Za błędną odpowiedź punkty nie są naliczane.
- Wartości punktowe pochodzą z [konfiguracji zdalnej](19-konfiguracja-zdalna.md) (domyślne to fallback).
- Punkty i historia „widzianych pytań" są wspólne z resztą aplikacji (patrz [Punktacja i seria](11-punktacja-i-seria.md)).

### 4.4 Seria (streak)
- Poprawna aktywność w sesji może **zaktualizować dzienną serię** użytkownika (streak). Fakt aktualizacji odnotowywany jest w podsumowaniu.
- Szczegółowe reguły serii: [Punktacja i seria](11-punktacja-i-seria.md).

### 4.5 Reklamy w sesji
- Reklama pełnoekranowa może pojawić się **co ustaloną liczbę odpowiedzi** (domyślnie co 20) oraz **na wyjściu/zakończeniu** po przekroczeniu progu liczby odpowiedzi (domyślnie 10).
- Progi pochodzą z konfiguracji zdalnej. Reklamy nie dotyczą użytkowników premium.

### 4.6 Aktualizacja treści „na żywo"
- Pula pytań jest nasłuchiwana pod kątem aktualizacji — jeśli treść pytania zmieni się po stronie backendu w trakcie sesji, dane widocznych pytań są odświeżane.

---

## 5. Statystyki jakościowe i werdykt

### 5.1 Typy błędów
Tryb rozróżnia dwa rodzaje pomyłek:
- **Błąd typu 1** — stwierdzenie było **prawdziwe**, a użytkownik ocenił je jako fałszywe (przeoczenie prawdy).
- **Błąd typu 2** — stwierdzenie było **fałszywe**, a użytkownik ocenił je jako prawdziwe (fałszywy alarm).

### 5.2 Statystyki czasowe
Sesja mierzy m.in.:
- średni czas odpowiedzi (ogółem, dla poprawnych, dla błędnych),
- najszybszą poprawną odpowiedź,
- łączny czas trwania sesji.

### 5.3 Werdykt stylu odpowiadania
- Na podstawie **stosunku czasu** odpowiedzi błędnych do poprawnych aplikacja wystawia werdykt jakościowy:
  - odpowiedzi błędne wyraźnie **szybsze** niż poprawne → styl **impulsywny**,
  - odpowiedzi błędne wyraźnie **wolniejsze** niż poprawne → styl **niepewny/wahający się**.
- Werdykt jest wystawiany dopiero po osiągnięciu **minimalnej liczby błędów** (domyślnie 3) — przy mniejszej liczbie pomyłek brak wystarczających danych.

---

## 6. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Brak połączenia / błąd ładowania pytań | Sesja pokazuje stan błędu zamiast pytań; brak rozgrywki. |
| Pusta pula pytań (pełny tryb) | Brak pytań do rozegrania — sesja kończy się bez treści. |
| Wersja próbna z pustą pulą darmowych pytań | Brak pytań triala; efektywnie brak sesji próbnej. |
| Wyjście przed pierwszą odpowiedzią | Natychmiastowe wyjście, bez podsumowania i bez naliczeń. |
| Wyjście po części pytań | Sesja finalizowana z dotychczasowym wynikiem; przejście do podsumowania. |
| Zakup anulowany | Powrót do stanu triala bez zmian; użytkownik może spróbować ponownie. |
| Zakup w stanie oczekującym (pending) | Pełny dostęp nieaktywny do zatwierdzenia; stan oczekiwania sygnalizowany. |
| Błąd płatności | Komunikat o błędzie; trial pozostaje aktywny. |
| Odblokowanie trybu w trakcie triala | Automatyczne przejście w pełny tryb i przeładowanie pełnej puli pytań. |

---

## 7. Powiązania

- **[Punktacja i seria](11-punktacja-i-seria.md)** — punkty, „pierwsza poprawna", rekord combo, aktualizacja streaka.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — zakup trybu / pełnego pakietu, statusy zakupu, cena.
- **[Reklamy](15-reklamy.md)** — częstotliwość i wyzwalanie reklam, zależność od premium.
- **[Konfiguracja zdalna](19-konfiguracja-zdalna.md)** — parametry punktów i częstotliwości reklam.
- **[Zgłaszanie błędów](18-zglaszanie-bledow.md)** — zgłaszanie problemów z pytaniem.
- **[Onboarding](01-onboarding.md)** — onboarding trybu przy pierwszym wejściu.
