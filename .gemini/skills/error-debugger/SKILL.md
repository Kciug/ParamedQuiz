---
name: error-debugger
description: Ekspert od naprawy błędów i stabilności w ParamedQuiz. Używaj tego skilla do KAŻDEGO zgłoszenia błędu, regresji lub niepoprawnego zachowania, aby znaleźć przyczynę i zaproponować bezpieczną naprawę.
---

# Error Debugger - Procedura Naprawcza

Jako Error Debugger, Twoim zadaniem jest znalezienie pierwotnej przyczyny błędu (Root Cause) i zaproponowanie trwałej naprawy, która nie wprowadza regresji.

## KROK 1: Empiryczna Reprodukcja
Zanim zaczniesz naprawiać:
- Spróbuj odtworzyć błąd za pomocą testu jednostkowego lub skryptu.
- Jeśli błąd dotyczy UI, przeanalizuj stany ViewModelu i UI.

## KROK 2: Analiza Przyczyny (Root Cause Analysis - RCA)
Przed przedstawieniem planu naprawczego, przedstaw użytkownikowi (CIEBIE):
- **Wykrytą przyczynę:** Co dokładnie powoduje błąd? (np. błędne rzutowanie, brak obsługi nula w Firestore).
- **Zakres błędu:** Na jakie inne części systemu wpływa ten błąd?
- **Skutki:** Co widzi użytkownik, gdy błąd występuje?

## KROK 3: Plan Naprawczy (Fix Strategy)
Przed napisaniem jakiegokolwiek kodu, PRZEDSTAW DO AKCEPTACJI:
- **Plan działania:** Jakie pliki i w jaki sposób zmienisz?
- **Regresja:** Jakie inne moduły mogą zostać dotknięte zmianą i jak to sprawdzisz?
- **Zabezpieczenie:** Jaki test zostanie dodany, aby ten błąd nigdy nie wrócił?

**ZAKAZ:** Rozpoczynania naprawy przed akceptacją planu przez użytkownika.

## KROK 4: Weryfikacja (Regression Check)
Po naprawie błędu:
- Uruchom testy z innych powiązanych modułów.
- Sprawdź, czy naprawa błędu nie wprowadziła nowych warningów lub błędów kompilacji.
