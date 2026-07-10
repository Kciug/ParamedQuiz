# 09 — Tryb CEM

> Ten tryb korzysta ze [wspólnej mechaniki quizu](00-mechanika-quizu.md) (ocena odpowiedzi, precyzja, punkty, reklamy, ekran wyniku, wyjście). Poniżej opisano tylko elementy **specyficzne**.

## Metryka

| | |
|---|---|
| **Moduł** | `cem_mode` |
| **Typ** | Tryb gry |
| **Dostępność** | Część kategorii **darmowa**, reszta w **pełnym pakiecie** |
| **Powiązane featury** | [Wspólna mechanika quizu](00-mechanika-quizu.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) · [Punktacja i seria](11-punktacja-i-seria.md) |

---

## 1. Cel funkcji

Tryb CEM to quiz pytań w formule zbliżonej do egzaminu **CEM** (Centrum Egzaminów Medycznych). Merytorycznie działa jak tryb główny (pytania jednokrotnego/wielokrotnego wyboru), ale ma **hierarchiczną strukturę kategorii** (kategorie i podkategorie).

---

## 2. Dostęp i warunki

- Tryb dostępny dla wszystkich; **poszczególne kategorie** mogą być darmowe lub płatne.
- **Kategoria jest dostępna**, gdy jest **darmowa** **lub** użytkownik posiada **pełny pakiet**.
- W odróżnieniu od trybu głównego kategorie CEM **nie są kupowane pojedynczo** — odblokowuje je pełny pakiet (albo są darmowe).
- Przy pierwszym wejściu wyświetlany jest onboarding trybu (jednorazowo).

---

## 3. Przepływ użytkownika

### 3.1 Nawigacja po kategoriach (zagnieżdżonych)
1. Użytkownik wchodzi w tryb CEM i widzi listę **kategorii najwyższego poziomu**.
2. Kategoria może zawierać **podkategorie** — wejście w nią prezentuje kolejny poziom listy.
3. Kategoria z pytaniami uruchamia quiz; kategoria z podkategoriami prowadzi głębiej w strukturę.

### 3.2 Rozwiązywanie quizu
1. Po wybraniu dostępnej kategorii ładowany jest jej zestaw pytań (potasowany).
2. Rozgrywka przebiega zgodnie ze [wspólną mechaniką quizu](00-mechanika-quizu.md).
3. Po ostatnim pytaniu prezentowany jest ekran wyniku.

---

## 4. Reguły biznesowe (specyficzne dla trybu)

- **Struktura hierarchiczna:** kategorie mają relację nadrzędna–podrzędna (kategoria → podkategorie → pytania).
- **Dostęp:** kategoria darmowa albo pełny pakiet; brak zakupów pojedynczych kategorii.
- **Stan „oczekujący" (pending)** pełnego pakietu jest rozróżniany — dostęp aktywuje się po zatwierdzeniu płatności.
- **Aktualizacja serii:** poprawna aktywność może zaktualizować dzienną serię.
- **Aktualizacja treści „na żywo":** lista kategorii i pytania w trakcie sesji są odświeżane przy zmianach po stronie backendu.
- Ocena odpowiedzi, punkty, precyzja i reklamy — jak w [mechanice wspólnej](00-mechanika-quizu.md).

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Błąd/brak sieci przy ładowaniu kategorii | Ekran kategorii pokazuje stan błędu (z możliwością ponowienia). |
| Błąd/brak sieci przy ładowaniu pytań | Sesja pokazuje stan błędu zamiast pytań. |
| Wejście w zablokowaną kategorię | Prezentacja stanu płatnego / brak rozpoczęcia bez dostępu. |
| Odblokowanie pełnym pakietem w tle | Lista kategorii aktualizuje status dostępu bez ponownego wejścia. |
| Kategoria z podkategoriami | Wejście prowadzi do kolejnego poziomu listy zamiast rozpoczynać quiz. |
| Wyjście w trakcie sesji | Zgodnie z [mechaniką wspólną](00-mechanika-quizu.md). |

---

## 6. Powiązania

- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** — pełny opis przebiegu sesji.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — pełny pakiet.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — punkty i seria.
