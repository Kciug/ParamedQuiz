# 10 — Tryb Powtórek (Revisions)

> Tryb korzysta z punktacji i serii jak reszta aplikacji, ale ma **własną mechanikę sesji** (kolejka z powtórkami). Ocena pojedynczego pytania działa jak w [mechanice wspólnej](00-mechanika-quizu.md) i w trybie źródłowym.

## Metryka

| | |
|---|---|
| **Moduł** | `revisions` |
| **Typ** | Tryb gry (powtórkowy) |
| **Dostępność** | Wszyscy; zależna od historii odpowiedzi użytkownika |
| **Powiązane featury** | [Ekran główny](04-ekran-glowny.md) · [Tryb główny](05-tryb-glowny.md) · [CEM](09-tryb-cem.md) · [Tłumaczeń](08-tryb-tlumaczen.md) · [Punktacja i seria](11-punktacja-i-seria.md) |

---

## 1. Cel funkcji

Tryb Powtórek pozwala **celowanie** powtarzać pytania, na które użytkownik już wcześniej odpowiadał — np. te najsłabiej opanowane. Jest to narzędzie utrwalania wiedzy: użytkownik konfiguruje sesję (skąd i jakie pytania, ile), a następnie rozwiązuje ją w trybie „do skutku".

---

## 2. Dostęp i warunki

- Dostępny z [ekranu głównego](04-ekran-glowny.md) (dodatki).
- Powtórce podlegają **wyłącznie pytania już odpowiadane** (widziane co najmniej raz) — nie da się powtarzać pytań, których użytkownik nie widział.
- Źródłem pytań może być jeden z trybów: **główny**, **CEM**, **tłumaczeń**.
- **Kwalifikacja trybu źródłowego:** tryb jest dostępny jako źródło tylko, gdy istnieją kwalifikujące się pytania. Dla trybu **tłumaczeń** wymagane jest minimum odpowiedzianych pytań (próg **10**).

---

## 3. Konfiguracja sesji

Przed startem użytkownik ustala parametry:

| Parametr | Opcje | Znaczenie |
|----------|-------|-----------|
| **Tryb źródłowy** | główny / CEM / tłumaczeń | Z jakiej puli pochodzą pytania. |
| **Kategoria** | kategorie danego trybu (jeśli dotyczy) | Zawężenie do wybranej kategorii. |
| **Kryterium** | najsłabsze / najlepsze / poniżej 50% | Które odpowiadane pytania wybrać (patrz 4.1). |
| **Rozmiar sesji** | 10 / 20 / 50 / 100 / wszystkie | Ile pytań w sesji (dostępne opcje zależą od liczby kwalifikujących się pytań). |

Ekran konfiguracji pokazuje na bieżąco **liczbę dostępnych pytań** dla wybranych ustawień; opcje rozmiaru sesji ograniczają się do wartości nieprzekraczających tej liczby.

---

## 4. Reguły biznesowe

### 4.1 Kryteria doboru pytań
Dobór opiera się na **skuteczności** danego pytania u użytkownika (stosunek poprawnych do wszystkich prób):
- **Najsłabsze (WORST):** pytania od najniższej skuteczności.
- **Najlepsze (BEST):** pytania od najwyższej skuteczności.
- **Poniżej 50% (UNDER_50):** tylko pytania ze skutecznością < 50%, od najsłabszych.

### 4.2 Mechanika sesji „do skutku" (kolejka)
- Pytania tworzą **kolejkę**.
- **Poprawna** odpowiedź → pytanie **opuszcza** kolejkę.
- **Błędna** odpowiedź → pytanie wraca **na koniec** kolejki (faza „korekty") i wróci ponownie.
- Pytanie **odpowiadane błędnie 3 razy** zostaje **usunięte** z sesji jako „niezaliczone" (nie wraca dalej).
- Sesja kończy się, gdy kolejka jest pusta (wszystkie pytania zaliczone lub odrzucone po 3 błędach).

### 4.3 Punkty i seria — tylko za pierwsze podejście
- Punkty oraz aktualizacja serii są naliczane **wyłącznie przy pierwszym podejściu** do danego pytania w sesji.
- Powtórki tego samego pytania (faza korekty) **nie** przyznają dodatkowych punktów ani nie odnawiają serii.
- Zasady punktów jak w [mechanice wspólnej](00-mechanika-quizu.md) (bonus za pierwszą w historii poprawną odpowiedź itd.).

### 4.4 Rodzaje pytań w sesji
- Dla trybów **głównego** i **CEM** — pytania wyboru (jak w [mechanice wspólnej](00-mechanika-quizu.md); poprawność = 100% precyzji).
- Dla trybu **tłumaczeń** — wpisanie tłumaczenia (ocena jak w [trybie Tłumaczeń](08-tryb-tlumaczen.md)).

### 4.5 Postęp i podsumowanie
- Postęp rozróżnia **pierwsze przejście** i **fazę korekty** powtarzanych pytań.
- Ekran wyniku pokazuje m.in. liczbę pytań, poprawne odpowiedzi, pytania **niezaliczone** (3 błędy) oraz umożliwia **przegląd** odpowiedzi.
- Reklamy — zgodnie z [mechaniką wspólną](00-mechanika-quizu.md).

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Brak kwalifikujących się pytań dla ustawień | Stan pusty; sesji nie można rozpocząć dla tej konfiguracji. |
| Tryb tłumaczeń poniżej progu odpowiedzi (10) | Tryb źródłowy niedostępny do wyboru. |
| Wybrany rozmiar większy niż liczba pytań | Opcje rozmiaru ograniczone do dostępnej liczby; dobierany dopuszczalny limit. |
| Pytanie 3× błędne | Usuwane z sesji, oznaczone jako niezaliczone, liczone w podsumowaniu. |
| Powtórki pytania (korekta) | Brak dodatkowych punktów i brak ponownej aktualizacji serii. |
| Wyjście w trakcie sesji | Potwierdzenie; finalizacja z dotychczasowym wynikiem. |
| Błąd/brak sieci przy ładowaniu | Konfiguracja/sesja pokazuje stan błędu. |

---

## 6. Powiązania

- **[Ekran główny](04-ekran-glowny.md)** — wejście z dodatków.
- **[Tryb główny](05-tryb-glowny.md)** · **[CEM](09-tryb-cem.md)** · **[Tłumaczeń](08-tryb-tlumaczen.md)** — tryby źródłowe pytań.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — punkty (za pierwsze podejście), seria, historia „widzianych pytań" jako podstawa kwalifikacji.
- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** — ocena pojedynczego pytania.
