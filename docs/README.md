# ParamedQuiz — dokumentacja funkcjonalna

> Dokumentacja **biznesowa / funkcjonalna** aplikacji ParamedQuiz — aplikacji do nauki i sprawdzania wiedzy dla ratownictwa medycznego. Opisuje **co** aplikacja robi z perspektywy użytkownika i reguł biznesowych, bez szczegółów platformowych (Android/iOS) i bez detali implementacyjnych.
>
> Odbiorcy: **testerzy** (baza do projektowania testów), **product/PM** (import do narzędzia project management), zespół na przyszłość.

---

## Jak korzystać z tej dokumentacji

- Każdy plik = jeden **feature** (obszar funkcjonalny). Pliki są niezależne i można je importować pojedynczo.
- Poziom szczegółowości: opis funkcji, przepływy użytkownika, reguły biznesowe, warunki dostępu oraz stany brzegowe i błędy.
- Szczegóły platformowe (specyfika Androida/iOS, techniczne kontrakty) są **poza zakresem** — wyjątek: powiadomienia mają dodatkowo osobne dokumenty techniczne (patrz niżej).
- Konwencja nazw plików: `NN-nazwa-feature.md` (numer = kolejność w spisie poniżej).

---

## Spis featurów

| # | Feature | Plik | Status |
|---|---------|------|--------|
| 00 | Wspólna mechanika quizu (podstawa referencyjna) | `00-mechanika-quizu.md` | ✅ |
| 01 | Onboarding i pierwsze uruchomienie | `01-onboarding.md` | ✅ |
| 02 | Regulamin (Terms of Service) | `02-regulamin.md` | ✅ |
| 03 | Konto i logowanie | `03-konto-i-logowanie.md` | ✅ |
| 04 | Ekran główny (Home) | `04-ekran-glowny.md` | ✅ |
| 05 | Tryb główny — quiz kategoriami | `05-tryb-glowny.md` | ✅ |
| 06 | Ćwiczenie dnia (Daily Exercise) | `06-cwiczenie-dnia.md` | ✅ |
| 07 | Tryb Swipe | `07-tryb-swipe.md` | ✅ |
| 08 | Tryb Tłumaczeń | `08-tryb-tlumaczen.md` | ✅ |
| 09 | Tryb CEM | `09-tryb-cem.md` | ✅ |
| 10 | Tryb Powtórek (Revisions) | `10-tryb-powtorek.md` | ✅ |
| 11 | Punktacja, punkty i seria (streak) | `11-punktacja-i-seria.md` | ✅ |
| 12 | Profil i statystyki użytkownika | `12-profil-i-statystyki.md` | ✅ |
| 13 | Ustawienia konta | `13-ustawienia-konta.md` | ✅ |
| 14 | Sklep i zakupy (Premium / Billing) | `14-sklep-i-zakupy.md` | ✅ |
| 15 | Reklamy i zgoda | `15-reklamy.md` | ✅ |
| 16 | Powiadomienia (biznesowo) | `16-powiadomienia.md` | ✅ |
| 17 | Ocena aplikacji (in-app rating) | `17-ocena-aplikacji.md` | ✅ |
| 18 | Zgłaszanie błędów / feedback | `18-zglaszanie-bledow.md` | ✅ |
| 19 | Konfiguracja zdalna (Remote Config) | `19-konfiguracja-zdalna.md` | ✅ |

Legenda statusu: ✅ gotowe · 🚧 w toku · ⏳ zaplanowane

### Scenariusze testów E2E
- [`e2e/`](e2e/README.md) — katalog scenariuszy end-to-end per feature (checklista regresji dla testera + backlog automatyzacji, z priorytetami i statusem).

### Dokumenty techniczne (uzupełniające, poza dokumentacją funkcjonalną)
- `notifications-spec.md` — pełna specyfikacja powiadomień.
- `notifications-cross-platform-contract.md` — kontrakt danych iOS/Android + panel wysyłki.
- `notifications-e2e-checklist.md` — checklista testów end-to-end powiadomień.

---

## Przegląd produktu

ParamedQuiz to aplikacja edukacyjna, w której użytkownik uczy się i sprawdza wiedzę z ratownictwa medycznego poprzez kilka **trybów gry** o różnej mechanice. Postęp, punkty i seria (streak) są wspólne dla całej aplikacji, a treści (pytania, kategorie, konfiguracja) pochodzą ze **zdalnego backendu** i mogą być aktualizowane bez wydawania nowej wersji aplikacji.

### Tryby gry (skrót)

| Tryb | Mechanika w skrócie | Dostęp |
|------|---------------------|--------|
| Główny (Main) | Klasyczny quiz jednokrotnego/wielokrotnego wyboru w ramach kategorii | Część kategorii darmowa, część płatna |
| Ćwiczenie dnia | Krótki dzienny zestaw losowych pytań z trybu głównego | Powiązany z trybem głównym |
| Swipe | Szybka ocena stwierdzeń „prawda/fałsz" gestem przesunięcia karty | Wersja próbna (free) + pełny płatny |
| Tłumaczeń | Tłumaczenie terminów/zwrotów medycznych | Płatny (dodatek) |
| CEM | Pytania w formule egzaminu CEM (Centrum Egzaminów Medycznych) | Osobny tryb |
| Powtórki (Revisions) | Konfigurowana sesja powtórkowa (np. słabe pytania / kategorie) | — |

### Warstwy wspólne (cross-cutting)

- **Konto i logowanie** — rejestracja, logowanie, reset hasła, usuwanie konta.
- **Punktacja i seria** — punkty za pytania, dzienna seria (streak), rekordy.
- **Profil i statystyki** — wyniki per tryb, najlepsze/najgorsze pytania, wykresy.
- **Sklep i płatności** — zakup pełnych trybów / kategorii / pakietu, status premium.
- **Reklamy** — reklamy pełnoekranowe między pytaniami (dla użytkowników bez premium) + zgoda.
- **Powiadomienia** — przypomnienia lokalne (daily/streak/powtórki/win-back) i treści marketingowe.
- **Ocena aplikacji, zgłaszanie błędów, konfiguracja zdalna** — funkcje wspierające.

---

## Glosariusz

| Termin | Znaczenie |
|--------|-----------|
| **Tryb (mode)** | Odrębny sposób rozgrywki quizu (Główny, Swipe, Tłumaczeń, CEM, Powtórki). |
| **Kategoria** | Tematyczny zbiór pytań w obrębie trybu (głównie w trybie głównym i CEM). |
| **Seria / streak** | Liczba kolejnych dni z zaliczoną aktywnością; buduje zaangażowanie. |
| **Combo (swipe)** | Liczba kolejnych poprawnych odpowiedzi w sesji Swipe; zapisywany jest rekord. |
| **Premium / pełny pakiet** | Status odblokowujący płatne treści (tryby/kategorie) i wyłączający reklamy. |
| **Wersja próbna (trial)** | Ograniczony, darmowy fragment płatnego trybu (tylko pytania oznaczone jako darmowe). |
| **Pytanie darmowe (free)** | Pytanie dostępne bez zakupu; używane m.in. w wersjach próbnych. |
| **Onboarding** | Sekwencja ekranów wprowadzających przy pierwszym wejściu w aplikację lub tryb. |
| **News banner** | Baner z nowościami/ogłoszeniami na ekranie głównym. |
| **Remote Config** | Zdalna konfiguracja parametrów rozgrywki i treści, zmienialna bez aktualizacji aplikacji. |
| **Werdykt (swipe)** | Podsumowanie jakościowe sesji Swipe (np. impulsywność vs wahanie) na podstawie czasów odpowiedzi. |

---

## Konwencja dokumentu feature

Każdy plik feature'a ma stałą strukturę:

1. **Metryka** — moduł, dostępność, powiązane featury.
2. **Cel funkcji** — po co istnieje, jaki problem rozwiązuje.
3. **Dostęp i warunki** — kto i kiedy widzi/używa funkcji (logowanie, premium, trial).
4. **Przepływ użytkownika** — kroki głównego scenariusza i wariantów.
5. **Reguły biznesowe** — konkretne zasady, progi, parametry.
6. **Stany brzegowe i błędy** — sytuacje wyjątkowe i oczekiwane zachowanie.
7. **Powiązania** — zależności od innych featurów.
