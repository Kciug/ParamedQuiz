# Scenariusze E2E — ParamedQuiz

> Katalog scenariuszy testów **end-to-end** (przepływy użytkownika przez całą aplikację). Służy dwóm celom:
> 1. **Checklista regresji** dla testera (od razu, zanim cokolwiek zautomatyzujemy).
> 2. **Backlog automatyzacji** — scenariusze będziemy sukcesywnie zamieniać na testy (Robolectric + Roborazzi).
>
> Scenariusze wyprowadzone są z dokumentacji funkcjonalnej (`docs/00`–`19`), sekcje „Przepływ użytkownika" i „Stany brzegowe". Każdy feature ma tu swój plik lustrzany numerem.

---

## Konwencja zapisu scenariusza

Każdy scenariusz ma stały format:

```
### E2E-<OBSZAR>-<NN> — <krótki tytuł>
- **Priorytet:** P0 | P1 | P2
- **Status:** ☐ manualny · 🟡 do automatyzacji · ✅ zautomatyzowany
- **Given:** stan początkowy (konto, premium, streak, dane)
- **When:** kroki użytkownika
- **Then:** oczekiwany rezultat (obserwowalny)
```

### Priorytety
| Priorytet | Znaczenie | Kandydat do automatyzacji |
|-----------|-----------|----------------------------|
| **P0** | Smoke — krytyczna ścieżka, której złamanie blokuje release | Tak, w pierwszej kolejności |
| **P1** | Regresja — ważny wariant / typowy przepływ | Tak, sukcesywnie |
| **P2** | Edge — stan brzegowy / rzadki wariant | Wg potrzeby (część zostaje manualna) |

### Statusy
- **☐ manualny** — sprawdzane ręcznie przez testera.
- **🟡 do automatyzacji** — zaplanowane, jeszcze niezaimplementowane.
- **✅ zautomatyzowany** — pokryte testem; tester może pominąć.

> Zasada: scenariusze **P0** dążymy do statusu ✅ najpierw — to one zdejmują najwięcej powtarzalnej pracy z testera.

---

## Warunki wstępne środowiska (dla automatyzacji)

Scenariusze zakładają **odizolowane** środowisko testowe (patrz plan `docs/e2e` / Porcja 2):
- **Fake backend** (`FakeFirestoreApi`) z zaseedowanymi danymi (pytania, kategorie, regulamin z wersją, news, config).
- **Fake auth** — logowanie/rejestracja bez realnego Firebase/Google.
- **Fake billing** (`MockBillingRepository`) — „zakupy" bez realnej płatności.
- **Reklamy i powiadomienia wyłączone** (no-op).
- **Sterowalny czas** (`TimeProvider`) — do scenariuszy streaka / ćwiczenia dnia.

Dane „Given" opisują stan, który ustawia się przez seed fake'ów i preferencje (onboarding/ToS/premium/streak).

---

## Matryca scenariuszy

| Plik | Obszar | P0 | P1 | P2 | Zautomatyzowane |
|------|--------|----|----|----|-----------------|
| [01-onboarding.md](01-onboarding.md) | Onboarding | 1 | 2 | 1 | 0 |
| [02-regulamin.md](02-regulamin.md) | Regulamin | 1 | 2 | 1 | 1 |
| [03-konto-i-logowanie.md](03-konto-i-logowanie.md) | Konto/logowanie | 1 | 3 | 2 | 0 |
| [04-ekran-glowny.md](04-ekran-glowny.md) | Ekran główny | 1 | 2 | 1 | 1 |
| [05-tryb-glowny.md](05-tryb-glowny.md) | Tryb główny | 2 | 2 | 2 | 1 |
| [06-cwiczenie-dnia.md](06-cwiczenie-dnia.md) | Ćwiczenie dnia | 1 | 1 | 2 | 0 |
| [07-tryb-swipe.md](07-tryb-swipe.md) | Tryb Swipe | 1 | 3 | 2 | 1 |
| [08-tryb-tlumaczen.md](08-tryb-tlumaczen.md) | Tryb Tłumaczeń | 1 | 2 | 2 | 0 |
| [09-tryb-cem.md](09-tryb-cem.md) | Tryb CEM | 1 | 2 | 1 | 0 |
| [10-tryb-powtorek.md](10-tryb-powtorek.md) | Powtórki | 1 | 3 | 2 | 0 |
| [12-profil-i-statystyki.md](12-profil-i-statystyki.md) | Profil/statystyki | 0 | 2 | 2 | 0 |
| [13-ustawienia-konta.md](13-ustawienia-konta.md) | Ustawienia konta | 1 | 3 | 2 | 0 |
| [14-sklep-i-zakupy.md](14-sklep-i-zakupy.md) | Sklep/zakupy | 1 | 3 | 2 | 0 |
| [17-ocena-aplikacji.md](17-ocena-aplikacji.md) | Ocena aplikacji | 0 | 2 | 1 | 0 |
| [18-zglaszanie-bledow.md](18-zglaszanie-bledow.md) | Zgłaszanie błędów | 0 | 2 | 1 | 0 |

> Cross-cutting (punktacja/seria, reklamy, powiadomienia, remote config) są weryfikowane **wewnątrz** scenariuszy trybów/ekranów, gdzie są obserwowalne — nie mają osobnych plików E2E.

Matrycę aktualizujemy przy każdej porcji automatyzacji (kolumna „Zautomatyzowane" + statusy w plikach).

---

## Zestaw P0 „smoke" (cel automatyzacji nr 1)

Minimalny zestaw, który po zautomatyzowaniu daje największą pewność przy release:

1. `E2E-ONBOARDING-01` — świeży start → regulamin → ekran główny.
2. `E2E-MAIN-01` — rozegranie quizu kategorii → ekran wyniku.
3. `E2E-SWIPE-01` — trial → wyczerpanie darmowych → panel zakończenia → zakup → pełny tryb.
4. `E2E-STORE-01` — zakup pełnego pakietu → odblokowanie treści + brak reklam.
5. `E2E-DAILY-01` — ćwiczenie dnia → podbicie streaka → niedostępne drugi raz tego dnia.
6. `E2E-REV-01` — konfiguracja powtórek → sesja → wynik.
7. `E2E-SETTINGS-01` — zmiana hasła (happy path).
8. `E2E-MAIN-02` — blokada płatnej kategorii → okno zakupu.
