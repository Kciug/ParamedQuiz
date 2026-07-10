# 00 — Wspólna mechanika quizu (podstawa)

> Dokument **referencyjny** opisujący mechanikę wspólną dla trybów opartych o pytania. Poszczególne tryby (Główny, CEM, Powtórki, Swipe, Tłumaczeń) dziedziczą ten model i **modyfikują** wybrane elementy — różnice opisano w dokumentach danych trybów. Ten plik pozwala nie powtarzać tych samych reguł w każdym trybie.

## Metryka

| | |
|---|---|
| **Moduł** | `main_mode` (`quiz_base`), `core` (ekran wyniku), `score` (punkty) |
| **Typ** | Podstawa wspólna (cross-cutting) |
| **Dotyczy** | [Tryb główny](05-tryb-glowny.md) · [Ćwiczenie dnia](06-cwiczenie-dnia.md) · [CEM](09-tryb-cem.md) · [Powtórki](10-tryb-powtorek.md) · częściowo [Swipe](07-tryb-swipe.md) i [Tłumaczeń](08-tryb-tlumaczen.md) |
| **Powiązane featury** | [Punktacja i seria](11-punktacja-i-seria.md) · [Reklamy](15-reklamy.md) · [Zgłaszanie błędów](18-zglaszanie-bledow.md) |

---

## 1. Model sesji quizu

Sesja quizu to przejście przez **listę pytań** (zwykle potasowaną). W trakcie sesji śledzone są:
- bieżące pytanie i jego numer,
- liczba udzielonych odpowiedzi,
- liczba odpowiedzi poprawnych,
- czas odpowiedzi (per pytanie oraz zbiorczo),
- zdobyte w sesji punkty,
- czy w tej sesji zaktualizowano dzienną serię (streak).

---

## 2. Standardowy przepływ pytania (klasyczny quiz wyboru)

1. **Wyświetlenie pytania** — pokazywane jest pytanie z odpowiedziami; uruchamiany jest pomiar czasu.
2. **Wybór odpowiedzi** — użytkownik zaznacza jedną lub więcej odpowiedzi (zaznaczenie można przełączać).
3. **Zatwierdzenie** — po zatwierdzeniu odpowiedź jest oceniana; pokazywana jest informacja zwrotna (co było poprawne/błędne), naliczane są punkty i aktualizowane liczniki.
4. **Następne pytanie** — użytkownik przechodzi dalej; może pojawić się reklama (patrz sekcja 6).
5. **Zakończenie** — po ostatnim pytaniu prezentowany jest **ekran wyniku**.

> Tryb **Swipe** i **Tłumaczeń** mają odmienny sposób udzielania odpowiedzi (przesunięcie karty / wpisanie tłumaczenia), ale zachowują ten sam szkielet: pytanie → odpowiedź → ocena → wynik.

---

## 3. Ocena odpowiedzi (quiz wielokrotnego wyboru)

- Pytanie może mieć **jedną lub wiele** poprawnych odpowiedzi.
- Odpowiedź jest **poprawna** tylko wtedy, gdy zaznaczony zbiór odpowiedzi **dokładnie** odpowiada zbiorowi poprawnych (wszystkie poprawne zaznaczone i **żadna** błędna).
- **Precyzja** (metryka jakościowa per pytanie): stosunek trafnie zaznaczonych do sumy (zaznaczone ∪ poprawne), wyrażony w procentach. Dla sesji liczona jest **średnia precyzja**.
  - Przykład: 2 poprawne, użytkownik zaznaczył 1 poprawną i 1 błędną → precyzja = 1/3 ≈ 33%, mimo że całe pytanie liczone jest jako błędne.

---

## 4. Punktacja pytania

- **Poprawna odpowiedź:** punkty dodatnie.
  - **Pierwsza w historii** poprawna odpowiedź na dane pytanie → **bonus** (domyślnie 300).
  - Kolejna poprawna odpowiedź na to pytanie → wartość podstawowa (domyślnie 100).
- **Błędna odpowiedź:** 0 punktów.
- Wartości punktowe pochodzą z [konfiguracji zdalnej](19-konfiguracja-zdalna.md); wartości domyślne to fallback.
- Punkty i historia „widzianych pytań" (ile razy widziane / poprawnie) są **wspólne dla całej aplikacji** — patrz [Punktacja i seria](11-punktacja-i-seria.md).

---

## 5. Zakończenie i ekran wyniku

Ekran wyniku prezentuje spójny zestaw danych:
- liczbę przejrzanych/odpowiedzianych pytań,
- liczbę poprawnych odpowiedzi,
- **punkty zdobyte** w sesji oraz **łączny wynik** użytkownika,
- informację, czy zaktualizowano **serię** (streak) i jej aktualną wartość.

Ukończenie sesji jest **odnotowywane** (licznik ukończonych quizów) — wpływa m.in. na kwalifikację do próśb o ocenę aplikacji i zgody na powiadomienia.

Poszczególne tryby mogą dodać do ekranu wyniku **własne, dodatkowe sekcje** (np. Swipe: statystyki czasowe i werdykt; Powtórki: przegląd odpowiedzi).

---

## 6. Reklamy w sesji

- Reklamy pełnoekranowe pojawiają się **co ustaloną liczbę odpowiedzi** oraz **na zakończeniu** sesji po przekroczeniu progu liczby odpowiedzi.
- Progi pochodzą z konfiguracji zdalnej (domyślnie: co 20 odpowiedzi; próg zakończenia 10).
- Użytkownicy **premium** nie widzą reklam.
- Niektóre tryby/warianty mogą **ignorować próg** zakończenia (np. Ćwiczenie dnia). Szczegóły: [Reklamy](15-reklamy.md).

---

## 7. Wyjście z sesji

- Próba wyjścia w trakcie sesji wymaga **potwierdzenia**.
- Jeśli użytkownik nie odpowiedział jeszcze na żadne pytanie → wyjście bez podsumowania.
- Jeśli odpowiedział na co najmniej jedno pytanie → sesja jest **finalizowana** z dotychczasowym wynikiem (przejście do ekranu wyniku, naliczenia zachowane).

---

## 8. Funkcje wspólne w trakcie sesji

- **Zgłoszenie problemu z pytaniem** — dostępne z poziomu pytania; treść pytania i tryb dołączane automatycznie. Patrz [Zgłaszanie błędów](18-zglaszanie-bledow.md).
- **Podgląd / przegląd** (w wybranych trybach) — możliwość obejrzenia poprawnych odpowiedzi.
- **Aktualizacja treści „na żywo"** — jeśli treść pytania zmieni się po stronie backendu w trakcie sesji, widoczne dane są odświeżane.

---

## 9. Powiązania

- **[Punktacja i seria](11-punktacja-i-seria.md)** — punkty, „pierwsza poprawna", seria, widziane pytania.
- **[Reklamy](15-reklamy.md)** — częstotliwość i wyzwalanie reklam.
- **[Konfiguracja zdalna](19-konfiguracja-zdalna.md)** — parametry punktów i reklam.
- **[Zgłaszanie błędów](18-zglaszanie-bledow.md)** — zgłaszanie problemów z pytaniem.
