# 13 — Ustawienia konta

## Metryka

| | |
|---|---|
| **Moduł** | `home` (`presentation/user_settings`) |
| **Typ** | Ekran zarządzania kontem i preferencjami |
| **Dostępność** | Zalogowani (operacje na koncie); ustawienia powiadomień — wszyscy |
| **Powiązane featury** | [Konto i logowanie](03-konto-i-logowanie.md) · [Punktacja i seria](11-punktacja-i-seria.md) · [Powiadomienia](16-powiadomienia.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) |

---

## 1. Cel funkcji

Ustawienia konta pozwalają użytkownikowi zarządzać swoim kontem (zmiana nazwy, zmiana hasła, wylogowanie, usunięcie konta), swoim **postępem** (skasowanie postępu) oraz **preferencjami powiadomień** (włączenie/wyłączenie, godzina przypomnień).

---

## 2. Dostęp i warunki

- Operacje na koncie (zmiana nazwy/hasła, usunięcie konta) dotyczą **zalogowanego** użytkownika.
- Zmiana **hasła** dotyczy kont **e-mail + hasło**; dla kont **zewnętrznych** (Google) usunięcie konta wykorzystuje ponowne uwierzytelnienie przez dostawcę.
- Ekran rozpoznaje **typ konta** (metodę logowania) oraz stan **anonimowy** (brak danych konta).
- Ustawienia **powiadomień** są dostępne niezależnie od konta.

---

## 3. Przepływy użytkownika

### 3.1 Zmiana nazwy użytkownika
1. Użytkownik podaje nową nazwę.
2. Walidacja: nowa nazwa musi **różnić się** od obecnej.
3. Po powodzeniu nazwa jest aktualizowana i prezentowane jest potwierdzenie.

### 3.2 Zmiana hasła
1. Użytkownik podaje **stare hasło**, **nowe hasło** i jego **powtórzenie**.
2. Walidacja: nowe hasło = powtórzenie, długość **≥ 6** znaków, nowe **różne** od starego.
3. Po powodzeniu hasło jest zmieniane i prezentowane jest potwierdzenie.

### 3.3 Usunięcie konta
1. Użytkownik potwierdza chęć usunięcia konta.
2. Wymagane jest **ponowne uwierzytelnienie**:
   - konto e-mail/hasło → podanie **hasła**,
   - konto Google → potwierdzenie przez **dostawcę**.
3. Po powodzeniu konto i powiązane dane są usuwane, a użytkownik zostaje wyprowadzony z ekranu (wylogowany).

### 3.4 Skasowanie postępu
1. Użytkownik potwierdza skasowanie postępu.
2. Dane progresji (punkty, seria, historia pytań) zostają **wyzerowane** (patrz [Punktacja i seria](11-punktacja-i-seria.md)).
3. Prezentowane jest potwierdzenie. Konto pozostaje aktywne.

### 3.5 Wylogowanie
- Użytkownik wylogowuje się; dane progresji są synchronizowane i czyszczone lokalnie.

### 3.6 Ustawienia powiadomień
- **Włącz/wyłącz powiadomienia** — włączenie planuje przypomnienia i zapewnia subskrypcję treści; wyłączenie anuluje przypomnienia.
- **Godzina przypomnień** — użytkownik ustawia porę przypomnień (wybór godziny/minuty).
- Szczegóły logiki: [Powiadomienia](16-powiadomienia.md).

---

## 4. Reguły biznesowe

- **Walidacja nazwy:** nowa nazwa musi być inna niż obecna.
- **Walidacja hasła:** zgodność nowego z powtórzeniem, minimalna długość 6 znaków, różność od starego.
- **Usunięcie konta wymaga re-auth:** zawsze poprzedzone potwierdzeniem tożsamości (hasło lub dostawca).
- **Skasowanie postępu ≠ usunięcie konta:** kasowanie postępu zeruje dane progresji, ale konto zostaje.
- **Powiadomienia:** przełącznik i godzina są zapamiętywane; zmiana godziny przy włączonych powiadomieniach przeplanowuje przypomnienia.
- **Konto anonimowe / niezalogowane:** operacje na koncie nie mają zastosowania; dostępne pozostają ustawienia niewymagające konta.

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Nowa nazwa taka sama jak obecna | Komunikat walidacyjny; brak zmiany. |
| Nowe hasło za krótkie / niezgodne / takie samo jak stare | Komunikat walidacyjny; brak zmiany. |
| Błędne stare hasło / nieudane re-auth | Błąd operacji; konto bez zmian. |
| Usunięcie konta — sukces | Dane usunięte, użytkownik wyprowadzony z ekranu. |
| Skasowanie postępu — sukces | Progresja wyzerowana; konto aktywne. |
| Brak sieci | Operacja kończy się błędem; stan bez zmian. |
| Wyłączenie powiadomień | Przypomnienia anulowane. |

---

## 6. Powiązania

- **[Konto i logowanie](03-konto-i-logowanie.md)** — metody uwierzytelniania i re-auth.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — skutki skasowania postępu / usunięcia konta.
- **[Powiadomienia](16-powiadomienia.md)** — przełącznik i godzina przypomnień.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — status premium widoczny w ustawieniach.
