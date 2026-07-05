# Tryb Powtórek

## Cel i koncepcja

Moduł pozwalający użytkownikom na utrwalanie wiedzy poprzez powtarzanie pytań, na które już wcześniej odpowiedzieli. Główną mechaniką jest pętla: błędnie odpowiedziane pytania w ramach sesji wracają na koniec kolejki, aż użytkownik odpowie na nie poprawnie.

Warstwa prezentacyjna może być recyklingowana z głównego trybu quizu

## Opis funkcjonalny

### 1. Ekran startowy powtórek (Konfiguracja sesji)

Zanim rozpocznie się quiz, użytkownik wybiera parametry sesji.

1. **Wybór powtarzanego trybu**

    * Wybór dowolnego trybu **poza Swipe**, ze względu na jego naturę szybkich pytań tak/nie.

    * Jeśli tryb zawiera pule lub kategorie umożliwiamy wybór puli.

    * Dla trybów które nie posiadają wewnętrznych puli, jeśli użytkownik nie udzielił dla danego trybu przynajmniej 10 odpowiedzi, jest on wyszarzony oraz posiada adnotację informującą użytkownika że musi pierw udzielić więcej odpowiedzi.

2. **Wybór puli**

    * Konkretna kategoria LUB opcja "Mix" (wszystkie kategorie).

    * System bierze pod uwagę wyłącznie kategorie, na które użytkownik udzielił już przynajmniej 10 odpowiedzi. Pozostałe kategorie są wyszarzone oraz posiadają adnotację informującą użytkownika że musi pierw udzielić więcej odpowiedzi.

3. **Wybór kryterium (sortowanie/filtrowanie)**

    * "Najgorsze" – pytania posortowane rosnąco od najniższego % poprawnych odpowiedzi.

    * "Najlepsze" – pytania posortowane malejąco od najwyższego % poprawnych odpowiedzi.

    * "Poniżej 50% trafności" – odfiltrowana lista pytań, gdzie współczynnik wynosi < 50%.

4. **Ilość pytań do powtórki**

    * Dostępne warianty bazowe: 10, 20, 50, 100, Wszystkie (z danej puli i kryterium)

    * **Logika wyświetlania***:* Widoczne są tylko opcje równe lub mniejsze niż faktyczna liczba dostępnych pytań.

*Przykład: jeśli w kategorii jest tylko 18 odpowiedzianych pytań, interfejs ukrywa/usuwa opcje "20" i "50". Użytkownik widzi tylko "10" i "Wszystkie" (czyli w tym wypadku 18).*

***

### 2. Puste stany

Jeśli w wybranej puli (lub w całej aplikacji dla nowego użytkownika) brakuje pytań spełniających kryteria, ekran konfiguracji jest blokowany.

* **UI:** Dedykowana ikona, krótki tekst (np. "Nie masz jeszcze materiału do powtórek. Rozwiąż najpierw quiz, abyśmy mieli z czego ułożyć pytania.").

***

### 3. Mechanika Pętli (Core Gameplay)

Po wystartowaniu sesji silnik ładuje wybraną pulę (np. 10 pytań).

* **Prawidłowa odpowiedź:** Pytanie zostaje uznane za zaliczone i trwale wypada z kolejki w tej sesji. Następuje przejście do kolejnego pytania.

* **Błędna odpowiedź (przed osiągnięciem limitu):** Pytanie zostaje rzucone na sam koniec aktualnej kolejki. Użytkownik będzie musiał zmierzyć się z nim ponownie po przejściu pozostałych pytań.

* **Limit błędów:** Maksymalną liczbę błędnych odpowiedzi na to samo pytanie w ramach jednej sesji wynosi 3 błędy. Jeśli użytkownik wyczerpie limit, pytanie trwale wypada z kolejki. Aplikacja zatrzymuje wtedy pętlę, wyraźnie wyświetla poprawną odpowiedź i wymaga ręcznego potwierdzenia (np. przycisk "Zrozumiałem" / "Dalej"), zanim przejdzie do kolejnego pytania.

* **Koniec sesji:** Następuje w momencie wyczyszczenia całej kolejki - użytkownik odpowiedział poprawnie **lub&#x20;**&#x70;ytanie wypadło z pętli po osiągnięciu limitu błędów.

***

### 4. Zapisywanie statystyk i zarządzanie stanem

* **Zapisywanie postępów:** W trybie powtórek **tylko pierwsza interakcja** z konkretnym pytaniem w ramach danej sesji aktualizuje globalne statystyki użytkownika (licznik wyświetleń i poprawnych/błędnych odpowiedzi). Kolejne próby odpowiedzi na to samo pytanie wymuszone przez "pętlę" w tej samej sesji są ignorowane w globalnych wynikach.

* **Utrata sesji:** Stan kolejki w trakcie trwania powtórki istnieje tylko w pamięci podręcznej. Zamknięcie aplikacji lub ręczne wyjście z trybu przerywa sesję bezpowrotnie. Przy kolejnym wejściu użytkownik musi skonfigurować nową sesję powtórek od nowa.

* **Punkty:&#x20;**&#x50;oprawne odpowiedzi na pytania powtórkowe gwarantuje przyznanie punktów, tak samo jak kolejna poprawna odpowiedź na dane pytanie w standardowym trybie.

* **Streak:** Ukończenie sesji powtórkowej jest traktowane systemowo dokładnie tak samo jak ukończenie standardowego quizu i przedłuża dzienny streak użytkownika.

***

### 5. Interfejs quizów

Tryb powtórek używa tych samych interfejsów quizów, co ich podstawowe odpowiedniki. Logika UI dla danego trybu pozostaje w większości niezmieniona.

Zmiany:

* W trakcie poprawiania błędów (indeks pytania > rozmiar początkowej puli): tekst "Popraw błędy!".

***

### 6. Ekrany podsumowania

Tryb powtórek używa jednego ekranu podsumowania, spójnego z podstawowym odpowiednikiem aplikacji.

Ekran przedstawia:

* Podstawowe dane o sesji

* Komunikat o wyczyszczeniu kolejki

* Listę rozegranych pytań

    * Listy dziedziczone z podstawowych odpowiedników dla danego trybu.

    * **Dodatkowe oflagowanie:** Pytania, które wypadły z pętli z powodu przekroczenia limitu błędów, muszą być wyraźnie zaznaczone (np. inna ikona/kolor obramowania), aby użytkownik wiedział, z czym ma największy problem.
