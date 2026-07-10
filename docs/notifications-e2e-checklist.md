# Powiadomienia — checklista testów E2E (QA)

> Testy manualne całej funkcji powiadomień (Android). Większość scenariuszy odpalasz przez **Opcje deweloperskie** (ukryte w release; w buildzie testowym/staging dostępne).
> Legenda: ☐ do zrobienia. „Dev → X" = przycisk w Opcjach deweloperskich.

## Macierz urządzeń / stanów
Przetestuj przynajmniej:
- **Android 13+ (API 33+)** — runtime permission `POST_NOTIFICATIONS`.
- **Android ≤ 12 (API 24–32)** — brak runtime permission (powiadomienia domyślnie dozwolone).
- Stany aplikacji dla deep-linków/FCM: **foreground**, **tło**, **ubita (killed)**.

Uwaga o silniku decyzji: „Dev → Uruchom przypomnienie teraz" ocenia **bieżący** stan konta i wysyła **jeden** typ przypomnienia. Stan konta ustawiasz najpierw przez sim („Symuluj…"). Powiadomienia muszą być **włączone**, żeby cokolwiek się pokazało.

---

## A. Uprawnienia i kanały
- ☐ (API 33+) Świeża instalacja → przy pierwszym uruchomieniu **NIE** ma prośby o powiadomienia.
- ☐ Dev → „Wyślij testowe powiadomienie": na API 33+ pojawia się systemowy dialog zgody; po zgodzie notyfikacja się wyświetla. Na API 24 — notyfikacja od razu.
- ☐ Odmowa zgody → notyfikacja się nie pokazuje, brak crasha.
- ☐ Ustawienia → „Ustawienia systemowe powiadomień" → widoczne **3 kanały**: „Przypomnienia", „Nowości i aktualizacje", „Marketing".

## B. Ustawienia w aplikacji
- ☐ Sekcja „Aplikacja" widoczna również dla **użytkownika anonimowego** (niezalogowanego).
- ☐ Toggle „Powiadomienia" ON (API 33+, brak zgody) → dialog systemowy → po zgodzie toggle zostaje ON.
- ☐ Restart aplikacji → toggle i godzina zachowane.
- ☐ Wyłącz powiadomienia w **ustawieniach systemu** → wróć do ekranu → toggle pokazuje **OFF** (odświeżenie na wejściu).
- ☐ Wiersz „Godzina przypomnienia" pokazuje bieżącą wartość (domyślnie 19:00); picker zapisuje nową; wartość utrzymuje się po restarcie.
- ☐ Wiersz „Ustawienia systemowe powiadomień" otwiera systemowy ekran powiadomień aplikacji.

## C. Flow zgody (priming po pierwszej sesji)
- ☐ Dev → „Wymuś prośbę o zgodę na powiadomienia" → wejście na ekran główny → pojawia się **priming-dialog** („Ucz się regularnie").
- ☐ „Włącz przypomnienia" (API 33+, brak zgody) → dialog systemowy; po zgodzie toggle w Ustawieniach = ON, prompt nie wraca.
- ☐ „Może później" → dialog znika; **kolejne wejścia na home tego samego dnia go nie pokazują** (odstęp 7 dni; max 3 pokazania).
- ☐ Odmowa w dialogu systemowym → prompt **nie wraca** (terminalny stop).
- ☐ (API 24) „Włącz przypomnienia" włącza od razu (brak runtime permission).
- ☐ Priming i karta „Oceń nas!" mogą pojawić się **razem** (dialog nad kartą) — priming NIE jest blokowany obecnością karty oceny.

## D. Silnik przypomnień lokalnych (decyzja)
Powiadomienia włączone. Dla każdego: ustaw sim → „Dev → Uruchom przypomnienie teraz".
- ☐ **Daily** — świeże konto bez streaka/słabych pytań → Daily Reminder.
- ☐ **Streak** — Dev → „Symuluj streak w toku (wczoraj)" → przypomnienie o serii z liczbą dni.
- ☐ **Win-back 7** — Dev → „Symuluj brak aktywności 7 dni" → win-back „Dawno Cię nie było…".
- ☐ **Win-back 14** — Dev → „Symuluj brak aktywności 14 dni" → win-back „Twoja wiedza czeka…".
- ☐ **Revision** — Dev → „Symuluj słabe pytania" → przypomnienie o powtórce; tap → **ekran konfiguracji powtórek**.
- ☐ **DONE dziś (brak spamu)** — zrób realnie quiz/zadanie dnia (zalicz streak dziś) → „Uruchom przypomnienie teraz" → **brak** powiadomienia.
- ☐ **Wyłączone** — wyłącz powiadomienia → „Uruchom przypomnienie teraz" → **nic** nie wysyła.

### Reguły pierwszeństwa / brak duplikatów
- ☐ **Win-back > Revision**: najpierw „Symuluj słabe pytania", potem „Symuluj brak aktywności 7 dni" → „Uruchom przypomnienie teraz" → pojawia się **Win-back** (nie Revision).
- ☐ **Brak duplikatu win-backu**: po win-backu drugie „Uruchom przypomnienie teraz" tego samego progu **nie** pokazuje ponownie win-backu.
- ☐ **Interwał powtórki**: „Symuluj słabe pytania" → Revision; drugie „Uruchom" od razu → **brak Revision** (interwał 3 dni).

## E. Deep-linki (tap w powiadomienie)
Dla każdego z 3 stanów aplikacji (foreground / tło / ubita):
- ☐ Tap w Daily/Streak/Win-back → otwiera **ekran główny**.
- ☐ Tap w Revision → otwiera **ekran konfiguracji powtórek**.
- ☐ Po tapnięciu z apki ubitej — apka startuje na właściwym ekranie (nie tylko launcher).

## F. Planowanie (scheduling)
- ☐ Ustaw godzinę przypomnienia na ~2 min do przodu, włączone, dzień niezaliczony → poczekaj → powiadomienie wpada samo o tej porze.
- ☐ Po wysłaniu — praca przeplanowuje się na kolejny dzień (kolejnego dnia znów zadziała).
- ☐ Zmiana godziny w Ustawieniach → praca przeplanowana na nową porę.
- ☐ (Opcjonalnie) Reboot urządzenia → zaplanowane przypomnienie przetrwało restart.

## G. FCM Content/Marketing
- ☐ Włącz powiadomienia → apka subskrybuje temat `content` (sanity: brak crasha; w logach FCM subskrypcja OK).
- ☐ Dev → „Symuluj powiadomienie Nowości" → powiadomienie na kanale **„Nowości i aktualizacje"**.
- ☐ Dev → „Symuluj powiadomienie Marketing" → powiadomienie na kanale **„Marketing"**.
- ☐ **Kampania z konsoli (news)** → wyślij z Firebase Console do tematu `content` bez `android_channel_id` → trafia na kanał „Nowości"; foreground i tło; tap otwiera właściwy ekran (z custom data `notification_destination`).
- ☐ **Kampania z konsoli (marketing)** → z `android_channel_id = marketing` → trafia na kanał „Marketing".
- ☐ **Blokada kanału**: zablokuj w systemie kanał „Marketing" → marketing się **nie pokazuje**; „Nowości" nadal działają.
- ☐ Wyłącz powiadomienia w apce → po chwili (propagacja subskrypcji) kampania **nie dochodzi**.
- ☐ Deep-link z FCM: kampania z `notification_destination = REVISIONS` → tap → ekran powtórek.

## H. Konfiguracja z Firestore (teksty/parametry)
- ☐ **Bez dokumentów w Firestore** → wszystko działa na tekstach/parametrach fallback (jak wyżej).
- ☐ Utwórz `app_config/notifications_config` + kolekcję `notification_templates` (patrz kontrakt cross-platform) → Dev → „Wymuś odświeżenie konfiguracji" → zmienione teksty/parametry widoczne przy „Uruchom przypomnienie teraz".
- ☐ **Offline po jednym udanym odświeżeniu** → wartości nadal z lokalnego cache (nie wraca do fallbacku).

## I. Sprzątanie po testach
- ☐ Dev → „Reset statystyk oceny" / „Reset onboardingu" itd. przywracają stan wyjściowy (dla powtórnych testów flowu zgody/oceny).
