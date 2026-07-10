# 16 — Powiadomienia (opis biznesowy)

> **Uwaga:** ten dokument opisuje powiadomienia **funkcjonalnie**. Szczegóły techniczne i kontrakt danych znajdują się w osobnych plikach: `notifications-spec.md`, `notifications-cross-platform-contract.md`, `notifications-e2e-checklist.md`.

## Metryka

| | |
|---|---|
| **Moduł** | `notifications` |
| **Typ** | Warstwa zaangażowania (retencja) |
| **Dostępność** | Wszyscy; za zgodą użytkownika |
| **Powiązane featury** | [Ekran główny](04-ekran-glowny.md) · [Ustawienia konta](13-ustawienia-konta.md) · [Punktacja i seria](11-punktacja-i-seria.md) · [Powtórki](10-tryb-powtorek.md) |

---

## 1. Cel funkcji

Powiadomienia mają utrzymywać **zaangażowanie** i wspierać regularną naukę: przypominają o codziennej aktywności, ratują zagrożoną **serię**, zachęcają do **powtórek** słabych pytań i **reaktywują** nieaktywnych użytkowników. Dodatkowo umożliwiają wysyłkę **treści/nowości** (marketing/ogłoszenia).

Wyróżniamy dwie warstwy:
- **Przypomnienia lokalne** — planowane na urządzeniu, oparte o dane progresji użytkownika.
- **Treści zdalne (content/marketing)** — rozsyłane centralnie do wszystkich odbiorców tematu.

---

## 2. Zgoda (priming)

- Zanim aplikacja poprosi systemowo o pozwolenie, prezentuje **własny pre-prompt** (priming) wyjaśniający korzyści.
- **Warunki pokazania promptu:**
  - co najmniej **1 ukończony quiz**,
  - maksymalnie **3 pokazania** łącznie,
  - odstęp **≥ 7 dni** między pokazaniami,
  - po systemowej odmowie — **trwałe zaprzestanie** proszenia.
- Akceptacja włącza powiadomienia (planuje przypomnienia i zapewnia subskrypcję treści); odmowa wstrzymuje dalsze proszenie.
- Użytkownik może później samodzielnie zarządzać powiadomieniami w [ustawieniach](13-ustawienia-konta.md).

---

## 3. Przypomnienia lokalne

Aplikacja planuje **jedno** przypomnienie dziennie, wybierane wg priorytetu na podstawie stanu użytkownika:

| Priorytet | Typ | Warunek (uproszczony) |
|-----------|-----|------------------------|
| 1 | *(brak)* | Dzień już zaliczony → nic nie wysyłamy. |
| 2 | **Streak (seria zagrożona)** | Ostatnie zaliczenie **wczoraj** i seria odpowiednio długa. |
| 3 | **Win-back (reaktywacja)** | Nieaktywność dokładnie **7** lub **14** dni (raz na próg). |
| 4 | **Revision (powtórka)** | Odpowiednio dużo **słabych pytań** i minął odstęp od ostatniego takiego przypomnienia. |
| 5 | **Daily (codzienne)** | W pozostałych przypadkach — bazowe przypomnienie o nauce. |

- **Godzina** przypomnień jest ustawiana przez użytkownika (w [ustawieniach](13-ustawienia-konta.md)).
- Treści przypomnień mogą pochodzić z szablonów zarządzanych zdalnie; przy ich braku używany jest wbudowany fallback.
- Kliknięcie przypomnienia otwiera odpowiedni ekran (np. Daily/Streak/Win-back → ekran główny; Revision → [powtórki](10-tryb-powtorek.md)).

> „Słabe pytanie" i długość serii wyliczane są z [danych progresji](11-punktacja-i-seria.md).

---

## 4. Treści zdalne (content / marketing)

- Rozsyłane jako **broadcast** do subskrybentów tematu treści (bez profilowania per-użytkownik).
- **Subskrypcja jest bramkowana zgodą** — wyłączenie powiadomień odsubskrybowuje treści.
- Rozróżniane są kategorie **nowości** i **marketing**.
- Wiadomość może kierować do wskazanego ekranu (np. ekran główny lub powtórki).

---

## 5. Zarządzanie przez użytkownika

- **Włącz/wyłącz** powiadomienia oraz **godzina** przypomnień — w [ustawieniach konta](13-ustawienia-konta.md).
- Włączenie planuje przypomnienia i zapewnia subskrypcję treści; wyłączenie anuluje przypomnienia i odsubskrybowuje treści.

---

## 6. Reguły biznesowe

- **Jedno przypomnienie lokalne dziennie**, wybierane wg powyższego priorytetu.
- **Zgoda jako bramka** dla treści zdalnych i planowania przypomnień.
- **Win-back** ma pierwszeństwo przed **Revision** na progach reaktywacji.
- **Guardy powtarzalności:** win-back raz na próg (7/14); revision z minimalnym odstępem między przypomnieniami.
- **Dzień zaliczony** = brak przypomnienia tego dnia.

---

## 7. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Systemowa odmowa uprawnień | Trwałe zaprzestanie proszenia; powiadomienia nieaktywne. |
| Brak szablonów treści przypomnień | Użycie wbudowanego fallbacku. |
| Dzień już zaliczony | Brak przypomnienia. |
| Wyłączenie powiadomień w ustawieniach | Anulowanie przypomnień i odsubskrybowanie treści. |
| Offline | Przypomnienia lokalne działają lokalnie; treści zdalne wymagają sieci. |

---

## 8. Powiązania

- **[Ustawienia konta](13-ustawienia-konta.md)** — przełącznik i godzina przypomnień.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — dane do decyzji (seria, słabe pytania).
- **[Powtórki](10-tryb-powtorek.md)** — cel przypomnień o powtórkach.
- **Dokumenty techniczne:** `notifications-spec.md`, `notifications-cross-platform-contract.md`, `notifications-e2e-checklist.md`.
