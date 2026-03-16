# Mapa Modułów ParamedQuiz

Ten dokument opisuje przeznaczenie każdego modułu w projekcie. Używaj go jako przewodnika przy planowaniu zmian.

## Rdzeń i Infrastruktura
- `core`: Współdzielone komponenty UI, theme, narzędzia, DI, bazowe klasy.
- `app`: Punkt wejściowy aplikacji, główny NavHost, konfiguracja Hilt.
- `firestore`: Integracja z Firebase Firestore, repozytoria danych quizowych.
- `auth`: Logika autoryzacji, ekrany logowania.
- `billing`: Obsługa płatności i subskrypcji.
- `ads`: Integracja z systemem reklam.
- `buildSrc`: Skrypty builda i zarządzanie wersjami.

## Funkcje Quizowe (Tryby)
- `main_mode`: Klasyczny tryb quizu (4 odpowiedzi).
- `swipe_mode`: Tryb quizu oparty na gestach swipe (Prawda/Fałsz).
- `translation_mode`: Tryb nauki słownictwa (tłumaczenia).
- `cem_mode`: Tryb egzaminacyjny (CEM - Centrum Egzaminów Medycznych).

## Pozostałe Funkcje
- `home`: Ekran główny aplikacji, menu wyboru trybów.
- `score`: Statystyki, tabele wyników, podsumowania.
- `signup`: Rejestracja nowych użytkowników.

**PAMIĘTAJ:** Zawsze sprawdzaj, czy zmiana, którą planujesz, trafia do właściwego modułu funkcjonalnego, a nie jest upychana w `app` lub `core`.
