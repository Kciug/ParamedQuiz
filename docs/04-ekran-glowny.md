# 04 — Ekran główny (Home)

## Metryka

| | |
|---|---|
| **Moduł** | `home` (`presentation/home_page`) |
| **Typ** | Ekran centralny (hub nawigacyjny) |
| **Dostępność** | Wszyscy użytkownicy (po onboardingu / akceptacji regulaminu) |
| **Powiązane featury** | [Tryby gry](00-mechanika-quizu.md) · [Ćwiczenie dnia](06-cwiczenie-dnia.md) · [Powtórki](10-tryb-powtorek.md) · [Sklep i zakupy](14-sklep-i-zakupy.md) · [Punktacja i seria](11-punktacja-i-seria.md) · [Profil i statystyki](12-profil-i-statystyki.md) · [Ocena aplikacji](17-ocena-aplikacji.md) · [Powiadomienia](16-powiadomienia.md) |

---

## 1. Cel funkcji

Ekran główny to centralny punkt aplikacji po jej uruchomieniu. Prezentuje status użytkownika (punkty, seria, konto/premium), umożliwia wejście do wszystkich **trybów gry** oraz **dodatków** (ćwiczenie dnia, powtórki, sklep), pokazuje **nowości** (news banners) i jest miejscem obsługi próśb o **ocenę aplikacji** oraz **zgody na powiadomienia**.

---

## 2. Elementy ekranu

### 2.1 Górny pasek (status)
- **Punkty** użytkownika i **seria** (streak) — z oznaczeniem, gdy seria jest „w toku" / do podtrzymania na dziś (stan PENDING).
- **Stan konta** — zalogowany/niezalogowany oraz oznaczenie **premium**.
- **Awatar** — wejście do [profilu użytkownika](12-profil-i-statystyki.md).

### 2.2 Karta powitalna
- Powitanie z nazwą użytkownika (jeśli dostępna).

### 2.3 Nowości (news banners)
- Lista aktywnych banerów z tytułem/treścią/grafiką.
- Baner można **odrzucić** (zniknie i zostanie oznaczony jako zobaczony).

### 2.4 Menu trybów gry
Wejścia do czterech głównych trybów:
- [Tryb główny](05-tryb-glowny.md),
- [Tryb Swipe](07-tryb-swipe.md),
- [Tryb Tłumaczeń](08-tryb-tlumaczen.md),
- [Tryb CEM](09-tryb-cem.md).

### 2.5 Dodatki (addons)
Pozioma lista skrótów:
- **Ćwiczenie dnia** — wyróżnione, gdy dostępne na dziś; przy braku dostępności próba wejścia pokazuje komunikat „już wykonane". Patrz [Ćwiczenie dnia](06-cwiczenie-dnia.md).
- **Powtórki** — wejście do [trybu powtórek](10-tryb-powtorek.md).
- **Sklep** — wejście do [sklepu](14-sklep-i-zakupy.md).

### 2.6 Prośba o ocenę aplikacji (in-flow)
- Gdy użytkownik kwalifikuje się, na ekranie pojawia się karta oceny. Szczegóły przepływu: [Ocena aplikacji](17-ocena-aplikacji.md).

### 2.7 Prośba o zgodę na powiadomienia (priming)
- Gdy użytkownik kwalifikuje się, pojawia się prośba o włączenie powiadomień. Szczegóły: [Powiadomienia](16-powiadomienia.md).

---

## 3. Przepływy użytkownika

### 3.1 Wejście do trybu darmowego / odblokowanego
1. Użytkownik wybiera tryb z menu.
2. Aplikacja przechodzi do wybranego trybu (ew. z onboardingiem trybu przy pierwszym wejściu).

### 3.2 Wejście do trybu płatnego (Swipe / Tłumaczeń)
- Tryby płatne (Swipe, Tłumaczeń) mają na ekranie głównym powiązany **panel zakupu** (bottom sheet), z którego użytkownik może:
  - **kupić** tryb,
  - **wypróbować** wersję próbną (dotyczy Swipe),
  - **rozpocząć** tryb, jeśli jest już odblokowany.
- Stany zakupu (odblokowany / oczekujący / cena) są odzwierciedlane w panelu. Szczegóły: [Sklep i zakupy](14-sklep-i-zakupy.md).

### 3.3 Odświeżanie danych
- Po wejściu na ekran główny dane są odświeżane: wynik/seria, dostępność ćwiczenia dnia, stan konta, statusy zakupów, liczby pytań w trybach, nowości.
- Przy każdym powrocie na ekran ponownie sprawdzana jest kwalifikacja do prośby o zgodę na powiadomienia.

---

## 4. Reguły biznesowe

- **Dostępność ćwiczenia dnia** wyznaczana jest na podstawie daty ostatniego wykonania (patrz [Ćwiczenie dnia](06-cwiczenie-dnia.md)); element jest wyróżniony tylko gdy dostępne.
- **Odblokowanie trybów płatnych:** tryb jest odblokowany, gdy użytkownik posiada dany tryb **lub** pełny pakiet.
- **Stan „oczekujący" (pending)** zakupu jest rozróżniany od pełnego odblokowania — dostęp aktywuje się dopiero po zatwierdzeniu płatności.
- **Nowości:** wyświetlane są aktywne banery; odrzucenie jest zapamiętywane (baner nie wraca).
- **Seria (PENDING):** górny pasek sygnalizuje, że seria wymaga dziś podtrzymania.
- **Prośba o ocenę i prośba o powiadomienia** to niezależne mechanizmy — mogą wystąpić równolegle (jedna jako karta w treści, druga jako prompt).

---

## 5. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Użytkownik niezalogowany | Ekran działa; górny pasek pokazuje stan „niezalogowany"; funkcje zależne od konta pozostają ograniczone. |
| Brak sieci | Dane mogą się nie odświeżyć (wynik/nowości/ceny); ekran pozostaje użyteczny z ostatnimi znanymi danymi. |
| Próba wejścia w ćwiczenie dnia po jego wykonaniu | Komunikat „już wykonane" zamiast rozpoczęcia. |
| Zakup anulowany / błąd płatności | Panel pozostaje; komunikat błędu; brak odblokowania. |
| Brak aktywnych nowości | Sekcja nowości ukryta. |
| Powrót na ekran po zmianach (np. po quizie) | Ponowne odświeżenie danych i ponowna ocena kwalifikacji do promptów. |

---

## 6. Powiązania

- **[Wspólna mechanika quizu](00-mechanika-quizu.md)** — wejście do trybów.
- **[Ćwiczenie dnia](06-cwiczenie-dnia.md)** — dostępność i wejście z dodatków.
- **[Powtórki](10-tryb-powtorek.md)** · **[Sklep i zakupy](14-sklep-i-zakupy.md)** — dodatki.
- **[Punktacja i seria](11-punktacja-i-seria.md)** — dane na górnym pasku.
- **[Profil i statystyki](12-profil-i-statystyki.md)** — wejście przez awatar.
- **[Ocena aplikacji](17-ocena-aplikacji.md)** · **[Powiadomienia](16-powiadomienia.md)** — prompty in-flow.
