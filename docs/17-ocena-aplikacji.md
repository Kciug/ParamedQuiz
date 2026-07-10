# 17 — Ocena aplikacji (in-app rating)

## Metryka

| | |
|---|---|
| **Moduł** | `core` (`composables/rating`), `home` |
| **Typ** | Warstwa zaangażowania (ocena / feedback) |
| **Dostępność** | Wszyscy spełniający kryteria |
| **Powiązane featury** | [Ekran główny](04-ekran-glowny.md) · [Zgłaszanie błędów / feedback](18-zglaszanie-bledow.md) |

---

## 1. Cel funkcji

Mechanizm proszenia o **ocenę aplikacji** ma pozyskiwać oceny w sklepie od zadowolonych użytkowników, a od niezadowolonych — **prywatny feedback** (zamiast negatywnej oceny publicznej). Prośba pojawia się na [ekranie głównym](04-ekran-glowny.md) dopiero, gdy użytkownik ma już realne doświadczenie z aplikacją.

---

## 2. Kiedy pojawia się prośba (kwalifikacja)

Prośba jest pokazywana, gdy **wszystkie** warunki są spełnione:
- użytkownik **nie ocenił** jeszcze aplikacji **i** nie wyłączył próśb,
- minęły co najmniej **3 dni** od instalacji,
- ukończono co najmniej **5 quizów**,
- od poprzedniego pokazania minęło co najmniej **14 dni**.

---

## 3. Przepływ użytkownika

1. **Pytanie o ocenę** — użytkownik wybiera ocenę (skala 1–5).
2. Rozgałęzienie po ocenie:
   - **ocena wysoka (≥ 4)** → zachęta do wystawienia oceny w sklepie; wybór uruchamia natywny proces oceny w sklepie,
   - **ocena niska (< 4)** → formularz **prywatnego feedbacku** (opis + ocena), wysyłany do zespołu.
3. **Opcje dodatkowe:**
   - **wyślij feedback** — zapisuje opinię i oznacza aplikację jako ocenioną,
   - **więcej nie pytaj** — trwale wyłącza prośby,
   - **odrzuć** — zamyka prośbę (z krokiem potwierdzenia); nie blokuje przyszłych próśb (z zachowaniem odstępu).

Po wystawieniu oceny w sklepie lub wysłaniu feedbacku prośba nie jest ponawiana.

---

## 4. Reguły biznesowe

- **Rozdzielenie ścieżek:** zadowoleni → sklep; niezadowoleni → prywatny feedback (ochrona średniej ocen).
- **Antyspam:** limity oparte o datę instalacji, liczbę ukończonych quizów i odstęp między pokazaniami.
- **Stan trwały:** „ocenione" oraz „nie pytaj więcej" wyłączają dalsze prośby.
- **Współistnienie z prośbą o powiadomienia:** prośba o ocenę (karta w treści) może wystąpić równolegle z [prośbą o zgodę na powiadomienia](16-powiadomienia.md).
- Wysłany feedback trafia do [zgłoszeń/feedbacku](18-zglaszanie-bledow.md).

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Warunki niespełnione | Prośba się nie pojawia. |
| Ocena ≥ 4 | Ścieżka do oceny w sklepie. |
| Ocena < 4 | Ścieżka do prywatnego feedbacku. |
| „Nie pytaj więcej" | Trwałe wyłączenie próśb. |
| Odrzucenie | Zamknięcie z potwierdzeniem; możliwe ponowienie po odstępie. |
| Błąd wysyłki feedbacku | Komunikat błędu; feedback niewysłany. |

---

## 6. Powiązania

- **[Ekran główny](04-ekran-glowny.md)** — miejsce prezentacji prośby.
- **[Zgłaszanie błędów / feedback](18-zglaszanie-bledow.md)** — kanał prywatnego feedbacku.
- **[Powiadomienia](16-powiadomienia.md)** — niezależny prompt mogący współistnieć.
