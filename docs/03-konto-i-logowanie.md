# 03 — Konto i logowanie

## Metryka

| | |
|---|---|
| **Moduł** | `signup` (ekrany), `auth` (uwierzytelnianie) |
| **Typ** | Warstwa wspólna (konto użytkownika) |
| **Dostępność** | Wszyscy; konto **opcjonalne** |
| **Powiązane featury** | [Onboarding](01-onboarding.md) · [Ustawienia konta](13-ustawienia-konta.md) · [Profil i statystyki](12-profil-i-statystyki.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) |

---

## 1. Cel funkcji

Obszar konta odpowiada za **uwierzytelnianie** użytkownika: rejestrację, logowanie, reset hasła oraz utrzymanie sesji. Konto pozwala powiązać użytkownika z jego danymi (nazwa, e-mail) i jest podstawą funkcji wymagających tożsamości (m.in. profil, ustawienia konta, powiązanie zakupów).

Zarządzanie już zalogowanym kontem (zmiana nazwy/hasła, usunięcie konta, wylogowanie) opisano w [Ustawienia konta](13-ustawienia-konta.md).

---

## 2. Dostęp i warunki

- Konto jest **opcjonalne** — aplikacji można używać bez logowania (patrz [Onboarding](01-onboarding.md)).
- Dostępne **metody uwierzytelniania**:
  - **E-mail + hasło** (rejestracja i logowanie),
  - **Logowanie przez Google**.
- Wejście do ekranów logowania/rejestracji możliwe m.in. z onboardingu oraz z obszaru konta w aplikacji.

---

## 3. Przepływy użytkownika

### 3.1 Rejestracja (e-mail + hasło)
1. Użytkownik podaje **nazwę**, **e-mail**, **hasło** i **powtórzenie hasła**.
2. Przycisk rejestracji jest aktywny tylko, gdy hasło nie jest puste i **oba hasła są identyczne** (walidacja po stronie aplikacji).
3. Po zatwierdzeniu trwa rejestracja (stan ładowania).
4. Powodzenie → użytkownik jest uwierzytelniony i wraca do miejsca, z którego rozpoczął logowanie.
5. Błąd → komunikat o błędzie (np. e-mail zajęty, nieprawidłowe dane).

### 3.2 Logowanie (e-mail + hasło)
1. Użytkownik podaje **e-mail** i **hasło**.
2. Po zatwierdzeniu trwa logowanie (stan ładowania).
3. Powodzenie → uwierzytelnienie i powrót.
4. Błąd → komunikat (np. błędne dane logowania).

### 3.3 Logowanie przez Google
1. Użytkownik wybiera logowanie przez Google.
2. Przechodzi natywny wybór konta Google.
3. Powodzenie → uwierzytelnienie i powrót; błąd/anulowanie → komunikat / brak zmiany.

### 3.4 Reset hasła
1. Użytkownik podaje **e-mail**.
2. Wysyłany jest link/instrukcja resetu hasła na podany adres.
3. Powodzenie → potwierdzenie wysłania; błąd → komunikat.
4. Zmiana hasła odbywa się poza aplikacją (zgodnie z otrzymaną wiadomością).

### 3.5 Wylogowanie i operacje na koncie
- Wylogowanie oraz zmiana nazwy/hasła i usunięcie konta są dostępne dla zalogowanego użytkownika — opis w [Ustawienia konta](13-ustawienia-konta.md).

---

## 4. Reguły biznesowe

- **Walidacja rejestracji (po stronie aplikacji):** hasło niepuste **oraz** zgodne z powtórzeniem — inaczej rejestracja niedostępna.
- **Stan uwierzytelnienia:** aplikacja rozpoznaje, czy użytkownik jest zalogowany, i udostępnia dane konta (nazwa, e-mail) tam, gdzie są potrzebne (np. onboarding, profil).
- **Metody logowania są równorzędne:** dla konta e-mail/hasło oraz Google efekt końcowy (uwierzytelniona sesja) jest taki sam.
- **Ponowne uwierzytelnienie (re-auth):** operacje wrażliwe na koncie (np. usunięcie konta, zmiana hasła) mogą wymagać ponownego potwierdzenia tożsamości — hasłem lub przez dostawcę (Google). Szczegóły: [Ustawienia konta](13-ustawienia-konta.md).
- **Reset hasła** działa w oparciu o wiadomość e-mail; aplikacja jedynie inicjuje wysyłkę.

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Hasła w rejestracji różne / puste | Rejestracja niedostępna (przycisk nieaktywny). |
| E-mail już zarejestrowany | Błąd rejestracji z komunikatem. |
| Błędne dane logowania | Komunikat o błędzie; brak sesji. |
| Anulowanie logowania Google | Brak zmiany stanu; ewentualny komunikat. |
| Reset hasła dla nieznanego / błędnego e-maila | Zachowanie zgodne z odpowiedzią backendu (komunikat błędu lub potwierdzenie wysłania). |
| Brak sieci | Operacja kończy się błędem z komunikatem; stan ładowania jest kończony. |
| Korzystanie bez konta | Dozwolone; funkcje wymagające tożsamości pozostają niedostępne do zalogowania. |

---

## 6. Powiązania

- **[Onboarding](01-onboarding.md)** — wejście do logowania/rejestracji i prezentacja stanu konta.
- **[Ustawienia konta](13-ustawienia-konta.md)** — wylogowanie, zmiana nazwy/hasła, usunięcie konta, ponowne uwierzytelnienie.
- **[Profil i statystyki](12-profil-i-statystyki.md)** — dane i wyniki powiązane z kontem.
- **[Sklep i zakupy](14-sklep-i-zakupy.md)** — powiązanie statusu premium/zakupów z użytkownikiem.
