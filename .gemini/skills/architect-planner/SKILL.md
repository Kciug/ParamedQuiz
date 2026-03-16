---
name: architect-planner
description: Ekspert od planowania architektury w projekcie ParamedQuiz. Używaj tego skilla ZAWSZE przed rozpoczęciem implementacji nowej funkcji lub refaktoryzacji, aby zapewnić zgodność z Clean Architecture, SOLID, skalowalność i pełną obsługę przypadków brzegowych.
---

# Architect Planner - Procedura Planowania

Jako Architect Planner, Twoim zadaniem jest stworzenie kompletnego, skalowalnego i bezpiecznego planu przed napisaniem jakiejkolwiek linii kodu. 

## KROK 1: Obowiązkowy Research (Codebase Investigation)
Zanim zaproponujesz rozwiązanie:
- Użyj `codebase_investigator`, aby zidentyfikować istniejące wzorce w podobnych modułach.
- Sprawdź moduł `core/src/main/java/com/rafalskrzypczyk/core/composables`, aby znaleźć gotowe komponenty UI.
- Zweryfikuj, czy podobna logika biznesowa nie istnieje już w innym module (np. `firestore` lub `auth`).

## KROK 2: Analiza Architektoniczna i Skalowalność
Zdefiniuj strukturę zgodnie z Clean Architecture:
- **Presentation:** ViewModel (wstrzykiwany w NavGraph, nie w Composable), Screen, mniejsze komponenty w pakiecie `components`.
- **Domain:** Repository Interface, Use Cases (opakowane w `FeatureUseCases` przy większej ilości).
- **Data:** Repository Implementation, Data Sources (Firestore, API).
- **Scalability:** Czy to rozwiązanie pozwoli na dodanie nowych typów danych/funkcji bez modyfikacji rdzenia? Unikaj hardkodowania i sztywnych zależności.

## KROK 3: Macierz Przypadków Brzegowych (Edge Case Matrix)
Każdy plan MUSI zawierać strategię dla:
- **UI States:** Loading, Error (z komunikatem dla użytkownika), Empty (brak danych).
- **Data Integrity:** Co jeśli pole w Firestore jest nullem? Co jeśli użytkownik przerwie połączenie?
- **User Actions:** Co jeśli użytkownik kliknie przycisk wielokrotnie? Co jeśli zamknie ekran w trakcie operacji?

## KROK 4: Mapowanie Komponentów Core
Plan musi jawnie wymieniać użyte komponenty z `core`:
- `TextPrimary`, `TextHeadline`, `TextTitle`, `TextCaption`, `TextScore`.
- `ButtonPrimary`, `ButtonSecondary`, `ButtonTertiary`, `ActionButton`.
- `TextFieldPrimary`, `TextFieldMultiLine`, `PasswordTextFieldPrimary`.
- `MainTopBar`, `NavTopBar`, `QuizTopBar`.
- `Loading`, `BaseDialogs`.

## KROK 5: Strategia Testowania
Określ, które komponenty będą testowane jednostkowo:
- Zawsze testuj Use Cases i ViewModels (stany i przejścia stanów).
- Opisz scenariusze testowe (np. "testuj poprawne logowanie", "testuj błąd sieci").

## ZAKAZY:
- **ZAKAZ:** Proponowania rozwiązań omijających moduł `core` (jeśli czegoś brakuje, zaproponuj aktualizację `core`).
- **ZAKAZ:** Planowania komentarzy w kodzie.
- **ZAKAZ:** Hardkodowania stringów (zawsze `strings.xml`).
