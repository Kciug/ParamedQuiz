# 08 — Tryb Tłumaczeń

> Ten tryb korzysta ze [wspólnej mechaniki quizu](00-mechanika-quizu.md) w zakresie punktów, serii i ekranu wyniku, ale ma **własny sposób udzielania odpowiedzi** (wpisanie tłumaczenia). Poniżej opisano elementy specyficzne.

## Metryka

| | |
|---|---|
| **Moduł** | `translation_mode` |
| **Typ** | Tryb gry |
| **Dostępność** | **Płatny** (osobny tryb lub w pełnym pakiecie) |
| **Reklamy** | **Brak** reklam w tym trybie |
| **Powiązane featury** | [Sklep i zakupy](14-sklep-i-zakupy.md) · [Punktacja i seria](11-punktacja-i-seria.md) · [Zgłaszanie błędów](18-zglaszanie-bledow.md) |

---

## 1. Cel funkcji

Tryb Tłumaczeń ćwiczy znajomość terminologii: użytkownikowi prezentowany jest **zwrot/termin**, a jego zadaniem jest **wpisać tłumaczenie**. Nastawiony na aktywne przypomnienie (recall) zamiast wyboru z listy.

---

## 2. Dostęp i warunki

- Tryb jest **płatny**; dostęp po zakupie samego trybu **lub** posiadaniu pełnego pakietu.
- Zakup i wejście obsługiwane są m.in. z [ekranu głównego](04-ekran-glowny.md) (panel zakupu). Bliźniaczo do trybu Swipe dostępna jest **wersja próbna** („Wypróbuj") — trial pokazuje wyłącznie pytania oznaczone `isFree`, a po ich wyczerpaniu prezentowany jest panel zakończenia z opcją zakupu. Zakup w trakcie triala odblokowuje pełny tryb bez wychodzenia.
- Przy **pierwszym wejściu** wyświetlany jest onboarding trybu (jednorazowo).
- **Reklamy nie występują** w tym trybie.

---

## 3. Przepływ użytkownika

1. Po wejściu ładowany jest **potasowany** zestaw pytań (zwrot + dopuszczalne tłumaczenia).
2. Dla bieżącego pytania użytkownik **wpisuje** swoją odpowiedź.
3. Po zatwierdzeniu odpowiedź jest oceniana, pokazywana jest informacja zwrotna i naliczane są punkty.
4. Użytkownik przechodzi do kolejnego pytania.
5. Po ostatnim pytaniu prezentowany jest ekran wyniku.

---

## 4. Reguły biznesowe (specyficzne dla trybu)

- **Ocena odpowiedzi:** odpowiedź jest **poprawna**, gdy (po przycięciu spacji, **bez rozróżniania wielkości liter**) pokrywa się z **którymkolwiek** z dopuszczalnych tłumaczeń danego zwrotu.
- **Odpowiedź jest ostateczna:** po zatwierdzeniu pytania nie można zmienić odpowiedzi (pole zostaje zablokowane).
- **Punkty:** jak w [mechanice wspólnej](00-mechanika-quizu.md) — bonus za pierwszą w historii poprawną odpowiedź na dane pytanie, wartość podstawowa za kolejne, 0 za błędną.
- **Seria (streak):** poprawna odpowiedź może zaktualizować dzienną serię.
- **Brak reklam:** w trybie Tłumaczeń nie są wyświetlane reklamy pełnoekranowe.
- **Aktualizacja treści „na żywo":** zmiany pytań po stronie backendu są odświeżane z zachowaniem już udzielonych odpowiedzi; ewentualne nowe pytania są dołączane.
- **Zgłoszenie problemu:** możliwe z poziomu pytania (tryb oznaczany jako „Translation Mode").

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Błąd/brak sieci przy ładowaniu | Sesja pokazuje stan błędu zamiast pytań. |
| Pusty wpis / same spacje | Traktowane jak odpowiedź niepasująca do żadnego tłumaczenia (błędna). |
| Drobne różnice wielkości liter / spacji wiodących i końcowych | Nie wpływają na ocenę (porównanie po przycięciu i bez rozróżniania wielkości liter). |
| Literówki / synonimy spoza listy tłumaczeń | Uznane za błędne (dopasowanie wyłącznie do zdefiniowanych tłumaczeń). |
| Wyjście przed pierwszą odpowiedzią | Wyjście bez podsumowania. |
| Wyjście po części pytań | Sesja finalizowana z dotychczasowym wynikiem. |

---

## 6. Powiązania

- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — zakup trybu / pełnego pakietu.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — punkty i seria.
- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** — szkielet sesji i ekran wyniku.
- **[Zgłaszanie błędów](18-zglaszanie-bledow.md)** — zgłaszanie problemów z pytaniem.
