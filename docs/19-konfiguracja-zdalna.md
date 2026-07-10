# 19 — Konfiguracja zdalna (Remote Config)

## Metryka

| | |
|---|---|
| **Moduł** | `firestore` (`config`), `core` (`domain/config`) |
| **Typ** | Warstwa konfiguracji / treści (cross-cutting) |
| **Dostępność** | Dotyczy wszystkich (transparentnie) |
| **Powiązane featury** | [Wspólna mechanika quizu](00-mechanika-quizu.md) · [Punktacja i seria](11-punktacja-i-seria.md) · [Reklamy](15-reklamy.md) · [Ćwiczenie dnia](06-cwiczenie-dnia.md) · [Regulamin](02-regulamin.md) · [Ekran główny](04-ekran-glowny.md) · [Powiadomienia](16-powiadomienia.md) |

---

## 1. Cel funkcji

Konfiguracja zdalna pozwala **zmieniać zachowanie i treści** aplikacji **bez wydawania nowej wersji**. Obejmuje parametry rozgrywki, treści (pytania, kategorie, nowości, regulamin) oraz konfigurację powiadomień. Aplikacja ma wbudowane **wartości domyślne (fallback)** na wypadek braku sieci lub braku parametru.

---

## 2. Zakres sterowany zdalnie

### 2.1 Parametry rozgrywki (gameplay config)
| Parametr | Domyślnie | Wpływ |
|----------|-----------|-------|
| Częstotliwość reklam | 20 | Co ile odpowiedzi reklama (patrz [Reklamy](15-reklamy.md)). |
| Próg reklamy zakończeniowej | 10 | Min. odpowiedzi do reklamy na wyjściu. |
| Punkty za poprawną | 100 | Punkty za poprawną odpowiedź. |
| Punkty za pierwszą poprawną | 300 | Bonus za pierwszą w historii poprawną odpowiedź. |
| Próg serii | 3 | Aktywność w sesji potrzebna do zaliczenia dnia (patrz [Punktacja i seria](11-punktacja-i-seria.md)). |
| Liczba pytań ćwiczenia dnia | 3 | Rozmiar [ćwiczenia dnia](06-cwiczenie-dnia.md). |

### 2.2 Treści i dane
- **Pytania i kategorie** wszystkich trybów.
- **Nowości (news banners)** na [ekranie głównym](04-ekran-glowny.md).
- **Regulamin** (treść i **wersja**) — patrz [Regulamin](02-regulamin.md).
- **Konfiguracja i szablony powiadomień** — patrz [Powiadomienia](16-powiadomienia.md).

---

## 3. Reguły biznesowe

- **Fallback:** przy braku parametru / offline / błędzie stosowane są wbudowane wartości domyślne.
- **Sanity-limity:** wartości parametrów rozgrywki są ograniczane do rozsądnych zakresów — błędnie wpisana wartość w konsoli nie „zepsuje" aplikacji (parametry trafiają do użytkowników bez dodatkowej weryfikacji).
- **Odświeżanie:** konfiguracja parametrów jest cache'owana i odświeżana z określonym interwałem (domyślnie ok. raz na dobę); dane treściowe (pytania/kategorie/nowości) mogą być aktualizowane na bieżąco i propagować się także **w trakcie sesji**.
- **Wersjonowanie regulaminu:** zmiana wersji wymusza ponowną akceptację (patrz [Regulamin](02-regulamin.md)).
- **Transparentność:** zmiany konfiguracji są dla użytkownika niewidoczne wprost — objawiają się zmianą zachowania/treści.

---

## 4. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Brak sieci / błąd pobrania | Użycie wartości domyślnych / ostatnich znanych. |
| Brak konkretnego parametru | Wartość domyślna dla tego parametru. |
| Wartość poza rozsądnym zakresem | Przycięcie do dozwolonego zakresu (sanity-limit). |
| Aktualizacja treści w trakcie sesji | Odświeżenie widocznych pytań/danych bez restartu. |
| Nowa wersja regulaminu | Wymuszenie ponownej akceptacji. |

---

## 5. Powiązania

- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** / **[Punktacja i seria](11-punktacja-i-seria.md)** — punkty i próg serii.
- **[Reklamy](15-reklamy.md)** — częstotliwość i próg reklam.
- **[Ćwiczenie dnia](06-cwiczenie-dnia.md)** — liczba pytań.
- **[Regulamin](02-regulamin.md)** — treść i wersja.
- **[Ekran główny](04-ekran-glowny.md)** — nowości.
- **[Powiadomienia](16-powiadomienia.md)** — konfiguracja i szablony.
