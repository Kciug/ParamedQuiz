# 01 — Onboarding i pierwsze uruchomienie

## Metryka

| | |
|---|---|
| **Moduł** | `home` (`presentation/onboarding`), `app` (routing startowy) |
| **Typ** | Przepływ wprowadzający |
| **Dostępność** | Wszyscy użytkownicy przy pierwszym uruchomieniu |
| **Powiązane featury** | [Regulamin](02-regulamin.md) · [Konto i logowanie](03-konto-i-logowanie.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) |

---

## 1. Cel funkcji

Onboarding to sekwencja ekranów wprowadzających prezentowana przy **pierwszym uruchomieniu** aplikacji. Ma przedstawić nowemu użytkownikowi charakter aplikacji (nauka ratownictwa medycznego), zachęcić do założenia/zalogowania konta i doprowadzić go do punktu startowego (ekran główny lub akceptacja regulaminu).

Onboarding trybów gry (osobne, jednorazowe wprowadzenia w poszczególnych trybach) opisano w dokumentach danych trybów.

---

## 2. Dostęp i warunki

- Onboarding wyświetlany jest **jednorazowo** — przy pierwszym uruchomieniu po instalacji.
- Po ukończeniu status jest zapamiętywany; kolejne uruchomienia pomijają onboarding i przechodzą wprost do rozwiązania trasy startowej (regulamin lub ekran główny).
- Onboarding **nie wymaga** posiadania konta — użytkownik może przejść dalej bez logowania (konto jest opcjonalne, patrz [Konto i logowanie](03-konto-i-logowanie.md)).

---

## 3. Przepływ użytkownika

### 3.1 Ekran powitalny (welcome)
1. Po pierwszym uruchomieniu użytkownik widzi ekran powitalny.
2. Dostępne akcje: **rozpocznij** onboarding, przejście do **logowania/rejestracji**, otwarcie **regulaminu**.

### 3.2 Sekwencja onboardingu
Po rozpoczęciu prezentowana jest sekwencja stron:
1. Kilka stron **informacyjnych** przedstawiających aplikację i jej wartości (czym jest, po co, jak wspiera naukę, aspekty wiarygodności/rzetelności treści).
2. Strona **konta**:
   - jeśli użytkownik jest **zalogowany** — pokazuje jego nazwę, e-mail oraz potwierdzenie zalogowania (dodatkowo oznaczenie „premium" przy awatarze, jeśli posiada pełny pakiet),
   - jeśli **niezalogowany** — pokazuje zachętę i przycisk przejścia do logowania/rejestracji.
3. Strona **końcowa** — podsumowanie / zaproszenie do startu.

### 3.3 Nawigacja w sekwencji
- Użytkownik przechodzi między stronami przyciskami **Dalej** / **Wstecz**; dostępny jest przycisk **Pomiń**.
- Na ostatniej stronie zamiast „Dalej" pojawia się **Zakończ**.
- Z pierwszej strony sekwencji „Wstecz" wraca do ekranu powitalnego.

### 3.4 Zakończenie
1. Po „Zakończ" onboarding jest oznaczany jako ukończony (trwale).
2. Aplikacja wyznacza **trasę startową**:
   - jeśli wymagana jest akceptacja regulaminu → ekran [Regulaminu](02-regulamin.md) (obowiązkowy),
   - w przeciwnym razie → ekran główny.

---

## 4. Reguły biznesowe

- **Jednorazowość:** onboarding pokazywany tylko do pierwszego ukończenia; status zapamiętywany lokalnie.
- **Konto opcjonalne:** brak logowania nie blokuje przejścia dalej. Na stronie konta w sekwencji przycisk „Pomiń" jest ukrywany dla niezalogowanego użytkownika (by zachęcić do logowania), ale przejście dalej („Dalej") pozostaje możliwe.
- **Rozpoznanie stanu konta:** strona konta odzwierciedla aktualny stan (zalogowany/niezalogowany, premium/nie-premium).
- **Regulamin po onboardingu:** dla świeżej instalacji akceptacja regulaminu jest wymagana zaraz po onboardingu (patrz [Regulamin](02-regulamin.md)).
- **Wstępne pobranie regulaminu:** treść regulaminu jest wstępnie pobierana w tle już podczas onboardingu, aby ekran regulaminu wyświetlił się bez opóźnienia.

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Użytkownik zaloguje się w trakcie onboardingu | Po powrocie strona konta pokazuje dane zalogowanego użytkownika i potwierdzenie. |
| Niezalogowany na stronie konta | „Pomiń" ukryte, ale można przejść dalej („Dalej"); konto pozostaje opcjonalne. |
| Brak sieci podczas onboardingu | Onboarding działa (treści statyczne); wstępne pobranie regulaminu może się nie powieść — obsłużone przy wejściu na ekran regulaminu. |
| Ponowne uruchomienie po ukończeniu | Onboarding pominięty; od razu rozwiązanie trasy startowej. |

---

## 6. Powiązania

- **[Regulamin](02-regulamin.md)** — obowiązkowa akceptacja po onboardingu (świeża instalacja / nowa wersja).
- **[Konto i logowanie](03-konto-i-logowanie.md)** — logowanie/rejestracja z poziomu onboardingu.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — status premium prezentowany na stronie konta.
