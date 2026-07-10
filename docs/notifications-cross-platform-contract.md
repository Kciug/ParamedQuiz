# Powiadomienia — kontrakt cross-platform (iOS + panel wysyłki)

> Uzupełnienie do `notifications-spec.md`. Zawiera **konkretne stałe, schematy i payloady**, które ustaliła implementacja Android — po to, by iOS zachował się identycznie, a panel wysyłki (w edytorze pytań) produkował wiadomości zrozumiałe dla obu platform.
> Wszystko poniżej to **wspólny kontrakt** — nazwy kluczy, kolekcji i wartości muszą być **identyczne** na iOS i Androidzie.

---

## Część 1. Logika lokalna (przypomnienia Daily/Streak/Revision/Win-back)

Te powiadomienia są planowane **lokalnie** na urządzeniu (iOS: `UNUserNotificationCenter`; Android: WorkManager). Logika i dane muszą być spójne.

### 1.1 Parametry decyzji (źródło: Firestore, patrz 1.2; wartości domyślne = fallback)

| Parametr | Wartość domyślna | Uwaga |
|----------|------------------|-------|
| `streakMinValue` | 2 | Min. długość serii, by wysłać Streak Reminder |
| `winbackDays` | `[7, 14]` | Progi nieaktywności (dni) dla Win-backu |
| `revisionIntervalDays` | 3 | Min. odstęp między przypomnieniami o powtórce |
| `minWeakQuestions` | 3 | **Nie w Firestore** — stała w kodzie; min. liczba słabych pytań, by wysłać Revision |
| domyślna godzina | 19:00 | Konfigurowalna przez usera; `defaultReminderHour` z Firestore **nie jest konsumowany** (patrz niżej) |

### 1.2 Schemat Firestore (współdzielony przez obie platformy)

**Dokument `app_config/notifications_config`:**
```
streakMinValue: number        (np. 2)
winbackDays: number[]         (np. [7, 14])
revisionIntervalDays: number  (np. 3)
defaultReminderHour: number   (np. 19)  // pole kontraktowe; Android go NIE używa (godzinę ustawia user)
```

**Kolekcja `notification_templates`** (każdy dokument = jeden szablon tekstu):
```
type: string     // "daily" | "streak" | "revision" | "winback"
text: string     // treść; patrz placeholdery 1.5
enabled: boolean // false = pominięty
day: number      // TYLKO dla type="winback": próg dnia (7 lub 14); dla innych 0/pomiń
```

Zasady:
- Puste pule (brak `enabled` szablonów danego typu) → platforma używa **lokalnego fallbacku** (Załącznik A w `notifications-spec.md`).
- **Odświeżanie: max raz na 7 dni** (time-boxing), z lokalnym cache i fallbackiem offline. iOS implementuje ten sam interwał.
- Nazwy pól/kolekcji są **kontraktem** — identyczne na obu platformach.

### 1.3 Definicje (liczone lokalnie, identycznie)

- **Dzień zaliczony (DONE):** `lastStreakUpdateDate` = dziś (porównanie po dacie, bez czasu). `daysSince = 0`.
- **PENDING:** `daysSince == 1` (ostatnie zaliczenie wczoraj).
- **daysSince:** pełne dni między północą `lastStreakUpdateDate` a północą dziś (lokalna strefa).
- **Słabe pytanie (Revision):** z danych `seenQuestions` — `timesSeen > 0 && timesCorrect / timesSeen < 0.5`. Revision kwalifikuje się, gdy liczba takich pytań `>= minWeakQuestions (3)`.
- **⚠️ NIE używać** metody typu „getStreakState" jeśli ma efekt uboczny (na Androidzie zerowała streak) — do decyzji czytamy samą datę i liczymy dzień.

### 1.4 Kolejność decyzji (jednoprzebiegowa, 1 powiadomienie / uruchomienie)

```
1. DONE dziś            -> nic nie wysyłaj
2. PENDING & streak≥min -> Streak Reminder
3. daysSince ∈ winbackDays (i nie wysłany dla tego progu) -> Win-back
4. słabych pytań ≥ min & minął revisionIntervalDays       -> Revision
5. w przeciwnym razie   -> Daily Reminder
```

> **Decyzja Androida (odejście od literalnego §2.6):** Win-back ma pierwszeństwo **przed** Revision (na progach 7/14 komunikat reaktywacyjny jest silniejszy; słabe pytania i tak zwykle istnieją i inaczej Revision blokowałby win-back). iOS powinien zrobić **tak samo**.

Guardy stanu (lokalne):
- **Win-back raz na próg:** zapamiętaj ostatnio wysłany próg; reset przy ponownym DONE.
- **Revision:** zapamiętaj datę ostatniego revision-remindera (interwał 3 dni).
- **1/dzień** wynika z jednego planowania dziennego (brak osobnego guardu).

### 1.5 Placeholdery w tekstach
- Obsługiwany: **`{streak}`** (podstawiany liczbą dni serii w szablonach `type="streak"`).
- **Nie** używać innych placeholderów (np. `{count}`) — nie są podstawiane. Jeśli potrzebne, dodać na obu platformach jednocześnie.

### 1.6 Flow zgody (priming) — stałe
- Trigger: po **pierwszej ukończonej sesji** (`completedQuizzes >= 1`).
- Max **3 pokazania**, odstęp **≥ 7 dni**; po odmowie systemowej — **terminalny stop**.
- Deep-linki lokalnych przypomnień: Daily/Streak/Win-back → HOME; Revision → REVISIONS (patrz 2.3).

---

## Część 2. FCM — Content/Marketing (dla iOS + panel wysyłki)

Powiadomienia „nowości/marketing" idą **zdalnie** przez FCM jako **broadcast na temat** (bez backendu per-user, bez przechowywania tokenów).

### 2.1 Temat (topic)
- Obie platformy subskrybują temat **`content`**.
- Subskrypcja jest **bramką zgody**: gdy user wyłączy powiadomienia (Android: główny toggle), aplikacja **odsubskrybowuje** temat. iOS analogicznie.

### 2.2 Struktura wiadomości (co panel musi wysłać — FCM HTTP v1)

```json
{
  "message": {
    "topic": "content",
    "notification": { "title": "Tytuł", "body": "Treść" },
    "data": {
      "notification_destination": "HOME",
      "channel": "news"
    },
    "android": {
      "notification": { "channel_id": "news" }
    },
    "apns": {
      "payload": { "aps": { "sound": "default" } }
    }
  }
}
```

Pola wspólne (klucze **niezmienne**):
- `notification.title`, `notification.body` — treść.
- `data.notification_destination` — cel deep-linku: **`HOME`** lub **`REVISIONS`** (patrz 2.3).
- `data.channel` — kategoria: **`news`** lub **`marketing`** (dla logiki po stronie klienta / iOS).

Android-specyficzne:
- `android.notification.channel_id` = **`news`** (domyślne) lub **`marketing`**. **Bez tego pola marketing trafi na kanał „Nowości"** — dla kampanii marketingowej **trzeba** ustawić `marketing`.

iOS-specyficzne:
- `apns.payload.aps` — `sound`, `badge`, ew. `interruption-level`. iOS czyta cel z `userInfo["notification_destination"]` i kategorię z `userInfo["channel"]`.

### 2.3 Cele deep-linku (`notification_destination`)
| Wartość | Android | iOS (do odwzorowania) |
|---------|---------|------------------------|
| `HOME` | Ekran główny | Ekran główny |
| `REVISIONS` | Ekran konfiguracji powtórek | Odpowiednik powtórek; jeśli brak — fallback HOME |

> Rozszerzenia (np. konkretny zestaw pytań, sklep) wymagają **dodania wartości na obu platformach naraz** + w panelu.

### 2.4 Rozbieżność kategorii: Android channels vs iOS ⚠️
- **Android** rozdziela „Nowości" i „Marketing" jako **systemowe kanały powiadomień** — user włącza/wyłącza każdą kategorię w **ustawieniach systemowych** (apka tylko linkuje). Brak osobnego toggla w apce.
- **iOS nie ma odpowiednika kanałów** w ustawieniach systemowych (kontrola jest na poziomie apki). Więc „wyłącz marketing, zostaw nowości" **nie jest natywnie dostępne** na iOS przez ustawienia systemu.
- **Opcje dla iOS (do decyzji zespołu iOS):**
  1. Akceptacja kontroli na poziomie apki (wszystko-albo-nic) — najprościej.
  2. **Osobne tematy** `content_news` / `content_marketing` + własny in-app toggle → user odsubskrybowuje marketing. Najbliżej parytetu granularności. Wymagałoby wsparcia wyboru tematu w panelu (patrz 2.5) i ewentualnie dołożenia tego po stronie Androida.
  3. iOS notification categories/actions — bez per-kategorię on/off w Settings.
- **Rekomendacja:** na start opcja 1 (spójne z „delegujemy do systemu"), a jeśli potrzeba granularności — wspólnie przejść na opcję 2 (osobne tematy) na obu platformach.

### 2.5 Panel wysyłki (w edytorze pytań) — czego potrzebuje
- **API:** FCM **HTTP v1** (`https://fcm.googleapis.com/v1/projects/{PROJECT_ID}/messages:send`). Legacy „server key" jest wycofany.
- **Autoryzacja:** service account (Firebase Admin SDK) — plik JSON z uprawnieniem do wysyłki FCM; z niego OAuth2 access token. Trzymać po stronie **backendu panelu**, nie w kliencie.
- **Pola formularza (minimum):**
  - Tytuł, Treść.
  - Kategoria: `news` | `marketing` → mapuje na `data.channel` **oraz** `android.notification.channel_id`.
  - Cel (deep-link): `HOME` | `REVISIONS` → `data.notification_destination`.
  - (Opcjonalnie później) wybór tematu, jeśli przejdziecie na osobne tematy per kategoria.
- **Wysyłka:** `message.topic = "content"` (albo per-kategoria temat, jeśli opcja 2).
- **Uwaga:** dla marketingu ZAWSZE ustawić `android.notification.channel_id = "marketing"`, inaczej Android pokaże to jako „Nowości".

### 2.6 Zachowanie foreground vs tło (dla świadomości)
- **Tło/ubita:** system wyświetla powiadomienie sam (Android — wg `channel_id`; iOS — wg `aps`).
- **Foreground:** klient przejmuje wiadomość i wyświetla sam (Android już to robi; iOS: `willPresent` w delegacie).

---

## Część 3. Co dostarczyć / na co uważać — skrót dla iOS i panelu

**Dla iOS (parytet):**
- ☐ Ten dokument + `notifications-spec.md`.
- ☐ Identyczny schemat Firestore (1.2) + interwał odświeżania 7 dni + fallback.
- ☐ Identyczne stałe i kolejność decyzji (1.1, 1.4) — w tym **Win-back > Revision**.
- ☐ Identyczne definicje DONE/PENDING/„słabe pytanie" (1.3) i placeholder `{streak}`.
- ☐ Subskrypcja tematu `content` bramkowana zgodą; decyzja o kategoriach news/marketing (2.4).
- ☐ Mapowanie deep-linków `HOME`/`REVISIONS` (2.3), klucze z `userInfo`.

**Dla panelu wysyłki:**
- ☐ FCM HTTP v1 + service account (2.5).
- ☐ Payload wg 2.2 (klucze niezmienne), z `channel_id` dla marketingu.
- ☐ Formularz: tytuł/treść/kategoria/cel.
