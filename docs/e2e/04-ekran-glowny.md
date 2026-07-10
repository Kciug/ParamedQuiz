# E2E — Ekran główny (Home)

> Feature: [04-ekran-glowny.md](../04-ekran-glowny.md). Konwencja i statusy: [README](README.md).

### E2E-HOME-01 — ekran główny prezentuje status i wejścia do trybów
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** użytkownik po onboardingu i akceptacji regulaminu; posiada jakiś wynik i serię.
- **When:** wchodzi na ekran główny.
- **Then:** widoczne są punkty i seria na górnym pasku, menu trybów (główny, Swipe, tłumaczeń, CEM) oraz dodatki (ćwiczenie dnia, powtórki, sklep).

### E2E-HOME-02 — nawigacja do trybu i powrót
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** ekran główny.
- **When:** użytkownik wybiera tryb główny, a następnie wraca.
- **Then:** przejście do ekranu kategorii trybu głównego i poprawny powrót na ekran główny.

### E2E-HOME-03 — odrzucenie news bannera
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** backend serwuje aktywny news banner.
- **When:** użytkownik odrzuca baner.
- **Then:** baner znika i nie wraca po odświeżeniu ekranu.

### E2E-HOME-04 — wejście w profil przez awatar
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** ekran główny (użytkownik zalogowany).
- **When:** użytkownik dotyka awatara/panelu użytkownika.
- **Then:** otwiera się profil użytkownika.
