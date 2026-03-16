# Katalog Komponentów Core (ParamedQuiz)

Ten dokument jest listą dostępnych komponentów UI w module `core`. Używaj ich ZAMIAST standardowych komponentów Material3.

## Przyciski (`Buttons.kt`, `ActionButtons.kt`)
- `ButtonPrimary(text, onClick, enabled, modifier)` - Główny przycisk akcji (Filled).
- `ButtonSecondary(text, onClick, enabled, modifier)` - Przycisk drugorzędny (Outlined).
- `ButtonTertiary(text, onClick, enabled, modifier)` - Przycisk tekstowy (Transparent).
- `ActionButton(icon, onClick, modifier)` - Przycisk z ikoną (VectorAsset).
- `ActionButtonImage(painter, onClick, modifier)` - Przycisk z ikoną (Painter).

## Teksty (`Texts.kt`)
- `TextPrimary(text, modifier, maxLines, textAlign, color)` - Standardowy tekst body.
- `TextHeadline(text, modifier, textAlign, color)` - Mały nagłówek (`headlineSmall`).
- `TextTitle(text, modifier, textAlign, color)` - Duży, pogrubiony tytuł (`headlineLarge`).
- `TextCaption(text, modifier, textAlign, color)` - Mały tekst pomocniczy (`labelSmall`).
- `TextCaptionLink(text, onClick, modifier)` - Klikalny link.
- `TextScore(label, score, modifier)` - Wyspecjalizowany tekst do wyświetlania wyników.

## Pola Tekstowe (`TextFields.kt`)
- `TextFieldPrimary(value, onValueChange, label, modifier, keyboardOptions, keyboardActions, isError, visualTransformation, trailingIcon)` - Standardowy input.
- `TextFieldMultiLine(value, onValueChange, label, modifier, isError)` - Wieloliniowy input (min 5 linii).
- `PasswordTextFieldPrimary(...)` - Pole hasła z przełącznikiem widoczności.

## Nawigacja i Bary (`top_bars/`)
- `MainTopBar(title, onMenuClick, onProfileClick)`
- `NavTopBar(title, onBackClick)`
- `QuizTopBar(currentQuestion, totalQuestions, onQuitClick)`

## Ładowanie i Dialogi (`Loading.kt`, `BaseDialogs.kt`)
- `Loading(modifier)` - Pełnoekranowy loader.
- `BaseDialog(title, description, onConfirm, onDismiss, ...)` - Podstawowy dialog informacyjny.
- `ConfirmDialog(...)`, `ErrorDialog(...)`.

## Inne
- `QuizLinearProgressBar` - Pasek postępu w quizie.
- `NotificationDot` - Czerwona kropka powiadomienia.
- `OwnedBadge` - Plakietka "posiadane".
- `ReportIssueDialog` - Gotowy dialog do zgłaszania błędów.

**PAMIĘTAJ:** Jeśli modyfikujesz komponenty w `core`, zaktualizuj ten plik!
