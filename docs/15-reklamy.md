# 15 — Reklamy i zgoda

## Metryka

| | |
|---|---|
| **Moduł** | `ads`, `core` (`ads`) |
| **Typ** | Warstwa monetyzacji (reklamy) |
| **Dostępność** | Użytkownicy **bez** premium / bez „brak reklam" |
| **Powiązane featury** | [Wspólna mechanika quizu](00-mechanika-quizu.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) · [Konfiguracja zdalna](19-konfiguracja-zdalna.md) |

---

## 1. Cel funkcji

Reklamy pełnoekranowe (interstitial) to element monetyzacji dla użytkowników korzystających z aplikacji bezpłatnie. Wyświetlane są w trakcie rozgrywki w trybach quizowych, z częstotliwością sterowaną [konfiguracją zdalną](19-konfiguracja-zdalna.md). Moduł obsługuje też **zgodę** na reklamy zgodną z wymogami prywatności.

---

## 2. Kto widzi reklamy

- Reklamy widzą użytkownicy **bez** aktywnego wyłączenia reklam.
- Reklamy są **wyłączone**, gdy użytkownik posiada **pełny pakiet** lub produkt **„brak reklam"** (patrz [Sklep i zakupy](14-sklep-i-zakupy.md)).
- [Tryb Tłumaczeń](08-tryb-tlumaczen.md) **nie** wyświetla reklam niezależnie od statusu.

---

## 3. Kiedy pojawia się reklama

Reklama pełnoekranowa może pojawić się w dwóch momentach sesji:
- **Międzyodpowiedziowo** — co ustaloną liczbę udzielonych odpowiedzi (domyślnie **co 20**).
- **Na zakończeniu / wyjściu** — gdy od ostatniej reklamy padła wystarczająca liczba odpowiedzi (domyślny próg **10**).

Zasady:
- Progi (**częstotliwość** i **próg zakończenia**) pochodzą z [konfiguracji zdalnej](19-konfiguracja-zdalna.md).
- Reklamy nie „nakładają się" — licznik od ostatniej reklamy pilnuje odstępu.
- Niektóre krótkie warianty (np. [ćwiczenie dnia](06-cwiczenie-dnia.md)) **ignorują próg zakończenia**.
- Po zamknięciu reklamy rozgrywka kontynuuje lub przechodzi do ekranu wyniku (zależnie od momentu wyświetlenia).

---

## 4. Zgoda na reklamy (consent)

- Przy odpowiednich warunkach użytkownikowi prezentowany jest **formularz zgody** dotyczący reklam/prywatności (zgodny z wymogami regionalnymi).
- Zgoda jest warunkiem serwowania spersonalizowanych reklam; obsługa zgody jest oddzielona od samej logiki wyświetlania.

---

## 5. Reguły biznesowe

- **Zależność od zakupów:** pełny pakiet lub „brak reklam" trwale wyłącza reklamy.
- **Sterowanie zdalne:** częstotliwość i próg zakończenia można zmieniać bez aktualizacji aplikacji (z sanity-limitami wartości).
- **Wyjątki trybów:** tryb tłumaczeń bez reklam; ćwiczenie dnia bez progu zakończenia.
- **Ciągłość rozgrywki:** wyświetlenie i zamknięcie reklamy nie przerywa logiki sesji (kontynuacja albo finalizacja).

---

## 6. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Użytkownik premium / „brak reklam" | Reklamy nie są wyświetlane. |
| Reklama niezaładowana / niedostępna | Rozgrywka kontynuuje bez reklamy. |
| Krótka sesja poniżej progów | Brak reklamy zakończeniowej. |
| Ćwiczenie dnia | Próg zakończenia ignorowany. |
| Zakup „brak reklam" w trakcie korzystania | Reklamy przestają się pojawiać po odblokowaniu. |
| Odmowa/oczekiwanie zgody | Zachowanie zgodne z wymogami zgody (brak spersonalizowanych reklam bez zgody). |

---

## 7. Powiązania

- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** — momenty wyzwalania reklam w sesji.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — wyłączanie reklam przez zakup.
- **[Konfiguracja zdalna](19-konfiguracja-zdalna.md)** — częstotliwość i próg reklam.
