# E2E — Regulamin (Terms of Service)

> Feature: [02-regulamin.md](../02-regulamin.md). Konwencja i statusy: [README](README.md).

### E2E-TOS-01 — akceptacja obowiązkowego regulaminu odblokowuje aplikację
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** świeża instalacja (nigdy nie zaakceptowano regulaminu); backend serwuje regulamin w wersji N.
- **When:** po onboardingu wyświetla się ekran regulaminu; użytkownik wybiera „Akceptuję".
- **Then:** zaakceptowana wersja N zapamiętana; użytkownik trafia na ekran główny.

### E2E-TOS-02 — nowa wersja regulaminu wymusza ponowną akceptację
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** użytkownik zaakceptował wcześniej wersję N; backend serwuje wersję N+1.
- **When:** użytkownik uruchamia aplikację.
- **Then:** ponownie pojawia się obowiązkowy ekran regulaminu; po akceptacji zapamiętana wersja to N+1.

### E2E-TOS-03 — użytkownik z historią nie jest blokowany przy braku sieci
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** użytkownik ma zaakceptowaną aktualną wersję; backend niedostępny.
- **When:** uruchamia aplikację.
- **Then:** brak blokady — użytkownik przechodzi na ekran główny (traktowany jako mający akceptację).

### E2E-TOS-04 — podgląd regulaminu bez wymogu akceptacji
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** regulamin w aktualnej wersji już zaakceptowany.
- **When:** użytkownik otwiera regulamin dobrowolnie (np. z onboardingu/ustawień).
- **Then:** widzi treść bez przymusu akceptacji; powrót nie zmienia zapamiętanej wersji.
