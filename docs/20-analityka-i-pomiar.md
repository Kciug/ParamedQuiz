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
| `AnalyticsEvent` | `core/analytics` | `sealed interface` — cały kontrakt zdarzeń w jednym pliku |
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

### 3.1 Monetyzacja

| Event | Kiedy | Parametry |
|---|---|---|
| `paywall_shown` | Pokazanie oferty (sklep, panel na Home, okno kategorii, panel końca triala) | `surface`, `product_id`, `has_price` |
| `purchase_started` | Klik w zakup, tuż przed `launchBillingFlow` | `surface`, `product_id`, `price_micros`, `currency` |
| `purchase_completed` | `PurchaseResult.Success` | `surface`, `product_id`, `value`, `currency` |
| `purchase` (standard GA4) | Razem z `purchase_completed` | `value`, `currency`, `items` |
| `purchase_pending` | `PurchaseResult.Pending` | `surface`, `product_id` |
| `purchase_cancelled` | `PurchaseResult.Cancelled` | `surface`, `product_id` |
| `purchase_failed` | `PurchaseResult.Error` | `surface`, `product_id`, `error_code` |
| `paywall_price_missing` | Próba zakupu bez `ProductDetails` w cache | `surface`, `product_id` |
| `trial_started` | Start sesji trybu w wersji próbnej | `mode` |
| `trial_wall_reached` | Wyczerpanie puli darmowych pytań | `mode`, `questions_answered` |

`paywall_price_missing` łapie cichą utratę przychodu: `launchBillingFlow` po cichu nie robi nic, gdy
produktu nie ma w cache — użytkownik klika „Kup" i nie dzieje się nic.

### 3.2 Użycie

| Event | Kiedy | Parametry |
|---|---|---|
| `screen_view` | Zmiana destynacji w głównym `NavHost` | `screen_name`, `screen_class` |
| `mode_selected` | Wybór trybu z menu na ekranie głównym | `mode`, `locked` |
| `addon_tapped` | Tap w dodatek | `addon`, `available` |
| `category_selected` | Wybór kategorii | `mode`, `category_id`, `locked` |
| `quiz_started` | Inicjalizacja sesji | `mode`, `source`, `questions_count`, `is_trial` |
| `quiz_finished` | Finalizacja sesji | `mode`, `completion`, `questions_answered`, `correct_answers`, `duration_sec`, `is_trial` |
| `revisions_configured` | Start sesji powtórek | `criterion`, `mode`, `categories_count`, `questions_count` |
| `onboarding_finished` | Zakończenie onboardingu głównego | `skipped`, `last_page` |
| `mode_onboarding_finished` | Zakończenie onboardingu trybu | `mode` |

`locked` zastępuje osobne zdarzenie o tapnięciu w zablokowaną treść — mierzy popyt na treść jeszcze
niekupioną, co jest inną diagnozą niż sam wolumen sprzedaży.

### 3.3 Retencja

| Event | Kiedy | Parametry |
|---|---|---|
| `rating_prompt_shown` | Karta oceny na Home | — |
| `rating_prompt_answered` | Wybór w karcie oceny | `rating`, `action` |
| `notification_prompt_shown` | Prompt zgody na powiadomienia | — |
| `notification_prompt_answered` | Odpowiedź na prompt | `action` |
| `notification_opened` | Tap w powiadomienie (deep link) | `destination` |
| `news_banner_dismissed` | Odrzucenie banera nowości | `banner_id` |
| `signup_completed` | Udane logowanie lub rejestracja | `method` |
| `issue_reported` | Wysłanie zgłoszenia problemu z pytaniem | `mode` |

### 3.4 Zdrowie

| Event | Kiedy | Parametry |
|---|---|---|
| `app_error` | Każde wywołanie `ErrorLogger.log` | `origin`, `error_type` |

---

## 4. Słowniki wartości

| Parametr | Dozwolone wartości |
|---|---|
| `surface` | `store`, `home_sheet`, `category_sheet`, `trial_end`, `unknown` |
| `mode` | `main`, `swipe`, `translation`, `cem`, `revisions` |
| `source` | `home`, `category`, `daily_exercise`, `revisions` |
| `completion` | `completed`, `early_exit` |
| `addon` | `daily`, `revisions`, `store` |
| `criterion` | `worst`, `best`, `under_50` |
| `destination` | `home`, `revisions` |
| `method` | `password`, `google` |
| `action` (ocena) | `store`, `feedback`, `dismiss`, `never_again` |
| `action` (powiadomienia) | `accepted`, `denied`, `dismissed` |
| `error_code` | wariant `AppError.Billing` w snake_case, np. `item_already_owned` |
| `error_type` | wariant `AppError` z przestrzenią, np. `billing_product_details_missing`, `no_network` |
| `product_id` | SKU z Google Play (`BillingIds`), w tym dynamiczne `mediquiz_<categoryId>` |

Wartości logiczne (`locked`, `is_trial`, `has_price`, `available`, `skipped`) wysyłamy jako tekst
`true`/`false` — GA4 nie ma typu logicznego w parametrach.

---

## 5. Właściwości użytkownika

| Właściwość | Wartości | Źródło |
|---|---|---|
| `build_type` | `release`, `staging`, `debug` | `BuildConfig` modułu `:analytics` |
| `premium_tier` | `none`, `partial`, `full` | `PremiumStatusProvider.ownedProductIds` |
| `ads_disabled` | `true`, `false` | `PremiumStatusProvider.isAdsFree` + `GameplayConfigProvider.adsEnabled()` |
| `is_logged_in` | `true`, `false` | `UserManager` |
| `notifications_on` | `true`, `false` | `SharedPreferencesApi.isNotificationsEnabled()` (przełącznik w aplikacji, nie uprawnienie systemowe) |
| `streak_bucket` | `0`, `1_3`, `4_7`, `8_30`, `30_plus` | `ScoreManager.getScoreFlow()` |

`build_type` nie jest opcjonalne: wariant `staging` ma **ten sam** `applicationId` co produkcja, więc
bez tej właściwości ruch z internal tracka zanieczyściłby dane produkcyjne. Ta sama wartość idzie do
Crashlytics jako custom key.

Właściwości są **kolekcjonowane**, nie odczytywane jednorazowo na starcie: premium startuje pustym
zbiorem, a seria zerem — migawka w `Application.onCreate` raportowałaby `premium_tier = none` dla
każdego płacącego przez pierwsze sekundy sesji.

---

## 6. Zgody (Consent Mode)

- Domyślne zgody (odmowa) są zadeklarowane w manifeście — działają zanim wystartuje kod aplikacji.
- Przy starcie aplikacji czytamy zapisany stan TCF (`IABTCF_*` w domyślnych `SharedPreferences`) i
  ustawiamy zgodę, żeby powracający użytkownik nie tracił pierwszych zdarzeń sesji.
- Po ustaleniu zgody przez UMP (callback `gatherConsent` w `AdManagerImpl`) zgoda jest ustawiana
  ponownie — to jedyny moment, w którym stan jest pewny. Wywołanie jest poza gałęzią warunkową,
  więc obejmuje też użytkowników premium i okres wyłączonych reklam.
- Mapowanie celów TCF (do potwierdzenia przy aktualizacji polityki prywatności — nie jest to opinia
  prawna): P1 → `analytics_storage` i `ad_storage`; P1 + P7 → `ad_user_data`; P3 + P4 → `ad_personalization`.

---

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

- Wymiary (tekstowe): `surface`, `product_id`, `mode`, `source`, `completion`, `error_code`,
  `error_type`, `origin`, `action`, `addon`, `criterion`, `destination`, `method`, `category_id`,
  `locked`, `is_trial`, `has_price`, `available`, `skipped`, `banner_id`
- Metryki (liczbowe): `questions_count`, `questions_answered`, `correct_answers`, `duration_sec`,
  `price_micros`, `value`, `rating`, `categories_count`, `last_page`

**Pozostałe**

- [x] Play Console ↔ Firebase (`MQ-66-T`)
- [x] AdMob ↔ Firebase (`MQ-67-T`) — daje `ad_impression` i przychód reklamowy bez własnych zdarzeń
- [ ] Aktualizacja polityki prywatności o analitykę i Crashlytics (`MQ-65-T`)
- [ ] Data Safety w Play Console (`MQ-64-T`)
- [ ] Po 24 h: weryfikacja, czy wszystkie wymiary są widoczne w raportach
- [ ] Kontrola zgodności: liczba `purchase_completed` względem transakcji w Play Console

---

## 9. Ograniczenia i świadome odstępstwa

- Standardowe raporty GA4 mają opóźnienie do ~24 h. Do developmentu służą DebugView i Realtime.
- GA4 nie robi backfillu — dane zaczynają się w dniu wydania wersji z instrumentacją.
- Limity: 500 nazw zdarzeń (kontrakt definiuje ~27), 25 parametrów na zdarzenie, 25 właściwości
  użytkownika, nazwa do 40 znaków, wartość parametru do 100 znaków.
- Zagnieżdżone `NavHost`y trybów mają własne kontrolery i nie są objęte `screen_view`; ekrany
  wewnętrzne pokrywają jawne zdarzenia niosące więcej informacji.
- `notification_opened` niesie tylko cel deep-linku (`home`/`revisions`), nie typ powiadomienia —
  dopięcie do konkretnego przypomnienia wymagałoby zmiany kontraktu cross-platform.
- `AppError.Billing.ItemAlreadyOwned` trafia do `purchase_failed` z własnym `error_code` — to nie
  jest utrata przychodu, tylko produkt już posiadany.
- `AppError.Billing.ProductDetailsMissing` daje dwa zdarzenia: `app_error` (zdrowie) i
  `paywall_price_missing` (monetyzacja). Odpowiadają na różne pytania — świadomie nie deduplikujemy.
