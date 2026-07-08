# Powiadomienia

***

### TL;DR

**Po co:** powiadomienia = CTA do regularnej nauki (retencja), nie sprzedaż. Każde musi dać wartość w momencie kliknięcia.

**Zasady:** max 1 powiadomienie/dzień, treść personalizowana, pełna kontrola w ustawieniach, nie pytamy o zgodę na starcie.

**5 typów:**

1. **Daily Reminder** — codziennie (domyślnie 19:00), jeśli user dziś jeszcze nie zaliczył dnia (DONE).

2. **Streak Reminder** — wieczorem, gdy seria zagrożona (PENDING) i ≥ 2 dni.

3. **Revision Reminder** — co ≥ 3 dni, gdy są słabe pytania (WORST/UNDER_50).

4. **Win-back** — po 7/14/30 dniach bez zaliczonego dnia.

5. **Content/Marketing** — nowości, sterowane zdalnie.

**Pierwszeństwo przy kolizji:** Streak > Revision > Daily. Win-back tylko dla nieaktywnych.

**Zgoda:** dopiero po pierwszej ukończonej sesji → własny pre-prompt → dopiero potem dialog systemowy (chronimy „jeden strzał"). Max 2 ponowienia co 7 dni.

***

## 1. Założenia

### 1.1 Cel biznesowy

Powiadomienia w aplikacji służą **jednemu głównemu celowi: budowaniu nawyku regularnej nauki** (retencja / powracalność użytkownika), a nie sprzedaży ani agresywnemu marketingowi.

Grupa docelowa (ratownicy medyczni, osoby uczące się do egzaminów) uczy się w cyklach i pod presją czasu do egzaminu — dla nich **regularność jest ważniejsza niż intensywność**. Powiadomienie ma być pomocnikiem w nauce, a nie przypominajką „że apka istnieje".

### 1.2 Zasada nadrzędna

> Każde powiadomienie musi dostarczać użytkownikowi realnej wartości w momencie kliknięcia.

Przykład dobry: „Rozwiąż dziś 5 pytań z kardiologii". Przykład zły: „Wróć do nas! Tęsknimy 😢".

Każde powiadomienie ma:

* jasny, konkretny **powód&#x20;**(dlaczego user je dostaje teraz),

* jedną **akcję&#x20;**(co ma zrobić po kliknięciu),

* **deep-link** prowadzący dokładnie do miejsca, o którym mówi treść (nie do ekranu głównego).

### 1.3 Zasady ogólne

1. **Częstotliwość:** maksymalnie 1 powiadomienie dziennie w normalnym trybie działania. Wyjątki (np. ważny komunikat) muszą być rzadkie i świadome.

2. **Zgoda w dobrym momencie:** NIE prosimy o zgodę na powiadomienia przy pierwszym uruchomieniu. Prośbę pokazujemy dopiero, gdy user wykaże zaangażowanie (np. po ukończeniu pierwszego quizu). Zwiększa to znacząco konwersję zgody. Szczegóły flow — w osobnej części.

3. **Pełna kontrola użytkownika:** w ustawieniach aplikacji user może:

   * włączyć/wyłączyć powiadomienia globalnie,

   * ustawić godzinę przypomnień,

   * włączać/wyłączać poszczególne typy. Kontrola buduje zaufanie i zmniejsza liczbę odinstalowań.

4. **Personalizacja treści:** treść powinna odnosić się do zachowania usera (np. słabe kategorie, ostatnia aktywność, przerwana seria), zamiast być generyczna.

5. **Brak duplikacji i spamu:** jeśli user był dziś aktywny, nie wysyłamy „przypomnienia o nauce" tego samego dnia. Powiadomienia reagują na realny stan, nie lecą „na ślepo".

***

## 2. Katalog typów powiadomień

Każdy typ opisany jest wg jednego schematu: **cel&#x20;**→ **trigger&#x20;**→ **warunki wysłania** → **treść&#x20;**→ **deep-link**.

Wszystkie godziny odnoszą się do lokalnej strefy czasowej urządzenia. Domyślna pora przypomnień: 19:00 (konfigurowalna przez usera).

### 2.1 Daily Reminder

**Cel**: fundament budowania nawyku. Codzienny, delikatny sygnał „czas na naukę".

**Trigger**: codziennie o godzinie ustawionej przez usera (domyślnie 19:00).

**Warunki wysłania:**

* Powiadomienia są włączone i zgoda udzielona.

* User nie zaliczył dziś dnia w rozumieniu streaka (jeśli już dziś zaliczył — pomijamy, nie spamujemy).

* **Definicja „dzień zaliczony"** = stan streaka DONE (patrz 2.7). Dzień liczy się jako zaliczony dopiero, gdy user faktycznie się uczył (podbił streak), a nie gdy tylko otworzył apkę. To celowe — nagradzamy realną naukę.

**Treść&#x20;**(rotacja — nie ta sama codziennie, żeby się nie „wypaliła"): Zestaw szablonów, losowany/rotowany tak, by user nie widział tego samego tekstu dzień po dniu. Przykłady:

* „⏱️ 5 minut nauki dziś? Rozwiąż szybki zestaw pytań."

* „💊 Czas na dawkę wiedzy — sprawdź się z farmakologii."

* „🚑 Gotowy na dziś? Kilka pytań czeka."

* „📚 Twoja codzienna porcja pytań jest gotowa."

* „🧠 Utrwal wiedzę — krótka sesja przed snem?"

**Deep-link:** główny ekran.

### 2.2 Streak Reminder

**Cel**: wykorzystać najsilniejszy mechanizm nawykowy — niechęć do przerwania serii (efekt „nie zepsuj passy").

**Trigger**: wieczorem (np. 20:00–21:00, po głównym oknie daily reminder), gdy seria jest zagrożona.

**Warunki wysłania**:

* Stan streaka = PENDING (patrz 2.7) — czyli seria żyje (ostatnie zaliczenie było wczoraj), ale dziś jeszcze nie zaliczona i przepadnie o północy, jeśli user nic nie zrobi.

* Wartość serii ≥ 2 (nie ma sensu „ratować" 1-dniowej serii — słaby haczyk emocjonalny; próg do ewentualnej korekty).

* Powiadomienia włączone.

* Nie wysyłamy jednocześnie daily remindera i streak remindera tego samego wieczoru — streak ma pierwszeństwo, gdy seria istnieje (jest silniejszym motywatorem). Patrz reguły pierwszeństwa (2.6).

**Treść**:

* „🔥 Masz serię {X} dni! Nie przerywaj jej — jedna sesja wystarczy."

* „🔥 {X}-dniowa passa jest zagrożona. Wskocz na chwilę, żeby ją utrzymać."

**Deep-link**: główny ekran.

### 2.3 Powtórka

**Cel**: merytorycznie najwartościowszy typ — zachęca do powtórzenia pytań, które userowi idą słabo. Spina się z istniejącym modułem powtórek.

**Trigger**: cyklicznie, nie częściej niż raz na 3 dni (interwał — patrz niżej), o porze przypomnień.

**Warunki wysłania**:

* User ma statystyki odpowiedzi kwalifikujące pytania do powtórki (istnieją pytania wg kryterium WORST lub UNDER_50 — czyli jest realnie co powtarzać).

* Od ostatniego wysłania revision remindera minęły ≥ 3 dni (żeby nie przypominać o powtórce codziennie).

* Powiadomienia włączone.

* Respektuje limit 1/dzień i reguły pierwszeństwa (2.6).

> Interwał (do decyzji biznesowej): proponowane 3 dni. Uzasadnienie: powtórka słabych pytań co ~3 dni utrwala materiał bez natręctwa i nie kanibalizuje codziennego daily remindera. Wartość konfigurowalna centralnie (patrz część techniczna / Firestore).

**Treść**:

* „🔁 Masz {X} pytań, które idą Ci słabo — powtórz je i podbij wynik."

* „🔁 Czas na powtórkę słabych punktów. Kilka pytań wystarczy, żeby je opanować."

* „🎯 Twoje najsłabsze pytania czekają na rewanż."

**Deep-link**: ekran konfiguracji powtórki (najlepiej z domyślnie wybranym kryterium słabych pytań (WORST / UNDER_50))

### 2.4 Win-back - reaktywacja nieaktywnego użytkownika

**Cel**: odzyskać usera, który przestał korzystać z aplikacji, zanim odinstaluje.

**Trigger**: brak zaliczonego dnia przez 7 i 14 dni (dwa progi, każdy wysyłany jednorazowo).

> **Definicja „nieaktywności"**: liczymy dni od ostatniego dnia DONE (ostatniego zaliczonego dnia streaka), a nie od ostatniego otwarcia aplikacji. Uzasadnienie: dzień DONE zdobywa się trywialnie łatwo (wg logiki streaka wystarczą 3 odpowiedzi), więc jest to dobry i tani proxy realnego zaangażowania — jedno źródło prawdy (streak), które już mamy. Samo otwarcie apki bez nauki nie liczy się jako aktywność.

**Warunki wysłania**:

* Liczba dni od ostatniego DONE = dokładnie jeden z progów (7/14).

* Powiadomienia włączone.

* Maksymalnie jedno win-back w danym progu (nie zapętlamy).

* Delikatny ton — to najłatwiej irytujący typ.

**Treść**:

* 7 dni: „👋 Dawno Cię nie było — wróć na krótką sesję i nie trać formy."

* 14 dni: „📚 Twoja wiedza czeka. 5 pytań, żeby wrócić do rytmu?"

**Deep-link**: Ekran główny

### 2.5 Content / Marketing - nowe treści i komunikaty

**Cel**: poinformować o nowych zestawach pytań, aktualizacjach, ważnych zmianach.

**Trigger**: ręcznie/centralnie, sterowane przez zespół (nie automat na urządzeniu).

**Warunki wysłania**:

* Rzadko (żeby nie psuć zaufania zbudowanego przez wartościowe powiadomienia).

* Powiadomienia włączone (docelowo: osobny toggle „nowości i aktualizacje").

**Treść&#x20;**(przykłady):

* „🆕 Nowy zestaw pytań: {temat}. Sprawdź się!"

* „✨ Zaktualizowaliśmy pytania z {kategoria} zgodnie z nowymi wytycznymi."

**Deep-link**: konkretny nowy zestaw / ekran nowości.

**Zależność**: wymaga pushu zdalnego (FCM)— patrz część techniczna.

### 2.6 Reguły pierwszeństwa

Ponieważ obowiązuje limit 1 powiadomienie dziennie, przy kolizji obowiązuje kolejność:

1. **Streak Reminder** (seria zagrożona) — najsilniejszy motywator, wygrywa.

2. **Revision&#x20;**(powtórka dojrzała) — wysoka wartość merytoryczna.

3. **Daily Reminder** — bazowy fallback, gdy nic wyżej się nie kwalifikuje.

4. **Win-back** — tylko dla userów nieaktywnych (z definicji nie koliduje z powyższymi, bo tamte wymagają aktywności/serii).

Content/Marketing jest sterowany osobno i może (świadomie, rzadko) złamać limit 1/dzień — decyzja zespołu przy konkretnej kampanii.

***

## 3. Flow zgody na powiadomienia

### 3.1 Zasada nadrzędna

**Nie prosimy o zgodę przy pierwszym uruchomieniu**. Prośbę pokazujemy dopiero, gdy user wykazał zaangażowanie.

Prośba o zgodę na „zimno", przy starcie, ma niską konwersję i marnuje szansę — a na iOS i Androidzie 13+ systemowy dialog można pokazać tylko raz. Jeśli user odmówi za pierwszym razem, kolejnej prośby systemowej nie da się już wywołać (trzeba go odsyłać do ustawień systemu). Dlatego moment prośby jest kluczowy i musi być identyczny na obu platformach.

### 3.2 Kiedy prosimy

Prośbę o zgodę pokazujemy **po pierwszym realnym sukcesie użytkownika**, czyli:

* Po ukończeniu pierwszej sesji nauki w quizach (z zaliczeniem streaka)

* Po rozwiązaniu zadania dnia

W tym momencie user rozumie już wartość aplikacji, więc chętniej zgadza się na przypomnienia, które pomogą mu wracać.

### 3.3 Pre-permission prompt

Zanim pokażemy systemowy dialog zgody, pokazujemy własny (in-app) ekran zachęty — tzw. pre-permission prompt / priming.

**Zachowanie**:

1. Po spełnieniu warunku z 3.2 pokazujemy własny dialog: krótko tłumaczy korzyść.

   * Nagłówek (przykład): „Ucz się regularnie 📚"

   * Treść (przykład): „Włącz przypomnienia, a delikatnie damy Ci znać, kiedy warto wrócić do nauki. Bez spamu — maksymalnie raz dziennie."

   * Przyciski: „Włącz przypomnienia" (primary) / „Może później" (secondary).

2. Jeśli user kliknie „Włącz przypomnienia" → dopiero wtedy pokazujemy systemowy dialog zgody.

3. Jeśli user kliknie „Może później" → zamykamy dialog, nie wywołujemy systemowego dialogu. Możemy ponowić własny pre-prompt później (patrz 3.5), bo to nie zużywa systemowego „jednego strzału".

### 3.4 Ścieżki wyniku (systemowy dialog)

|                 |                                                                                                                                                                                                |
| --------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Zgoda udzielona | Włączamy powiadomienia. Ustawiamy domyślnie daily reminder na 19:00. Planujemy powiadomienia.                                                                                                  |
| Odmowa          | Nie planujemy powiadomień. Zapisujemy fakt odmowy. Nie ponawiamy systemowego dialogu. W ustawieniach aplikacji pokazujemy stan „wyłączone" z podpowiedzią, jak włączyć w ustawieniach systemu. |

### 3.5 Ponawianie pre-promptu

Jeśli user w pre-prompcie (3.3) wybrał „Może później" (a więc systemowy dialog się nie pokazał), możemy delikatnie ponowić własny pre-prompt:

* Ponawiamy nie wcześniej niż po 7 dniach od poprzedniego pre-promptu.

* Maksymalnie 2 ponowienia, potem odpuszczamy na dobre (żeby nie irytować).

* Zawsze przez własny ekran, nigdy „na siłę".

### 3.6 Włączenie z poziomu ustawień aplikacji

Niezależnie od powyższego, user zawsze może sam włączyć powiadomienia w ustawieniach aplikacji (patrz Część 4):

* Jeśli zgoda systemowa jeszcze nieudzielona → toggle uruchamia pre-prompt/systemowy dialog.

* Jeśli user wcześniej odmówił na poziomie systemu → toggle informuje, że trzeba włączyć powiadomienia w ustawieniach systemu, i (jeśli platforma pozwala) prowadzi go tam skrótem (deep-link do ustawień systemowych aplikacji).

***

## 4. Ustawienia w aplikacji

### 4.1 Zakres kontroli użytkownika

Dajemy userowi realną kontrolę — to zmniejsza odinstalowania i buduje zaufanie.

1. Główny przełącznik powiadomień - nadrzędny; wyłączenie ucisza wszystkie powiadomienia.

2. Godzina codziennego przypomnienia - wybór godziny (domyślnie 19:00)

3. Deep-link do ustawień systemowych - użytkownik może tam przełączać konkretne typy powiadomień

### 4.2 Zachowanie przełącznika głównego

|                                            |                                                                                                                                                                                              |
| ------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Zgoda systemowa udzielona                  | Toggle po prostu włącza/wyłącza planowanie powiadomień w aplikacji.                                                                                                                          |
| Zgoda systemowa jeszcze nieudzielona       | Włączenie toggla uruchamia flow zgody z Części 3 (pre-prompt → dialog systemowy).                                                                                                            |
| User wcześniej odmówił na poziomie systemu | Toggle pokazuje stan „wyłączone" i komunikat, że trzeba włączyć powiadomienia w ustawieniach systemu; jeśli platforma pozwala — przycisk/skrót prowadzący do ustawień systemowych aplikacji. |

**Stan toggla musi odzwierciedlać rzeczywistość systemową** — jeśli user wyłączył powiadomienia w ustawieniach systemu, aplikacja po powrocie pokazuje je jako wyłączone (nie kłamiemy „włączone", gdy system je blokuje).

### 4.3 Wybór godziny

* Prosty picker godziny (format zgodny z ustawieniami urządzenia, 24h/12h).

* Zmiana godziny przeplanowuje przyszłe przypomnienia.

* Domyślnie 19:00.

* Dotyczy przypomnień planowanych lokalnie (daily/streak/revision). Nie dotyczy powiadomień content (te idą zdalnie, o porze ustalonej przy kampanii).

***

## 5. Aspekty techniczne

### 5.1 Wyzwalanie powiadomienia

Większość naszych powiadomień to przypomnienia oparte na danych, które urządzenie zna samo (stan streaka, ostatnia aktywność, słabe pytania). Takich powiadomień nie ma sensu przepychać przez serwer — planujemy je lokalnie na urządzeniu. FCM/APNs jest potrzebne tylko tam, gdzie treść pochodzi z zewnątrz (nowe zestawy, kampanie).

|                           |                   |
| ------------------------- | ----------------- |
| Typ powiadomienia         | Mechanizm         |
| Daily Reminder (2.1)      | Lokalnie          |
| Streak Reminder (2.2)     | Lokalnie          |
| Revision Reminder (2.3)   | Lokalnie          |
| Win-back (2.4)            | Lokalnie          |
| Content / Marketing (2.5) | Push Zdalny (FCM) |

> **Ważne:** powiadomienia nie są „ślepym budzikiem". O ustalonej porze aplikacja/worker ocenia warunki (stan streaka, czy jest co powtarzać, aktywność) i decyduje, który typ wysłać wg reguł pierwszeństwa (2.6) — albo nie wysyła nic, jeśli dzień jest już DONE.

### 5.2 Model "oceń i zdecyduj"

O porze przypomnień (i wieczornym oknie streaka) uruchamiamy wspólną procedurę decyzyjną — identyczną na obu platformach:

```text
1. Czy powiadomienia włączone i zgoda udzielona?  → nie: koniec.
2. Pobierz stan streaka (DONE / PENDING / MISSED).
3. Jeśli DONE → nic nie wysyłaj (user już się dziś uczył).
4. Jeśli PENDING i streak ≥ 2 → wyślij Streak Reminder. Koniec.
5. W przeciwnym razie sprawdź Revision:
   - są słabe pytania (WORST/UNDER_50) ORAZ minęły ≥ 3 dni od ostatniego revision remindera?
     → wyślij Revision Reminder. Koniec.
6. W przeciwnym razie → wyślij Daily Reminder (losowy szablon z puli). Koniec.
7. (Osobno) Win-back: jeśli liczba dni od ostatniego DONE = 7/14/30 → wyślij właściwy win-back.
```

### 5.3 Źródło treści i parametrów

Teksty i parametry trzymamy w **Firestore**, żeby aktualizować je bez wydania nowej wersji aplikacji (obie platformy czytają tę samą kolekcję).

Proponowana struktura (do doprecyzowania przy implementacji):

```text
notifications_config/            (dokument konfiguracyjny)
├── revisionIntervalDays: 3
├── streakMinValue: 2
├── winbackDays: [7, 14, 30]
└── defaultReminderHour: 19

notification_templates/          (kolekcja szablonów treści)
├── {id}: { type: "daily",    text: "⏱️ 5 minut nauki dziś? ...", enabled: true }
├── {id}: { type: "streak",   text: "🔥 Masz serię {streak} dni! ...", enabled: true }
├── {id}: { type: "revision", text: "🔁 Masz {count} pytań, które ...", enabled: true }
└── {id}: { type: "winback",  text: "👋 Dawno Cię nie było ...", day: 7, enabled: true }
```

Zasady:

* Aplikacja cache'uje konfigurację i szablony lokalnie. Powiadomienia planowane lokalnie muszą działać offline, więc zawsze korzystamy z ostatniej znanej wersji, a przy pierwszym uruchomieniu bez sieci — z fallbacku wbudowanego w apkę (Załącznik A).

* Placeholdery w tekstach ({streak}, {count}, {category}) podstawia aplikacja lokalnie na podstawie danych usera.

* Pole enabled pozwala wygaszać pojedyncze szablony bez usuwania. • Nazwy pól/kolekcji są wspólnym kontraktem — iOS i Android muszą używać identycznych kluczy.

#### Strategia odświeżania (minimalizacja odczytów z Firestore):

Config zmienia się rzadko (kilka tekstów + parametry), więc nie trzymamy stałego listenera ani nie odpytujemy serwera przy każdym starcie. Wzorzec:

1. Domyślnie czytamy z lokalnego cache'u (koszt: 0 odczytów) — natychmiast, offline-friendly.

2. Do serwera sięgamy tylko, gdy cache jest starszy niż 7 dni (time-boxing). Zapisujemy lokalnie lastConfigFetch; przy starcie apki: jeśli teraz - lastConfigFetch ≥ 7 dni → jednorazowy get() z serwera, odświeżenie cache'u i znacznika. W przeciwnym razie — nic nie pobieramy.

3. Jeden get() zamiast listenera — odczytujemy raz, w tle, bez utrzymywania połączenia (listener liczyłby odczyty przy każdej zmianie i trzymał socket — niepotrzebne dla danych zmienianych raz na miesiące).

4. Firestore ma wbudowaną trwałą pamięć podręczną (offline persistence) — włączamy ją, dzięki czemu nawet przy błędzie sieci get() zwraca ostatnie znane dane bez kosztu.

### 5.3 Przypadki brzegowe

|                                                      |                                                                                                                                                                 |
| ---------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Strefa czasowa / zmiana czasu                        | Godzina przypomnienia odnosi się do lokalnej strefy urządzenia; po zmianie strefy/DST przeplanować.                                                             |
| Reboot urządzenia (Android)                          | Przeplanować powiadomienia po restarcie (BOOT_COMPLETED), bo AlarmManager/część zadań nie przeżywa reboota.                                                     |
| Brak sieci                                           | Planowanie lokalne działa offline na cache'owanej konfiguracji + fallbacku.                                                                                     |
| User uczył się w innej aplikacji/na innym urządzeniu | Stan streaka jest źródłem prawdy; jeśli synchronizowany przez konto — po synchronizacji nie wysyłamy „fałszywego" remindera (ocena warunków tuż przed wysyłką). |
| Zmiana godziny w ustawieniach                        | Natychmiast przeplanować przyszłe przypomnienia.                                                                                                                |
| Cofnięcie zgody w systemie                           | Wykryć przy starcie, odzwierciedlić w ustawieniach aplikacji (4.2), zaprzestać planowania.                                                                      |
| Podwójne powiadomienie                               | Idempotencja — max jedno powiadomienie „przypominające" na dzień (limit 1/dzień, 2.6).                                                                          |

***

# Załącznik A - Fallbackowa pula tekstów (wbudowana w aplikację)

> Używana, gdy Firestore jest niedostępny (np. pierwsze uruchomienie offline). Docelowe/rozszerzone teksty żyją w Firestore. Obie platformy zawierają identyczną listę fallback.

Daily Reminder:

* „⏱️ 5 minut nauki dziś? Rozwiąż szybki zestaw pytań."

* „💊 Czas na dawkę wiedzy — sprawdź się z pytań."

* „🚑 Gotowy na dziś? Kilka pytań czeka."

* „📚 Twoja codzienna porcja pytań jest gotowa."

* „🧠 Utrwal wiedzę — krótka sesja przed snem?"

Streak Reminder:

* „🔥 Masz serię {streak} dni! Nie przerywaj jej — jedna sesja wystarczy."

* „🔥 {streak}-dniowa passa jest zagrożona. Wskocz na chwilę, żeby ją utrzymać."

Revision Reminder:

* „🔁 Masz pytania, które idą Ci słabo — powtórz je i podbij wynik."

* „🎯 Twoje najsłabsze pytania czekają na rewanż."

Win-back:

* 7 dni: „👋 Dawno Cię nie było — wróć na krótką sesję i nie trać formy."

* 14 dni: „📚 Twoja wiedza czeka. 5 pytań, żeby wrócić do rytmu?"
