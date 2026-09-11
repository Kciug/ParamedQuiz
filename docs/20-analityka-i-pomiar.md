# 20 — Analityka i pomiar

> Dokument **techniczny** (wyjątek od konwencji katalogu, który opisuje funkcje biznesowo).
> To **kontrakt pomiarowy**: definiuje nazwy zdarzeń, ich parametry i miejsca wyzwolenia.
> Nazwa zdarzenia wysłana raz na produkcję jest niezmienna, więc zmiany w tym pliku traktujemy
> jak zmiany w publicznym API. Pełne uzasadnienie decyzji: dokument `20 — Analityka i pomiar` w Sync.

| | |
|---|---|
| **Moduły** | `:analytics` (implementacje Firebase), `core/analytics` (interfejs + kontrakt), `:billing` (lejek zakupowy) |
| **Stack** | Firebase Analytics (GA4) + Crashlytics + powiązanie AdMob z Firebase |
| **Dostępność** | Wszyscy użytkownicy; w buildach `debug` zbieranie jest wyłączone |
| **Powiązane** | [Sklep i zakupy](14-sklep-i-zakupy.md) · [Reklamy](15-reklamy.md) · [Ekran główny](04-ekran-glowny.md) · [Mechanika quizu](00-mechanika-quizu.md) · [Powiadomienia](16-powiadomienia.md) · [Ocena aplikacji](17-ocena-aplikacji.md) · [Konfiguracja zdalna](19-konfiguracja-zdalna.md) |

---

## 1. Po co to jest

Instrumentujemy **pytania, nie ekrany**:

1. Który z pięciu punktów sprzedaży faktycznie sprzedaje i gdzie przecieka lejek?
2. Co się sprzedaje: pakiet, pojedyncze tryby, brak reklam czy kategorie?
3. Czy wersje próbne konwertują i ilu użytkowników dochodzi do ściany triala?
4. Które tryby są używane, jak głęboko (start kontra ukończenie), a które są martwe?
5. Czy zbudowane pętle (seria, powiadomienia, prośba o ocenę) cokolwiek zmieniają?

---

## 2. Architektura

| Element | Lokalizacja | Rola |
|---|---|---|
| `AnalyticsLogger` | `core/analytics` | Jedyny punkt wysyłki. Żaden moduł nie woła Firebase bezpośrednio |
| `AnalyticsControls` | `core/analytics` | Sterowanie dostawcą (zgody, zbieranie, reset). Konsumuje je wyłącznie menedżer zgody |
| `AnalyticsConsentManager` | `core/analytics` | Właściciel decyzji użytkownika i jedyny pisarz zgód |
| `ConsentGatedAnalyticsLogger` + `AnalyticsCollectionGate` | `core/analytics` | Odcinają zdarzenia przed zgodą |
| `AnalyticsEvent` | `core/analytics` | `sealed interface` — cały kontrakt zdarzeń w jednym pliku |
| `LocalAnalyticsLogger` + `TrackScreenViews` | `core/analytics` | `screen_view` z zagnieżdżonych `NavHost`ów, które nie mają ViewModelu |
| `ScreenNames` | `:app/analytics` | Mapa tras głównego `NavHost`a na nazwy ekranów; pomija kontenery trybów |
| `AnalyticsUserProperty` | `core/analytics` | Właściwości użytkownika |
| `LogcatAnalyticsLogger` | `core/analytics` | Implementacja dla buildów debug |
| `RecordingAnalyticsLogger` | `core/testing` | Atrapa dla testów (brak `testFixtures` w projekcie) |
| `CrashReporter` / `NoOpCrashReporter` | `core/error` | Cienki interfejs nad Crashlytics |
| `FirebaseAnalyticsLogger`, `CrashlyticsCrashReporter` | `:analytics` | Implementacje produkcyjne |
| `AnalyticsModule` | `:analytics/di` | Jedyne wiązanie obu interfejsów; wybór po `BuildConfig.BUILD_TYPE_NAME` |
| `AppErrorLogger` | `core/error` | Implementacja `ErrorLogger`: logcat + Crashlytics + `app_error` |
| `PurchaseFunnelTracker` | `:billing` | Jedyne źródło zdarzeń terminalnych zakupu |
| `UserPropertySync` | `:app` | Kolektor stanu → właściwości użytkownika |

Zasady:

- **Jedno źródło wysyłki** — wszystko przez `AnalyticsLogger`.
- **Jedno źródło wyniku zakupu** — zdarzenia terminalne emituje wyłącznie `PurchaseFunnelTracker`.
  `BillingDataSource.purchaseResult` to gorący `SharedFlow` kolekcjonowany równolegle przez pięć
  ViewModeli; logowanie per ViewModel dawałoby duplikaty, a `Cancelled`/`Error` nie niosą `product_id`.
- **Podział z Firestore** — analityka mierzy zachowanie i lejki, Firestore i moduł `score` mierzą treść
  (statystyki per pytanie). Nie duplikujemy.
- **Kontrola kardynalności** — nie wysyłamy `question_id` ani treści pytań. `category_id` jest dopuszczalne.
- **Brak danych osobowych** — żadnego e-maila, nazwy użytkownika ani treści wpisywanych przez użytkownika.
- **Brak `setUserId`** — konto jest opcjonalne, więc anonimowy app-instance-id pokrywa 100% ruchu.

---

## 3. Kontrakt zdarzeń

Nazwy są konwencją Google, nie naszą: `obiekt_czasownik` w formie podstawowej (`quiz_start`,
`tutorial_complete`, `select_item`), nigdy imiesłów. `sign_up` i `login` to zdarzenia
**rekomendowane** przez Google — zasilają gotowe raporty i predefiniowane wymiary, więc nie mają
własnych odpowiedników w naszej przestrzeni nazw. Parametry oznaczone `?` są opcjonalne:
pomijamy je, gdy wartość jest nieznana — nigdy nie wysyłamy pustego stringa.

### 3.1 Monetyzacja

| Event | Kiedy | Parametry |
|---|---|---|
| `paywall_view` | Pokazanie oferty (sklep, panel na Home, okno kategorii, panel końca triala) | `paywall`, `product_id`, `has_price`, `mode`?, `category_id`? |
| `purchase_start` | Klik w zakup, tuż przed `launchBillingFlow` | `paywall`, `product_id`, `product_type`, `price_micros`, `currency` |
| `purchase_complete` | `PurchaseResult.Success` | `paywall`, `product_id`, `product_type`, `mode`?, `category_id`? |
| `purchase_pending` | `PurchaseResult.Pending` | `paywall`, `product_id` |
| `purchase_cancel` | `PurchaseResult.Cancelled` | `paywall`, `product_id` |
| `purchase_fail` | `PurchaseResult.Error` | `paywall`, `product_id`, `error_code` |
| `paywall_price_missing` | Próba zakupu bez `ProductDetails` w cache | `paywall`, `product_id` |
| `trial_start` | Start sesji trybu w wersji próbnej | `mode` |
| `trial_wall_reach` | Wyczerpanie puli darmowych pytań | `mode`, `answered_count` |

**Przychodu nie raportujemy własnym zdarzeniem.** `purchase_complete` jest znacznikiem lejka i
celowo nie niesie `value` ani `currency`, a standardowego `purchase` nie wysyłamy wcale: Firebase
zbiera `in_app_purchase` automatycznie, a GA4 **nie deduplikuje** go z ręcznie wysłanym `purchase`
na strumieniach aplikacyjnych (deduplikacja po `transaction_id` działa tylko na strumieniach
webowych). Oba zdarzenia razem podwajałyby raport przychodu. `price_micros` w `purchase_start`
zostaje — to cena oferty w chwili kliku, nie transakcja.

`paywall_price_missing` łapie cichą utratę przychodu: `launchBillingFlow` po cichu nie robi nic, gdy
produktu nie ma w cache — użytkownik klika „Kup" i nie dzieje się nic.

### 3.2 Użycie

| Event | Kiedy | Parametry |
|---|---|---|
| `screen_view` | Zmiana destynacji w głównym `NavHost` **i** w zagnieżdżonych `NavHost`ach trybów; ekran wyniku z ViewModelu | `screen_name`, `screen_class`, `mode`? |
| `mode_select` | Wybór trybu z menu na ekranie głównym | `mode`, `locked` |
| `addon_tap` | Tap w dodatek | `addon`, `available` |
| `category_select` | Wybór kategorii | `mode`, `category_id`, `locked` |
| `quiz_start` | Inicjalizacja sesji | `mode`, `quiz_type`, `question_count`, `is_free_preview`, `category_id`?, `category_name`? |
| `quiz_complete` | Finalizacja sesji | `mode`, `quiz_type`, `question_count`, `answered_count`, `correct_count`, `incorrect_count`, `is_early_exit`, `is_free_preview`, `duration_sec`, `max_streak`, `category_id`? |
| `revision_config` | Start sesji powtórek | `criterion`, `mode`, `category_count`, `question_count` |
| `onboarding_complete` | Zakończenie onboardingu głównego | `skipped`, `last_page` |
| `mode_onboarding_complete` | Zakończenie onboardingu trybu | `mode` |

`locked` zastępuje osobne zdarzenie o tapnięciu w zablokowaną treść — mierzy popyt na treść jeszcze
niekupioną, co jest inną diagnozą niż sam wolumen sprzedaży.

**Słownik `screen_name`** jest wspólny z iOS: `home`, `store`, `account`, `settings`,
`notification_settings` (główny `NavHost`, mapa w `ScreenNames`) oraz `categories`, `quiz`,
`quiz_end`, `revision_setup` (wewnątrz trybów). Te ostatnie raportują zagnieżdżone `NavHost`y
przez `TrackScreenViews` z loggerem z `LocalAnalyticsLogger`; **korzenie trybów są pomijane**,
żeby wejście w tryb nie dawało dwóch ekranów. `quiz_end` wychodzi z ViewModelu przy przejściu
w stan zakończenia — ekran wyniku to stan, nie trasa — i nie pojawia się przy wyjściu przed
pierwszą odpowiedzią. Nasze dodatki: `signup`, `onboarding`, `privacy_consent`,
`terms_of_service`, `dev_options`; `stats` zostaje iOS-only. Zadanie dnia to `quiz` z `mode = main`.

`mode` towarzyszy `categories`, `quiz` i `quiz_end` — iOS rozróżnia „quiz w trybie głównym" od
„quiz w Swipe" właśnie tym parametrem, a nie osobną nazwą ekranu. W powtórkach to tryb
powtarzanej treści, tak jak w zdarzeniach sesji. `screen_class` jest z definicji równe
`screen_name` (GA4 ma dla klasy wbudowany wymiar, który bez tego wypełniałaby nazwa Activity —
u nas zawsze ta sama).

`max_streak` to najdłuższa seria poprawnych odpowiedzi **pod rząd** w jednej sesji, liczona
w każdym trybie od zera (w Swipe osobno od `bestStreak`, który startuje od rekordu wszech czasów).
Nie mylić z `streak_count` w `streak_increment` — tam chodzi o serię dni.

`incorrect_count` liczymy w kontrakcie (`answered_count - correct_count`, przycięte do zera), żeby
raport nie wymagał wyliczanej metryki w konsoli.

**`quiz_type` i `is_free_preview` są zamrażane przy `quiz_start`.** Zakup w trakcie wersji próbnej
(Swipe, Tłumaczenia) przełącza sesję na pełną pulę bez restartu, ale `quiz_complete` raportuje typ,
z jakim sesja ruszyła — inaczej start i koniec tej samej sesji miałyby różne `quiz_type` i lejek
konwersji rozjeżdżałby się dokładnie na kohorcie, która kupiła. Ustalone z iOS (u nich tak samo).
`question_count` w `quiz_complete` to pula w chwili zakończenia — po konwersji większa niż
w `quiz_start`, ale `answered_count` nigdy jej nie przekroczy. Konwersję znaczy `purchase_complete`.

**Sesja powtórek ma `mode` treści, którą powtarza** (`main`, `cem`, `swipe`, `translations`), a nie
`revisions` — sam fakt powtórki niesie `quiz_type = revision`. Bez tego rozdziału nie dałoby się
powiedzieć, czego użytkownik powtarza. Dotyczy to wszystkich zdarzeń sesji powtórek, łącznie
z `issue_report`, więc `revisions` nie jest już emitowane jako wartość `mode` **nigdzie** —
zostaje wyłącznie w słowniku `addon` (kafelek na ekranie głównym).

### 3.3 Retencja

| Event | Kiedy | Parametry |
|---|---|---|
| `rating_prompt_view` | Karta oceny na Home | — |
| `rating_prompt_answer` | Wybór w karcie oceny | `rating`, `action` |
| `notification_prompt_view` | Prompt zgody na powiadomienia | — |
| `notification_prompt_answer` | Odpowiedź na prompt | `action` |
| `notification_tap` | Tap w powiadomienie (deep link) | `destination`, `is_remote` |
| `news_banner_dismiss` | Odrzucenie banera nowości | `banner_id` |
| `daily_quest_complete` | Zadanie dnia zaliczone (dzień zużyty) | `streak_count` |
| `streak_increment` | Podbicie serii dziennej, najwyżej raz na dobę | `streak_count` |
| `notification_permission` | Odpowiedź na **systemowy** dialog `POST_NOTIFICATIONS` | `granted` |
| `sign_up` | Utworzenie konta (rekomendowane przez Google) | `method` |
| `login` | Logowanie na istniejące konto (rekomendowane przez Google) | `method` |
| `sign_out` | Wylogowanie zakończone powodzeniem | — |
| `account_delete` | Konto usunięte na życzenie użytkownika | — |
| `issue_report` | Wysłanie zgłoszenia problemu z pytaniem | `mode` |

`notification_tap`, nie `notification_open`: ta druga nazwa jest zarezerwowana przez FCM i zbierana
automatycznie.

`daily_quest_complete` nie jest duplikatem `quiz_complete` z `quiz_type = daily_quest`: tamto zapada
także przy wyjściu przed pierwszą odpowiedzią, kiedy dzień **nie** jest zużyty. To zdarzenie stoi tam,
gdzie zapisujemy datę ostatniego zadania dnia.

`streak_increment` emituje `StreakManager`, nie ViewModele — podbicie serii woła każdy z sześciu
trybów, a warunek „ostatnia aktualizacja starsza niż dziś" jest jedyną trwałą bramką dobową.
`streak_count` to seria **po** podbiciu, więc restart po przerwaniu widać jako `streak_count = 1`.
Nie mylić z `max_streak` w `quiz_complete` — tam chodzi o serię poprawnych odpowiedzi w jednej sesji.

`notification_permission` dotyczy dialogu systemowego, `notification_prompt_*` — naszego pytania,
które go poprzedza. To pierwsze logujemy **raz na instalację** (`NotificationPermissionTracker`):
Android pokazuje dialog najwyżej dwa razy, a po trwałej odmowie oddaje `false` natychmiast, więc bez
bramki każde tapnięcie przełącznika w ustawieniach dawałoby fałszywe `granted = 0`.

`sign_up` i `login` emituje `AuthRepositoryImpl`, nie ViewModele. `signInWithGoogle` prowadzi raz
do rejestracji, raz do logowania (`additionalUserInfo.isNewUser`), a odtworzenie profilu po
utracie dokumentu w Firestore to logowanie, którego żaden ViewModel nie widzi.

### 3.4 Reklamy

| Event | Kiedy | Parametry |
|---|---|---|
| `ad_shown` | Interstitial faktycznie wyświetlony | `ad_format`, `ad_unit`, `answers_since_last_ad` |
| `ad_load_failed` | Nieudane załadowanie albo wyświetlenie | `stage`, `error_code`, `ad_unit` |

`ad_shown` nie dubluje automatycznego `ad_impression` z AdMob: tamto niesie przychód, to odpowiada
na pytanie, ile reklam widzi użytkownik i po ilu odpowiedziach — czyli czy częstotliwość z Remote
Config nie jest za agresywna. Zdarzenie zapada w `onAdShowedFullScreenContent`, nie przy zleceniu
pokazania. `answers_since_last_ad` liczy `QuizAdHandler` (jedyne miejsce, które widzi przebieg
sesji) i przekazuje je do `AdManager` w chwili decyzji.

`error_code` w `ad_load_failed` to **nazwa** kodu AdMob, nie liczba — wspólny słownik z iOS:
`no_fill`, `network_error`, `internal_error`, `invalid_request`, `app_id_missing`,
`mediation_no_fill` (ładowanie); `ad_reused`, `not_ready`, `app_not_foreground`,
`mediation_show_error` (prezentacja); nieznane jako `code_<n>`. Jedna nazwa parametru może być
w GA4 zarejestrowana raz, a `error_code` jest już wymiarem tekstowym dla `purchase_fail`.

### 3.5 Zdrowie

| Event | Kiedy | Parametry |
|---|---|---|
| `app_error` | Każde wywołanie `ErrorLogger.log` | `origin`, `error_type` |

---

## 4. Słowniki wartości

| Parametr | Dozwolone wartości |
|---|---|
| `paywall` | `store`, `category`, `mode`, `ad_free`, `trial_end`, `unknown` |
| `product_type` | `category`, `mode`, `ad_free`, `premium` |
| `mode` | `main`, `swipe`, `translations`, `cem` |
| `quiz_type` | `category`, `daily_quest`, `revision`, `full`, `free_preview` |
| `addon` | `daily`, `revisions`, `store` |
| `criterion` | `worst`, `best`, `under_50` |
| `destination` | `home`, `revisions` |
| `ad_format` | `interstitial` |
| `ad_unit` | `test`, `production` |
| `stage` | `load`, `present` |
| `method` | `email`, `google` |
| `action` (ocena) | `store`, `feedback`, `dismiss`, `never_again` |
| `action` (powiadomienia) | `accepted`, `denied`, `dismissed` |
| `error_code` | wariant `AppError.Billing` w snake_case (np. `item_already_owned`) albo nazwa kodu AdMob (np. `no_fill`, nieznane `code_<n>`) |
| `error_type` | wariant `AppError` z przestrzenią, np. `billing_product_details_missing`, `no_network` |
| `product_id` | SKU z Google Play (`BillingIds`), w tym dynamiczne `mediquiz_<categoryId>`; `unknown` dla wyniku bez zapamiętanego startu |

Uwaga na liczbę mnogą w `translations` — wartość zgadza się z SKU (`mediquiz_translations_mode`),
nie z nazwą modułu. `QuizMode.RevisionsMode` ma odwzorowanie na `revisions` w `analyticsName()`,
ale żadna ścieżka go już nie emituje — to wyłącznie zabezpieczenie wyczerpalności `when`. `paywall = ad_free` i `product_type = ad_free` są w słowniku dla zgodności z
iOS; Android nie ma dziś osobnego paywalla „bez reklam" (sprzedaje go ekran sklepu), więc emituje
wyłącznie `product_type`.

**Wartości logiczne** (`locked`, `is_free_preview`, `is_early_exit`, `is_remote`,
`has_price`, `available`, `skipped`, `granted`) wysyłamy jako `Long` **1/0**, nie jako tekst `true`/`false` — GA4 nie ma
typu logicznego w parametrach, a kontrakt cross-platform ustala postać liczbową. Właściwości
użytkownika to osobna przestrzeń: tam wartości są zawsze tekstem (patrz §5).

---

## 5. Właściwości użytkownika

| Właściwość | Wartości | Źródło |
|---|---|---|
| `build_type` | `release`, `staging`, `debug` (iOS: `debug`, `release`; TestFlight raportuje `release`) | `BuildConfig` modułu `:analytics`, ustawiane po włączeniu zbierania |
| `premium_tier` | `none`, `partial`, `full` | `PremiumStatusProvider.ownedProductIds` |
| `ads_disabled` | `true`, `false` | `PremiumStatusProvider.isAdsFree` + `GameplayConfigProvider.adsEnabled()` |
| `is_logged_in` | `true`, `false` | `UserManager` |
| `notifications_on` | `true`, `false` | `SharedPreferencesApi.isNotificationsEnabled()` (przełącznik w aplikacji, nie uprawnienie systemowe) |
| `streak_bucket` | `0`, `1_3`, `4_7`, `8_30`, `30_plus` | `ScoreManager.getScoreFlow()` |

`build_type` nie jest opcjonalne: wariant `staging` ma **ten sam** `applicationId` co produkcja, więc
bez tej właściwości ruch z internal tracka zanieczyściłby dane produkcyjne. Tę samą wartość
`UserPropertySync` wpisuje do Crashlytics przez `CrashReporter.setCustomKey` — inaczej crashe ze
stagingu byłyby tam nieodróżnialne od produkcyjnych (filtr po `versionName` z sufiksem `-staging`
działa, ale gubi się przy porównaniach między wersjami).

Właściwości są **kolekcjonowane**, nie odczytywane jednorazowo na starcie: premium startuje pustym
zbiorem, a seria zerem — migawka w `Application.onCreate` raportowałaby `premium_tier = none` dla
każdego płacącego przez pierwsze sekundy sesji.

Komplet właściwości jest ustawiany od nowa przy **każdym** wejściu zgody w stan udzielonej, nie
tylko przy pierwszym. Wycofanie zgody woła `resetAnalyticsData()`, które kasuje app-instance-id
razem z właściwościami; po ponownym włączeniu przełącznika mamy więc nową tożsamość, a kolektory
z `distinctUntilChanged` nie powtórzyłyby niezmienionych wartości.

---

## 6. Zgody (model opt-in)

Zbieranie jest **wyłączone domyślnie** i włącza je wyłącznie decyzja użytkownika. Model jest
wspólny z iOS (`analytics-events.md`), a jego zakres to `MQ-69-T`.

- **Stan decyzji** — `AnalyticsConsentState`: `UNDECIDED` | `GRANTED` | `DENIED`, zapisywany
  lokalnie (`SharedPreferences`, klucz `analytics_consent`). Nierozpoznana wartość degraduje się
  do `UNDECIDED`.
- **Ekran zgody** — trasa `PrivacyConsent`, pokazywana po akceptacji regulaminu, a użytkownikom
  sprzed aktualizacji przy pierwszym uruchomieniu. Bramka stoi w `MainActivityVM`, na ścieżce
  prowadzącej do ekranu głównego: użytkownik z aktualnym regulaminem nigdy nie przechodzi przez
  tamten ekran, a i tak musi zostać zapytany. Dwa jawne przyciski, brak pomijania i brak wyjścia
  gestem wstecz.
- **Wycofanie** — przełącznik w ustawieniach konta (sekcja „Prywatność"). Poza wyłączeniem
  zbierania czyści zebrane dane (`resetAnalyticsData`) i niewysłane raporty awarii.
- **Crashlytics podlega tej samej zgodzie.** Bez niej `isCrashlyticsCollectionEnabled = false`.
- **Nic nie jest logowane przed zgodą** — nie „nic nie jest wysyłane". Wstrzykiwany wszędzie
  `AnalyticsLogger` jest owinięty `ConsentGatedAnalyticsLogger`, który przy zamkniętej bramce nie
  przepuszcza zdarzeń ani właściwości użytkownika. Samo wyłączenie SDK zatrzymuje wysyłkę, ale nie
  powstrzymuje kodu przed produkowaniem zdarzeń, więc niezmiennik nie dałby się przetestować.
- **Brak zdarzeń o samej zgodzie** — przed decyzją i tak by nie wyszły, a po zgodzie mierzyłyby
  tylko jedną stronę rozkładu.
- **UMP odpowiada wyłącznie za reklamy.** `TcfConsentReader` zwraca `AdConsent` (trzy flagi
  reklamowe); `analytics_storage` pochodzi wyłącznie z decyzji użytkownika. Oba źródła są rozłączne
  i scala je `AnalyticsConsentManager` — jedyny pisarz `setConsent` w aplikacji.
- Mapowanie celów TCF na zgody reklamowe (do potwierdzenia przy aktualizacji polityki prywatności —
  nie jest to opinia prawna): P1 → `ad_storage`; P1 + P7 → `ad_user_data`; P3 + P4 → `ad_personalization`.

**Znane ograniczenie.** Firebase inicjalizuje się z `ContentProvidera`, zanim wykona się pierwsza
linia naszego kodu. Dla świeżych instalacji zamykają to flagi `firebase_analytics_collection_enabled`
i `firebase_crashlytics_collection_enabled` w manifeście. Na instalacjach, na których działał już
build z włączonym zbieraniem, SDK wstaje z zapisanym stanem i może przeciec jedna sesja przy
pierwszym uruchomieniu po aktualizacji — pierwsze zastosowanie zgody czyści te dane.

## 7. Weryfikacja

DebugView pokazuje zdarzenia z pełnym zestawem parametrów niezależnie od rejestracji custom dimensions:

```
adb shell setprop debug.firebase.analytics.app com.frontfolks.mediquiz
```

Wyłączenie: `adb shell setprop debug.firebase.analytics.app .none.`

W testach jednostkowych używamy `RecordingAnalyticsLogger` (`core/testing`) albo mocka interfejsu.
Harness E2E podmienia całą warstwę przez `FakeAnalyticsModule` — bez tego testy budowałyby
`FirebaseAnalyticsLogger`, a `FirebaseAnalytics.getInstance()` bez zainicjalizowanego `FirebaseApp`
rzuca wyjątkiem.

---

## 8. Checklista wdrożeniowa (konsole)

Rejestracja custom dimensions **jest częścią wdrożenia, nie opcją**: parametr niezarejestrowany trafia
do DebugView i BigQuery, ale **nie** do standardowych raportów. To najczęstsza przyczyna wrażenia,
że „analityka nie działa".

**Firebase → Analytics → Custom definitions**

- Wymiary tekstowe: `paywall`, `product_type`, `product_id`, `mode`, `quiz_type`, `currency`,
  `error_code`, `error_type`, `origin`, `action`, `addon`, `criterion`, `destination`, `method`,
  `banner_id`, `category_name`, `ad_format`, `ad_unit`, `stage`
- Wymiary o wartościach liczbowych (służą do segmentacji, nie do sumowania): `category_id`,
  `locked`, `has_price`, `available`, `skipped`, `is_free_preview`, `is_early_exit`, `is_remote`,
  `granted`
- Metryki: `question_count`, `answered_count`, `correct_count`, `incorrect_count`, `duration_sec`,
  `max_streak`, `price_micros`, `rating`, `category_count`, `last_page`, `streak_count`,
  `answers_since_last_ad`

`screen_name` i `screen_class` są parametrami zarezerwowanymi — rejestracji nie wymagają.
Wartości logiczne rejestrujemy jako **wymiary**, mimo liczbowej postaci: interesuje nas podział
ruchu na 1/0, a nie suma jedynek.

**Pozostałe**

- [x] Play Console ↔ Firebase (`MQ-66-T`)
- [x] AdMob ↔ Firebase (`MQ-67-T`) — daje `ad_impression` i przychód reklamowy bez własnych zdarzeń
- [ ] Aktualizacja polityki prywatności o analitykę i Crashlytics (`MQ-65-T`)
- [ ] Data Safety w Play Console (`MQ-64-T`)
- [ ] Po 24 h: weryfikacja, czy wszystkie wymiary są widoczne w raportach
- [ ] Kontrola zgodności: liczba `purchase_complete` względem transakcji w Play Console

---

## 9. Ograniczenia i świadome odstępstwa

- Standardowe raporty GA4 mają opóźnienie do ~24 h. Do developmentu służą DebugView i Realtime.
- GA4 nie robi backfillu — dane zaczynają się w dniu wydania wersji z instrumentacją.
- Nie ma zdarzenia per odpowiedź (`question_answered`). Obie platformy je wycofały: byłoby o rząd
  wielkości liczniejsze od wszystkich pozostałych razem (w Swipe i Tłumaczeniach jedna sesja to
  cała pula z Firestore), a `quiz_complete` niesie `answered_count`, `correct_count` i `max_streak`.
  Statystyki per pytanie mierzy Firestore.
- Limity: 500 nazw zdarzeń (kontrakt definiuje 35), 25 parametrów na zdarzenie, 25 właściwości
  użytkownika, 50 wymiarów o zasięgu zdarzenia, nazwa do 40 znaków, wartość parametru do 100 znaków.
- Zmiana nazwy zdarzenia po wydaniu jest nieodwracalna: GA4 nie robi backfillu, a „Modify event"
  nie działa wstecz — stara i nowa nazwa zostają dwoma trwale rozłącznymi szeregami.
- Zagnieżdżone `NavHost`y trybów mają własne kontrolery i nie są objęte `screen_view`; ekrany
  wewnętrzne pokrywają jawne zdarzenia niosące więcej informacji.
- `notification_tap` rozróżnia push od lokalnego przypomnienia (`is_remote`) i niesie cel
  deep-linku (`home`/`revisions`), ale nie konkretny rodzaj przypomnienia — to wymagałoby
  zmiany kontraktu cross-platform.
- `AppError.Billing.ItemAlreadyOwned` trafia do `purchase_fail` z własnym `error_code` — to nie
  jest utrata przychodu, tylko produkt już posiadany.
- `AppError.Billing.ProductDetailsMissing` daje dwa zdarzenia: `app_error` (zdrowie) i
  `paywall_price_missing` (monetyzacja). Odpowiadają na różne pytania — świadomie nie deduplikujemy.

### Rozbieżności wobec kontraktu iOS

Kontrakt jest wspólny, ale nie identyczny. Poniższe zestawienie jest stanem na dziś — porównane
pozycja po pozycji z `analytics-events.md` (MQ-11-T).

**Tylko Android — zdarzenia.** Cały lejek zakupowy poza `purchase_complete` (`purchase_start`,
`purchase_pending`, `purchase_cancel`, `purchase_fail`, `paywall_price_missing`), wersje próbne
(`trial_start`, `trial_wall_reach`), nawigacja po treści (`mode_select`, `category_select`,
`addon_tap`), `revision_config`, `mode_onboarding_complete`, prośba o ocenę
(`rating_prompt_view`/`rating_prompt_answer`), własny prompt powiadomień
(`notification_prompt_view`/`notification_prompt_answer`), `news_banner_dismiss`, `issue_report`,
`app_error`.

**Tylko Android — parametry.** `duration_sec` w sesji quizu; `skipped` i `last_page`
w `onboarding_complete` (iOS wysyła je bez parametrów); `product_id` i `has_price` w `paywall_view`;
`paywall` w zdarzeniach zakupu; wartości `paywall = trial_end` (rozdziela ścianę wersji próbnej od
zwykłego panelu trybu — bez niej nie da się policzyć konwersji triala) i `paywall = unknown`
(kubełek na wynik zakupu bez poprzedzającego startu, np. kod promocyjny z Google Play).

**Tylko iOS — zdarzenia, których nie emitujemy.** Zostało jedno: `restore_purchases`. Aplikacja
nie ma przycisku „Przywróć zakupy", a wszystkie wywołania `refreshPurchases()` są ciche (start
ekranu, połączenie z Play, `ITEM_ALREADY_OWNED`) — kontrakt wymaga natomiast tapnięcia
użytkownika. Wpięcie zdarzenia wymaga najpierw dodania tej akcji do UI.

**Uzgodnione z iOS:** właściwości użytkownika — iOS przyjął nasze `premium_tier`, `ads_disabled`,
`is_logged_in`, `build_type` (`notifications_on` i `streak_bucket` zostają tylko u nas, u iOS puste);
porzucenia liczone po naszemu, jako `quiz_complete` z `answered_count = 0`; słownik `screen_name`
w całości (patrz §3.2); `quiz_type` zamrażany przy starcie sesji; `question_answered` wycofane na obu
platformach; `error_code` jako nazwy.

`terms_of_service` i `privacy_consent` są w mapie tras, ale przed zgodą bramka je odrzuca — `privacy_consent` nie wychodzi nigdy, `terms_of_service` tylko wtedy,
gdy użytkownik ze zgodą musi zaakceptować nową wersję regulaminu.
