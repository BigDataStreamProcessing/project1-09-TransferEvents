# Charakterystyka danych

Dane mają charakter przelewów bankowych.

W strumieniu pojawiają się zdarzenia zgodne ze schematem `TransferEvent`.

```
create json schema TransferEvent(category string, status string, sender string, recipient string, amount int, ets string, its string);
```

Każde zdarzenie związane z jest z wykonaniem pojedynczego przelewu przez określoną osobę.

Każde zdarzenie zawiera następujące informacje: czas przelewu, odbiorcę, nadawcę, kwotę, typ (np. jedzenie, pensja, rozrywka itd.) oraz status (odrzucony/zaakceptowany).

Dane uzupełnione są o dwie etykiety czasowe.

- Pierwsza (`ets`) związana jest z momentem wykonania przelewu.
  Etykieta ta może się losowo spóźniać w stosunku do czasu systemowego maksymalnie do 30 sekund.
- Druga (`its`) związana jest z momentem rejestracji przelewu w systemie.

# Opis atrybutów

Atrybuty w każdym zdarzeniu zgodnym ze schematem `TransferEvent` mają następujące znaczenie:

- `ets` - czas wykonania przelewu 
- `its` - czas rejestracji przelewu w systemie, 
- `category` - kategoria przelewu ["Entertainment", "Salary", "Health", "Food"]
- `status` - status przelewu ["Rejected", "Accepted"]
- `sender` - nadawca przelewu,
- `recipient` - odbiorca przelewu,
- `amount` - kwota przelewu

# Zadania

Opracuj rozwiązania poniższych zadań.

- Opieraj się strumieniu zdarzeń zgodnych ze schematem `TransferEvent`
- W każdym rozwiązaniu możesz skorzystać z jednego lub kilku poleceń EPL.
- Ostatnie polecenie będące ostatecznym rozwiązaniem zadania musi
  - być poleceniem `select`
  - posiadającym etykietę `answer`, przykładowo:

```aidl
@name('answer') SELECT category, status, sender, recipient, amount, ets, its
FROM TransferEvent#ext_timed(java.sql.Timestamp.valueOf(its).getTime(), 3 sec);
```

## Zadanie 1

Utrzymuj informacje o średniej i maksymalnej kwocie transakcji dla poszczególnego odbiorcy w ciągu ostatniej minuty.

Wyniki powinny zawierać następujące kolumny:

- `recipient` - odbiorca 
- `avg` - średnia kwota przelewów
- `max` - maksymalna kwota przelewu.

## Zadanie 2

Wykrywaj przypadki przelewów większych niż 1000.

Wyniki powinny zawierać następujące kolumny:

- `ets` - czas wykonania przelewu
- `category` - kategoria przelewu
- `status` - status przelewu
- `sender` - nadawca
- `recipient` - odbiorca
- `amount` - kwota przelewu.


## Zadanie 3

Ograniczając się do przelewów odrzuconych, wykrywaj przypadki przelewów o kwocie maksymalnej dla poszczególnego odbiorcy w ciągu ostatniej minuty.

Wyniki powinny zawierać następujące kolumny:

- `status` - status przelewu 
- `recipient` - odbiorca 
- `amount` - kwota przelewu.

## Zadanie 4

Urząd skarbowy bardzo martwi się o obywateli i nie chciałby dopuścić do sytuacji, aby ktoś z powodu długów musiał zaciągać pożyczki. 
Dlatego postanowił na bieżąco monitorować wydatki i przychody osób oraz sprawdzać, kiedy wydatki przewyższają przychody.

Wyniki powinny zawierać, następujące kolumny:

- `expenses` - sumę wydatków
- `incomes` - sumę przychodów
- `sender` - osoba

## Zadanie 5

Analitycy bankowi wykryli, że duże przelewy będące oszustwami poprzedzane są serią mniejszych przelewów.
Wykrywaj serie co najmniej trzech przelewów z tego samego konta o wartości nieprzekraczającej 50, zakończonych przelewem na kwotę powyżej 500. Aby seria była oszustwem, musi trwać nie dłużej niż 1 minutę.

Wyniki powinny zawierać, następujące kolumny:

- `sender` - nadawca oraz
- `start_mount` - kwota pierwszego przelewu w serii, 
- `end_amount` - kwota ostatniego przelewu w serii. 


## Zadanie 6

W okresie plagi oszustw bank postanowił wykrywać powtarzające się transakcje w krótkim odstępie czasu na wysoką kwotę pomiędzy takimi samymi parami osób
Wykrywaj przypadki trzech następujących po sobie (niekonieczne bezpośrednio) przelewów, w których osoba A przelewa osobie B dużą kwotę w ciągu 10 sekund a w tym samym czasie osoba B ani nie dokonała żadnego przelewu osobie A.

Wyniki powinny zawierać, następujące kolumny:

- `senderA` - nadawca oraz
- `recipientB` - odbiorca oraz
- `total_amount` - suma kwot trzech przelewów między osobą A i B.

## Zadanie 7

Wykrywaj serie trzech następujących po sobie przelewów wykonywanych pomiędzy dwiema osobami, o coraz większych kwotach zakończonych trzecim przelewem, który został odrzucony.

Wyniki powinny zawierać następujące kolumny:

- `stop_its` - czas rejestracji ostatniego odrzuconego przelewu 
- `start_its` - czas rejestracji pierwszego przelewu
- `amount1` - kwota pierwszego przelewu 
- `amount2` - kwota drugiego przelewu
- `amount3` - kwota trzeciego przelewu