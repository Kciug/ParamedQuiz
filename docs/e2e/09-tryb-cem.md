# E2E — Tryb CEM

> Feature: [09-tryb-cem.md](../09-tryb-cem.md) · mechanika: [00-mechanika-quizu.md](../00-mechanika-quizu.md). Konwencja i statusy: [README](README.md).

### E2E-CEM-01 — nawigacja po podkategoriach i rozegranie quizu
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** darmowa kategoria CEM zawierająca podkategorie z pytaniami (seed).
- **When:** użytkownik wchodzi w kategorię najwyższego poziomu, następnie w podkategorię z pytaniami; rozwiązuje quiz do końca.
- **Then:** poziomy list nawigują poprawnie; po ostatnim pytaniu pojawia się ekran wyniku.

### E2E-CEM-02 — kategoria z podkategoriami nie startuje quizu
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** kategoria zawierająca podkategorie.
- **When:** użytkownik ją wybiera.
- **Then:** prezentowany jest kolejny poziom listy (podkategorie), a nie rozpoczęcie quizu.

### E2E-CEM-03 — dostęp do płatnej kategorii tylko z pełnym pakietem
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** płatna kategoria CEM; użytkownik bez pełnego pakietu.
- **When:** próbuje ją otworzyć.
- **Then:** kategoria prezentowana jako płatna/zablokowana; brak rozpoczęcia bez dostępu.

### E2E-CEM-04 — błąd ładowania z możliwością ponowienia
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** backend zwraca błąd dla kategorii CEM.
- **When:** użytkownik wchodzi w tryb CEM.
- **Then:** ekran pokazuje stan błędu z możliwością ponowienia.
