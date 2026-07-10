# 14 — Sklep i zakupy (Premium / Billing)

## Metryka

| | |
|---|---|
| **Moduł** | `billing`, `home` (`presentation/store`) |
| **Typ** | Warstwa monetyzacji |
| **Dostępność** | Wszyscy |
| **Powiązane featury** | [Ekran główny](04-ekran-glowny.md) · [Tryb główny](05-tryb-glowny.md) · [Swipe](07-tryb-swipe.md) · [Tłumaczeń](08-tryb-tlumaczen.md) · [CEM](09-tryb-cem.md) · [Reklamy](15-reklamy.md) |

---

## 1. Cel funkcji

Sklep i warstwa płatności odpowiadają za **odblokowywanie płatnych treści** i statusu **premium**. Zakupy są jednorazowe (dożywotni dostęp do danej treści) i realizowane przez sklep z aplikacjami. Status zakupów steruje dostępem do trybów/kategorii oraz obecnością reklam.

---

## 2. Katalog produktów

| Produkt | Co odblokowuje |
|---------|----------------|
| **Pełny pakiet** | Wszystkie płatne treści (tryby i kategorie) **oraz** brak reklam. |
| **Tryb Swipe** | Pełny [tryb Swipe](07-tryb-swipe.md). |
| **Tryb Tłumaczeń** | [Tryb Tłumaczeń](08-tryb-tlumaczen.md). |
| **Kategoria (trybu głównego)** | Pojedyncza płatna kategoria w [trybie głównym](05-tryb-glowny.md). |
| **Brak reklam (ad-free)** | Wyłączenie reklam bez kupowania pełnego pakietu. |

- Kategorie [trybu CEM](09-tryb-cem.md) odblokowuje **pełny pakiet** (brak zakupu pojedynczych kategorii CEM).
- **Pełny pakiet** jest nadrzędny — obejmuje wszystkie powyższe zakresy.

---

## 3. Miejsca zakupu

- **Sklep** (ekran) — pełny katalog: pełny pakiet, tryby, brak reklam, wyróżniona kategoria; z cenami i statusem posiadania.
- **Panele zakupu na ekranie głównym** — dla trybów Swipe / Tłumaczeń (kup / wypróbuj / rozpocznij).
- **Okno zakupu kategorii** — w [trybie głównym](05-tryb-glowny.md) przy wejściu w zablokowaną kategorię.
- **Panel zakończenia triala** — w [trybie Swipe](07-tryb-swipe.md).

---

## 4. Statusy zakupu

| Status | Znaczenie |
|--------|-----------|
| **Posiadany (owned)** | Treść odblokowana. |
| **Oczekujący (pending)** | Płatność w toku (np. wymaga zatwierdzenia) — dostęp jeszcze nieaktywny. |
| **Sukces** | Zakup zakończony — treść odblokowana, ekrany aktualizują dostęp. |
| **Anulowany** | Użytkownik przerwał zakup — brak zmian. |
| **Błąd** | Niepowodzenie — komunikat, brak odblokowania. |

---

## 5. Przepływ użytkownika (zakup)

1. Użytkownik wybiera produkt (w sklepie lub panelu zakupu).
2. Uruchamiany jest natywny proces płatności sklepu z aplikacjami.
3. Wynik:
   - **sukces** → treść odblokowana; zależne ekrany (sklep, ekran główny, kategorie) aktualizują status; w odpowiednich miejscach następuje przejście do odblokowanej treści,
   - **oczekujący** → dostęp aktywuje się po zatwierdzeniu płatności,
   - **anulowanie/błąd** → brak odblokowania, ewentualny komunikat.
4. Posiadane zakupy są **odświeżane** przy wejściu na ekrany zakupowe (przywracanie zakupów).

---

## 6. Reguły biznesowe

- **Dostęp = suma uprawnień:** treść jest dostępna, gdy użytkownik ma dany produkt **lub** pełny pakiet (**lub** treść jest darmowa).
- **Odblokowanie propaguje się** do wszystkich ekranów odczytujących status zakupów (bez restartu).
- **Reklamy zależą od zakupów:** pełny pakiet lub „brak reklam" wyłącza reklamy (patrz [Reklamy](15-reklamy.md)).
- **Stan oczekujący** jest wyraźnie rozróżniany od pełnego odblokowania.
- **Ceny** pobierane są ze sklepu z aplikacjami; do czasu pobrania mogą być nieznane.

---

## 7. Stany brzegowe i błędy

| Sytuacja | Oczekiwane zachowanie |
|----------|-----------------------|
| Brak sieci / brak połączenia z billingiem | Ceny mogą być niedostępne; zakup może się nie rozpocząć. |
| Brak szczegółów produktu | Zakup wstrzymany; próba ponownego pobrania cen. |
| Zakup anulowany | Brak zmian; stan „w trakcie zakupu" kończony. |
| Zakup oczekujący | Dostęp nieaktywny do zatwierdzenia; status oznaczony. |
| Ponowna instalacja / nowe urządzenie | Zakupy przywracane przy odświeżeniu (posiadane produkty odblokowują treści). |
| Posiadanie pełnego pakietu | Wszystkie płatne treści i brak reklam niezależnie od pojedynczych zakupów. |

---

## 8. Powiązania

- **Tryby płatne** — [Swipe](07-tryb-swipe.md), [Tłumaczeń](08-tryb-tlumaczen.md), płatne kategorie [trybu głównego](05-tryb-glowny.md) i [CEM](09-tryb-cem.md).
- **[Reklamy](15-reklamy.md)** — zależność reklam od statusu premium / „brak reklam".
- **[Ekran główny](04-ekran-glowny.md)** — panele zakupu i status premium.
