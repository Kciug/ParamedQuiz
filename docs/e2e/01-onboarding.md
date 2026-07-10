# E2E — Onboarding

> Feature: [01-onboarding.md](../01-onboarding.md). Konwencja i statusy: [README](README.md).

### E2E-ONBOARDING-01 — świeży start prowadzi przez onboarding do regulaminu i ekranu głównego
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** świeża instalacja (brak ukończonego onboardingu, brak zaakceptowanego regulaminu).
- **When:** użytkownik uruchamia aplikację; na ekranie powitalnym rozpoczyna onboarding; przechodzi kolejne strony przyciskiem „Dalej"; na ostatniej wybiera „Zakończ".
- **Then:** onboarding oznaczony jako ukończony; pojawia się obowiązkowy ekran regulaminu; po akceptacji użytkownik trafia na ekran główny.

### E2E-ONBOARDING-02 — onboarding pokazywany tylko raz
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** onboarding już wcześniej ukończony, regulamin zaakceptowany.
- **When:** użytkownik ponownie uruchamia aplikację.
- **Then:** onboarding jest pomijany; aplikacja od razu rozwiązuje trasę startową (ekran główny).

### E2E-ONBOARDING-03 — strona konta odzwierciedla stan zalogowania
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** świeży start; użytkownik loguje się w trakcie onboardingu.
- **When:** wraca na stronę konta w sekwencji onboardingu.
- **Then:** strona konta pokazuje nazwę i e-mail zalogowanego użytkownika oraz potwierdzenie zalogowania (a nie przycisk logowania).

### E2E-ONBOARDING-04 — niezalogowany może przejść dalej mimo ukrytego „Pomiń"
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** świeży start; użytkownik niezalogowany, na stronie konta w sekwencji.
- **When:** próbuje przejść dalej.
- **Then:** przycisk „Pomiń" jest ukryty, ale „Dalej" pozwala kontynuować; konto pozostaje opcjonalne.
