# Regresja: refaktor obsługi błędów

> Plan testów dla **jednej konkretnej zmiany**: przebudowy systemu komunikatów o błędach i przepisania logowania przez Google.
> Nie zastępuje checklist z `docs/e2e/01`–`18`, tylko wskazuje, co ta zmiana mogła zepsuć.

**Zasięg zmiany:** 79 plików, 14 modułów. Dotknięte: logowanie i rejestracja, wszystkie ekrany quizowe, zakupy, ustawienia konta, ekran główny i sklep.

---

## Dla kogo jest ta lista

| Blok | Kto wykonuje | Build | Czas |
|---|---|---|---|
| 0–5 | tester, samo urządzenie | **staging** | ~60 min |
| 6.1–6.5 | deweloper, wymaga `adb` | debug wystarczy | ~10 min |
| 6.6 | deweloper, wymaga `adb` | **tylko staging** | ~5 min |
| 7 | tester + deweloper | staging | ~10 min |

**Tester dostaje build `staging`, nie `debug`.** Tylko staging jest minifikowany przez R8, a to właśnie w takim buildzie pojawiały się dotychczas problemy niewidoczne u dewelopera. Testowanie na debugu ominie połowę ryzyka.

---

## Jedna zasada nadrzędna

Każdy komunikat o błędzie, jaki zobaczysz w aplikacji, musi być:

- **po polsku**, pełnym zdaniem,
- **bez nazw technicznych** — żadnego `Exception`, `null`, `com.google...`, `FirebaseFirestoreException`, `AUTH:`, `FS:`, `GIS:`, ani ciągów typu `a$b`.

Jeśli zobaczysz cokolwiek takiego — **zrób zrzut ekranu i zgłoś**. To najważniejsza rzecz, której szukamy. Angielski tekst albo nazwa klasy w dialogu to zawsze błąd, niezależnie od tego, co robiłeś.

---

## Blok 0 — Smoke. Jeśli to nie przejdzie, przerwij testy

Bez tego reszta nie ma sensu.

| # | Krok | Oczekiwane |
|---|---|---|
| 0.1 | Zainstaluj build staging na czystej instalacji (odinstaluj poprzednią wersję) | Aplikacja startuje, nie crashuje |
| 0.2 | Przejdź onboarding i zaakceptuj regulamin | Dochodzisz do ekranu głównego |
| 0.3 | Wejdź w Tryb Główny → wybierz kategorię → odpowiedz na 3 pytania | Pytania się ładują, odpowiedzi działają |
| 0.4 | Wróć na ekran główny | Bez crasha, dane widoczne |

---

## Blok 1 — Logowanie i rejestracja (najwyższe ryzyko)

Cały moduł logowania został przepisany od zera. To tutaj najbardziej prawdopodobne są regresje.

### 1.1 Logowanie e-mailem — poprawne dane
Zaloguj się istniejącym kontem e-mail.
**Oczekiwane:** wchodzisz do aplikacji, na ekranie profilu widać Twoją nazwę i e-mail.

### 1.2 Logowanie e-mailem — błędne hasło
Podaj poprawny e-mail, złe hasło.
**Oczekiwane:** dialog „Wystąpił błąd" z komunikatem o błędnych danych logowania. Po kliknięciu OK dialog znika i **wracasz do formularza**, a nie do zablokowanego ekranu ładowania.

### 1.3 Logowanie e-mailem — nieistniejące konto
**Oczekiwane:** komunikat o błędnych danych logowania. OK zamyka dialog.

### 1.4 Rejestracja — e-mail już zajęty
**Oczekiwane:** „Konto o podanym adresie e-mail już istnieje."

### 1.5 Rejestracja — za słabe hasło
Podaj hasło krótsze niż 6 znaków.
**Oczekiwane:** „Hasło jest zbyt słabe. Wprowadź silniejsze hasło."

### 1.6 Rejestracja — zły format e-maila
**Oczekiwane:** „Adres e-mail ma niepoprawny format."

### 1.7 Reset hasła — poprawny adres
**Oczekiwane:** ekran potwierdzenia wysłania. Mail dochodzi.

### 1.8 Reset hasła — zły format adresu
**Oczekiwane:** „Adres e-mail ma niepoprawny format."

### 1.9 Logowanie Google — ścieżka szczęśliwa
**Oczekiwane:** wchodzisz do aplikacji, profil pokazuje dane z konta Google.

### 1.10 Logowanie Google — anulowanie ⚠️ *zmiana zachowania*
Kliknij logowanie Google, poczekaj aż pojawi się okno wyboru konta, **zamknij je** (swipe w dół albo przycisk wstecz).
**Oczekiwane:** wracasz na ekran logowania. **Nie pojawia się żaden dialog błędu.** Ekran jest sprawny, można kliknąć jeszcze raz.
**Zgłoś, jeśli:** wyskoczy dialog, ekran zawiesi się na kręciołku, albo przycisk przestanie reagować.

### 1.11 Wylogowanie i ponowne logowanie
Wyloguj się z ustawień, zaloguj ponownie tym samym kontem.
**Oczekiwane:** działa, dane wracają, punkty się zgadzają.

---

## Blok 2 — Komunikaty przy braku internetu (nowe zachowanie)

Wcześniej te sytuacje dawały „Wystąpił nieznany błąd". Teraz mają mówić konkretnie. **To nie jest regresja, tylko celowa poprawka** — sprawdzamy, czy działa.

Włącz **tryb samolotowy** przed każdym z poniższych kroków.

| # | Krok | Oczekiwane |
|---|---|---|
| 2.1 | Próba logowania e-mailem | „Brak połączenia z internetem. Sprawdź sieć i spróbuj ponownie." |
| 2.2 | Próba logowania Google | Komunikat o braku połączenia lub o nieudanym logowaniu Google. **Nie** „nieznany błąd" |
| 2.3 | Wejście w Tryb Główny (po czystej instalacji, bez cache) | Sensowny komunikat po polsku, aplikacja nie zawiesza się na kręciołku |
| 2.4 | Zmiana hasła w ustawieniach konta | Sensowny komunikat, ekran nie zostaje zablokowany |
| 2.5 | Wyłącz tryb samolotowy i powtórz 2.3 | Wszystko ładuje się normalnie |

**Kluczowe przy każdym z tych kroków:** czy po zamknięciu dialogu ekran **wraca do używalnego stanu**, czy zostaje na wiecznym kręciołku. Wieczny kręciołek zgłaszaj zawsze.

---

## Blok 3 — Tryby quizowe (przepisana warstwa danych)

Wszystkie ścieżki pobierania danych z serwera przeszły przebudowę. Sprawdzamy, że dane nadal się ładują.

Dla **każdego** trybu: Tryb Główny, Swipe, Tłumaczenia, CEM, Powtórki:

| # | Krok | Oczekiwane |
|---|---|---|
| 3.1 | Wejdź w tryb, wybierz kategorię/konfigurację | Lista się ładuje, brak dialogu błędu |
| 3.2 | Rozegraj quiz do końca | Ekran podsumowania z wynikiem |
| 3.3 | Sprawdź, że wynik trafił do profilu | Statystyki się zaktualizowały |

Dodatkowo:

### 3.4 Kategorie CEM — ponów po błędzie
Włącz tryb samolotowy, wejdź w kategorie CEM (po czystej instalacji).
**Oczekiwane:** dialog błędu. Po kliknięciu OK ekran **próbuje pobrać dane ponownie** (to jedyny ekran z ponowieniem zamiast cofnięcia).

### 3.5 Ćwiczenie dnia
Wykonaj ćwiczenie dnia, sprawdź streak.
**Oczekiwane:** bez zmian względem poprzedniej wersji.

---

## Blok 4 — Zakupy

Typ komunikatu o błędzie zakupu się zmienił, więc trzeba sprawdzić, czy nadal się wyświetla.

| # | Krok | Oczekiwane |
|---|---|---|
| 4.1 | Otwórz sklep | Lista trybów z cenami, bez błędu |
| 4.2 | Kliknij zablokowany tryb na ekranie głównym | Otwiera się panel zakupu z ceną |
| 4.3 | Kliknij zablokowaną kategorię w Trybie Głównym | Otwiera się panel zakupu z ceną |
| 4.4 | Rozpocznij zakup i **anuluj** okno Google Play | „Płatność została anulowana." pod przyciskiem, panel nadal używalny |
| 4.5 | Wyczerp trial w trybie Swipe | Panel „trial zakończony" z ceną, bez błędu |
| 4.6 | Wyczerp trial w trybie Tłumaczeń | Jak wyżej |
| 4.7 | Otwórz sklep w trybie samolotowym | Komunikat po polsku, **nie** angielski tekst |

⚠️ **Punkt 4.7 to znana zmiana:** wcześniej pojawiał się tam angielski napis „Error loading store: ...". Teraz ma być normalny polski komunikat.

---

## Blok 5 — Ustawienia konta ⚠️ *tu są celowe zmiany zachowania*

| # | Krok | Oczekiwane |
|---|---|---|
| 5.1 | Zmień nazwę użytkownika | Nazwa się zmienia, widać ją na profilu |
| 5.2 | Zmień hasło (poprawne stare hasło) | Potwierdzenie sukcesu, nowe hasło działa przy ponownym logowaniu |
| 5.3 | Zmień hasło (błędne stare hasło) | Komunikat o błędnych danych, ekran używalny |
| 5.4 | Wyloguj się | Wracasz do ekranu logowania/głównego jako gość |
| 5.5 | Usuń konto (konto e-mail) | Konto usunięte, wracasz do stanu gościa |
| 5.6 | Usuń konto (konto Google) | Prośba o ponowne potwierdzenie kontem Google, potem usunięcie |

⚠️ **Znana zmiana:** operacje 5.1–5.3 wykonane bez aktywnej sesji dawały wcześniej wieczny kręciołek albo crash. Teraz pokażą komunikat „Weryfikacja nieudana. Wyloguj się i spróbuj po ponownym zalogowaniu". To poprawka, nie błąd.

---

## Blok 6 — Weryfikacja logów (deweloper, wymaga `adb`)

Cały sens tej zmiany to możliwość zdiagnozowania awarii. Bez tego kroku nie wiadomo, czy zadziałała.

```bash
adb logcat -c && adb logcat -s AppError
```

**Do punktów 6.1–6.5 wystarczy build `debug`** i jest wygodniejszy: szybciej się buduje, nie wymaga keystore, łatwiej iterować. Sama logika logowania błędów jest identyczna w obu wariantach, więc nic na tym nie tracisz.

**Punkt 6.6 wymaga builda `staging`.** W debugu minifikacja jest wyłączona, więc nazwy są czytelne z definicji i test przeszedłby pusto, niczego nie dowodząc.

| # | Scenariusz | Oczekiwany wpis |
|---|---|---|
| 6.1 | Błędne hasło | `AuthRepository.loginWithEmailAndPassword \| Auth.*` |
| 6.2 | **Anulowanie okna Google** | **żadnego wpisu** — to nie awaria |
| 6.3 | Logowanie Google na urządzeniu bez konta Google | `AuthRepository.signInWithGoogle \| Google.NoCredentialAvailable` |
| 6.4 | Tryb samolotowy + wejście w tryb quizowy | `FirestoreService.* \| NoNetwork` lub `Data.Unavailable` |
| 6.5 | Dowolny błąd danych podczas logowania | **dokładnie jeden** wpis, nie trzy |

### 6.6 Czytelność po minifikacji — jedyny punkt wymagający staging

Zbuduj `./gradlew assembleStaging`, zainstaluj i powtórz scenariusz 6.1. W logu ma być czytelna nazwa, np. `Auth.WrongPassword`.

`adb logcat` działa na buildzie staging mimo `isDebuggable = false` — logcat jest systemowy i nie zależy od tej flagi. Wywołania `Log.e` nie są usuwane przez R8, bo nie mamy reguły `-assumenosideeffects`.
**Jeśli zobaczysz `a$b` albo podobny skrót — reguła `-keepnames` nie zadziałała** i cały zysk diagnostyczny przepada w buildach, które dostają testerzy.

---

## Blok 7 — Diagnostyka nierozwiązanego buga z `user_data`

**To nie jest test regresji.** Ten refaktor świadomie **nie naprawia** buga z kontami, które powstają w Firebase Auth bez wpisu w `user_data`. Ale teraz aplikacja potrafi powiedzieć, co się dzieje, więc warto zebrać dane.

Wykonaj na koncie testera, które wcześniej sprawiało problemy:

1. Tester **wylogowuje się** z aplikacji.
2. Ty kasujesz to konto z zakładki Auth w konsoli Firebase.
3. Tester **czyści dane aplikacji** w ustawieniach Androida.
4. Tester próbuje zalogować się przez Google i **zapisuje dosłowną treść komunikatu**.
5. Bez zamykania aplikacji próbuje **od razu drugi raz** i znowu zapisuje komunikat.

Co oznaczają odpowiedzi:

| Wynik | Wniosek |
|---|---|
| Przechodzi za pierwszym razem | Konto było trwale zepsute wcześniejszą nieudaną próbą. Potwierdza diagnozę |
| Pierwsza próba: „Coś poszło nie tak", druga: „Nie znaleziono danych" | Pierwsza próba padła na tworzeniu wpisu, druga poszła już ścieżką logowania. Konto zepsuło się na naszych oczach |
| Za każdym razem to samo | Awaria jest powtarzalna, log z bloku 6 wskaże miejsce |

W każdym z tych przypadków **weź log** (`adb logcat -s AppError`) — teraz wskaże konkretną metodę i konkretny wariant błędu, zamiast „nieznanego błędu".

---

## Czego **nie** zgłaszać jako błąd

Te zmiany są zamierzone:

1. Anulowanie okna wyboru konta Google nie pokazuje już żadnego dialogu.
2. Brak internetu daje komunikat o braku połączenia zamiast „Wystąpił nieznany błąd".
3. Brak konta Google na urządzeniu daje własny komunikat.
4. Sklep bez internetu pokazuje polski komunikat zamiast angielskiego „Error loading store: ...".
5. Operacje na koncie bez aktywnej sesji pokazują komunikat zamiast zawieszać ekran.

---

## Raportowanie

Dla każdego znalezionego problemu podaj:

- **numer punktu** z tej listy (np. 2.3),
- **dosłowną treść komunikatu** — najlepiej zrzut ekranu, nie parafrazę,
- **czas reakcji**: czy błąd pojawił się od razu, czy po kilkunastu sekundach kręciołka,
- **urządzenie i wersję Androida**,
- **rodzaj konta**: e-mail czy Google, a jeśli Google, to czy zwykłe `@gmail.com`, czy służbowe/Workspace,
- **czy da się powtórzyć**.

Rozróżnienie „od razu" kontra „po kręciołku" jest istotne — wskazuje na zupełnie inne przyczyny.

---

## Tabela wyników

| Blok | Zakres | Status | Uwagi |
|---|---|---|---|
| 0 | Smoke | ☐ | |
| 1 | Logowanie i rejestracja | ☐ | |
| 2 | Brak internetu | ☐ | |
| 3 | Tryby quizowe | ☐ | |
| 4 | Zakupy | ☐ | |
| 5 | Ustawienia konta | ☐ | |
| 6 | Logi (deweloper) | ☐ | |
| 7 | Diagnostyka `user_data` | ☐ | |
