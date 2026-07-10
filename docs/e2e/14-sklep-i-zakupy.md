# E2E — Sklep i zakupy

> Feature: [14-sklep-i-zakupy.md](../14-sklep-i-zakupy.md). Konwencja i statusy: [README](README.md).
> Wszystkie zakupy przez **fake billing** (`MockBillingRepository`) — bez realnej płatności.

### E2E-STORE-01 — zakup pełnego pakietu odblokowuje treści i wyłącza reklamy
- **Priorytet:** P0
- **Status:** ☐ manualny
- **Given:** użytkownik bez żadnych zakupów; reklamy aktywne.
- **When:** w sklepie kupuje pełny pakiet (fake billing, sukces).
- **Then:** status premium aktywny; płatne tryby/kategorie odblokowane; reklamy przestają się pojawiać.

### E2E-STORE-02 — zakup pojedynczego trybu (Swipe) odblokowuje tylko jego
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** użytkownik bez zakupów.
- **When:** kupuje tryb Swipe.
- **Then:** Swipe odblokowany; tłumaczenia i płatne kategorie pozostają zablokowane; reklamy nadal aktywne (brak ad-free).

### E2E-STORE-03 — produkt „brak reklam" wyłącza reklamy bez pełnego pakietu
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** użytkownik bez zakupów.
- **When:** kupuje produkt „brak reklam".
- **Then:** reklamy wyłączone; płatne treści pozostają zablokowane.

### E2E-STORE-04 — status odblokowania propaguje się do ekranów
- **Priorytet:** P1
- **Status:** ☐ manualny
- **Given:** zakup trybu/kategorii zakończony sukcesem.
- **When:** użytkownik wraca na ekran główny / listę kategorii.
- **Then:** status dostępu zaktualizowany bez restartu aplikacji.

### E2E-STORE-05 — anulowanie zakupu nie zmienia stanu
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** ekran sklepu.
- **When:** użytkownik inicjuje zakup i go anuluje.
- **Then:** brak odblokowania; stan „w trakcie zakupu" zakończony; brak komunikatu błędu.

### E2E-STORE-06 — błąd płatności pokazuje komunikat
- **Priorytet:** P2
- **Status:** ☐ manualny
- **Given:** fake billing skonfigurowany na błąd.
- **When:** użytkownik próbuje kupić produkt.
- **Then:** komunikat o błędzie; brak odblokowania.
