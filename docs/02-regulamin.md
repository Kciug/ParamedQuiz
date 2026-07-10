# 02 — Regulamin (Terms of Service)

## Metryka

| | |
|---|---|
| **Moduł** | `home` (`presentation/terms_of_service`), `firestore` (status/treść), `app` (routing) |
| **Typ** | Zgoda / bramka dostępu |
| **Dostępność** | Wszyscy użytkownicy |
| **Powiązane featury** | [Onboarding](01-onboarding.md) · [Konfiguracja zdalna](19-konfiguracja-zdalna.md) |

---

## 1. Cel funkcji

Regulamin (Terms of Service) to ekran prezentujący warunki korzystania z aplikacji, których **akceptacja jest wymagana** do dalszego korzystania. Treść i **wersja** regulaminu pochodzą z backendu, dzięki czemu można wydać nową wersję dokumentu i wymusić ponowną akceptację **bez aktualizacji aplikacji**.

---

## 2. Dostęp i warunki

Regulamin pojawia się jako **obowiązkowa bramka** w dwóch przypadkach:
- **Świeża instalacja** — użytkownik nigdy wcześniej nie zaakceptował żadnej wersji regulaminu.
- **Nowa wersja** — backend udostępnił wersję regulaminu **nowszą** niż ostatnio zaakceptowana przez użytkownika.

W pozostałych przypadkach (aktualna wersja już zaakceptowana) regulamin nie blokuje dostępu — użytkownik trafia od razu do ekranu głównego.

Regulamin można też otworzyć **dobrowolnie** (np. z ekranu powitalnego onboardingu / ustawień) w trybie „tylko do odczytu", bez wymuszania akceptacji.

---

## 3. Przepływ użytkownika

### 3.1 Akceptacja obowiązkowa (happy path)
1. Po onboardingu (lub przy starcie aplikacji) system stwierdza, że wymagana jest akceptacja.
2. Wyświetlany jest ekran regulaminu z pobraną treścią.
3. Użytkownik zapoznaje się z treścią i wybiera **Akceptuję**.
4. Zaakceptowana **wersja** jest zapamiętywana lokalnie.
5. Użytkownik przechodzi do ekranu głównego.

### 3.2 Wymuszenie po aktualizacji wersji
1. Backend publikuje nowszą wersję regulaminu.
2. Przy kolejnym starcie aplikacja wykrywa, że wersja zdalna jest wyższa od zaakceptowanej.
3. Ekran regulaminu pojawia się ponownie jako obowiązkowy; obowiązuje ten sam przepływ akceptacji.

### 3.3 Podgląd dobrowolny
- Użytkownik otwiera regulamin z własnej inicjatywy; widzi treść bez wymogu akceptacji i wraca do poprzedniego ekranu.

---

## 4. Reguły biznesowe

- **Wersjonowanie:** decyzja o wymogu akceptacji opiera się na porównaniu **wersji** — akceptacja wymagana, gdy `wersja zdalna > wersja zaakceptowana`.
- **Świeża instalacja:** brak jakiejkolwiek wcześniejszej akceptacji traktowany jest jako stan wymagający akceptacji **zawsze** i **niezależnie od dostępności backendu** (nie czeka na sieć — regulamin pokazywany od razu).
- **Zapamiętywanie:** po akceptacji zapisywana jest zaakceptowana wersja (lokalnie); służy do porównań przy kolejnych startach.
- **Odporność na brak sieci (użytkownik z historią):** jeśli użytkownik już kiedyś zaakceptował regulamin, a backend jest niedostępny lub odpowiada z opóźnieniem, **nie jest blokowany** — traktowany jest jak mający aktualną akceptację i przechodzi do aplikacji (obowiązuje krótki limit czasu oczekiwania na odpowiedź backendu).
- **Wstępne pobranie:** treść regulaminu jest wstępnie wczytywana w tle podczas onboardingu, by ekran renderował się natychmiast.

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Świeża instalacja, brak sieci | Regulamin i tak wymagany i pokazany (bez czekania na backend). |
| Użytkownik z historią, brak sieci / timeout | Nie jest blokowany — przechodzi do ekranu głównego (traktowany jako mający akceptację). |
| Błąd pobrania treści na ekranie regulaminu | Ekran sygnalizuje stan błędu zamiast treści; akceptacja niemożliwa do czasu wczytania treści. |
| Nowa wersja regulaminu po stronie backendu | Wymuszona ponowna akceptacja przy najbliższym starcie. |
| Otwarcie w trybie podglądu (dobrowolnie) | Treść widoczna bez wymogu akceptacji; brak wpływu na zapamiętaną wersję. |

---

## 6. Powiązania

- **[Onboarding](01-onboarding.md)** — regulamin rozstrzygany zaraz po ukończeniu onboardingu; treść wstępnie pobierana w jego trakcie.
- **[Konfiguracja zdalna](19-konfiguracja-zdalna.md)** — treść i wersja regulaminu zarządzane po stronie backendu.
