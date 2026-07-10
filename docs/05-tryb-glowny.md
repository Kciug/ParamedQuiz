# 05 — Tryb główny (quiz kategoriami)

> Ten tryb korzysta ze [wspólnej mechaniki quizu](00-mechanika-quizu.md) (ocena odpowiedzi, precyzja, punkty, reklamy, ekran wyniku, wyjście). Poniżej opisano tylko elementy **specyficzne** dla trybu głównego.

## Metryka

| | |
|---|---|
| **Moduł** | `main_mode` |
| **Typ** | Tryb gry (podstawowy) |
| **Dostępność** | Część kategorii **darmowa**, część **płatna** (per kategoria lub w pełnym pakiecie) |
| **Powiązane featury** | [Wspólna mechanika quizu](00-mechanika-quizu.md) · [Ćwiczenie dnia](06-cwiczenie-dnia.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) · [Punktacja i seria](11-punktacja-i-seria.md) |

---

## 1. Cel funkcji

Tryb główny to podstawowy sposób nauki: użytkownik wybiera **kategorię** tematyczną i rozwiązuje quiz jednokrotnego/wielokrotnego wyboru z pytań tej kategorii. To rdzeń treści merytorycznych aplikacji.

---

## 2. Dostęp i warunki

- Tryb jest dostępny dla wszystkich; **poszczególne kategorie** mogą być darmowe lub płatne.
- **Kategoria jest dostępna**, gdy:
  - jest oznaczona jako **darmowa**, **lub**
  - użytkownik kupił **tę kategorię**, **lub**
  - użytkownik posiada **pełny pakiet**.
- Kategorie płatne, do których użytkownik nie ma dostępu, są **zablokowane** i oferują zakup.

---

## 3. Przepływ użytkownika

### 3.1 Ekran kategorii
1. Użytkownik wchodzi w tryb główny i widzi **listę kategorii** (tytuł, opis, liczba pytań, status dostępu).
2. Kategorie odblokowane można uruchomić; zablokowane prezentują kłódkę / stan płatny.

### 3.2 Rozwiązywanie quizu kategorii
1. Po wybraniu dostępnej kategorii ładowany jest zestaw jej pytań (potasowany).
2. Rozgrywka przebiega zgodnie ze [wspólną mechaniką quizu](00-mechanika-quizu.md): pytanie → zaznaczenie odpowiedzi → zatwierdzenie → informacja zwrotna → następne pytanie.
3. Po ostatnim pytaniu prezentowany jest ekran wyniku.

### 3.3 Zakup zablokowanej kategorii
1. Użytkownik wybiera zablokowaną kategorię → otwiera się **okno zakupu** (z ceną, jeśli dostępna).
2. Może kupić kategorię (proces płatności — patrz [Sklep i zakupy](14-sklep-i-zakupy.md)).
3. Po udanym zakupie kategoria zostaje odblokowana i można ją uruchomić.
4. Zakup **oczekujący** (pending) jest rozróżniany — dostęp aktywuje się po zatwierdzeniu płatności.

---

## 4. Reguły biznesowe (specyficzne dla trybu)

- **Odblokowanie per kategoria:** każda płatna kategoria ma własny produkt zakupowy; pełny pakiet odblokowuje wszystkie.
- **Aktualizacja serii:** poprawna aktywność w quizie może zaktualizować dzienną serię (patrz [Punktacja i seria](11-punktacja-i-seria.md)).
- **Aktualizacja treści „na żywo":** lista kategorii oraz pytania w trakcie sesji są odświeżane przy zmianach po stronie backendu (w tym zmiana statusu dostępu po zakupie).
- Ocena odpowiedzi, punkty, precyzja i reklamy — jak w [mechanice wspólnej](00-mechanika-quizu.md).

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Błąd/brak sieci przy ładowaniu kategorii | Ekran kategorii pokazuje stan błędu. |
| Błąd/brak sieci przy ładowaniu pytań kategorii | Sesja pokazuje stan błędu zamiast pytań. |
| Wejście w zablokowaną kategorię | Okno zakupu zamiast rozpoczęcia quizu. |
| Zakup anulowany / błąd płatności | Kategoria pozostaje zablokowana; komunikat błędu. |
| Zakup oczekujący (pending) | Dostęp nieaktywny do zatwierdzenia; stan oznaczony. |
| Odblokowanie kategorii w tle (np. pełny pakiet) | Lista kategorii aktualizuje status dostępu bez ponownego wejścia. |
| Wyjście w trakcie sesji | Zgodnie z [mechaniką wspólną](00-mechanika-quizu.md) (potwierdzenie; finalizacja przy ≥1 odpowiedzi). |

---

## 6. Powiązania

- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** — pełny opis przebiegu sesji.
- **[Ćwiczenie dnia](06-cwiczenie-dnia.md)** — wariant korzystający z pełnej puli pytań tego trybu.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — zakup kategorii / pełnego pakietu.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — punkty i seria.
