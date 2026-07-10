# 18 — Zgłaszanie błędów i feedback

## Metryka

| | |
|---|---|
| **Moduł** | `core` (`report_issues`), `firestore` (zapis) |
| **Typ** | Warstwa jakości / komunikacji z zespołem |
| **Dostępność** | Wszyscy |
| **Powiązane featury** | Tryby gry · [Ocena aplikacji](17-ocena-aplikacji.md) |

---

## 1. Cel funkcji

Funkcja umożliwia użytkownikom **zgłaszanie problemów z konkretnymi pytaniami** (np. błędna treść, wątpliwa odpowiedź) oraz przekazywanie ogólnego **feedbacku** o aplikacji. Zgłoszenia trafiają do zespołu jako dane do poprawy treści i produktu.

Wyróżniamy dwa kanały:
- **Zgłoszenie pytania** — kontekstowe, z poziomu rozgrywki.
- **Feedback ogólny** — powiązany z [prośbą o ocenę](17-ocena-aplikacji.md).

---

## 2. Zgłoszenie problemu z pytaniem

### 2.1 Dostęp
- Dostępne z poziomu **sesji quizu** we wszystkich trybach z pytaniami (główny, CEM, Swipe, tłumaczeń, powtórki).

### 2.2 Przepływ
1. Użytkownik otwiera zgłoszenie dla **aktualnie widocznego** pytania.
2. Wpisuje **opis** problemu.
3. Wysyła zgłoszenie; po powodzeniu widzi **potwierdzenie**.

### 2.3 Zawartość zgłoszenia
Automatycznie dołączane są:
- **identyfikator** i **treść** zgłaszanego pytania,
- **tryb gry**, z którego pochodzi zgłoszenie,
- **opis** wpisany przez użytkownika,
- znacznik czasu.

---

## 3. Feedback ogólny

- Zbierany w ścieżce **niskiej oceny** aplikacji (patrz [Ocena aplikacji](17-ocena-aplikacji.md)) oraz jako opinia użytkownika.
- Zawiera **treść** opinii oraz **ocenę** (i opcjonalnie powiązanie z użytkownikiem).
- Po udanym wysłaniu aplikacja traktuje prośbę o ocenę jako obsłużoną.

---

## 4. Reguły biznesowe

- **Kontekstowość zgłoszeń pytań:** zgłoszenie zawsze dotyczy konkretnego, aktualnie widocznego pytania i niesie jego kontekst (id, treść, tryb).
- **Potwierdzenie po sukcesie:** udane wysłanie sygnalizowane jest użytkownikowi; pole opisu jest czyszczone.
- **Rozdział kanałów:** zgłoszenia pytań i feedback ogólny to osobne dane po stronie backendu.

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Wysłanie udane | Potwierdzenie; formularz zamknięty / wyczyszczony. |
| Błąd/brak sieci przy wysyłce | Zgłoszenie niewysłane; okno pozostaje (możliwość ponowienia). |
| Pusty opis | Zależnie od kanału — zgłoszenie może zostać wysłane z samym kontekstem pytania. |
| Zgłoszenie w trakcie zmiany pytania | Dołączany jest kontekst pytania widocznego w chwili otwarcia zgłoszenia. |

---

## 6. Powiązania

- **Tryby gry** — źródło zgłoszeń pytań (kontekst i etykieta trybu).
- **[Ocena aplikacji](17-ocena-aplikacji.md)** — ścieżka feedbacku ogólnego.
