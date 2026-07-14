---
version: alpha
name: ScoreCount
description: Design system for the Score-Count table tennis scoring app.
colors:
  primary: "#1E3A8A"
  secondary: "#22C55E"
  background: "#F8FAFC"
  surface-serving: "#EFF6FF"
  surface-normal: "#F1F5F9"
  on-surface: "#1F2937"
  on-surface-variant: "#6B7280"
  button-bg: "#BFDBFE"
  dark-primary: "#8B5CF6"
  dark-secondary: "#34D399"
  dark-background: "#1A1A2E"
  dark-surface-serving: "#363A59"
  dark-surface-normal: "#282A40"
  dark-on-surface: "#E0E0E0"
  dark-on-surface-variant: "#B0B0B0"
  dark-button-bg: "#4A4E69"
typography:
  body-lg:
    fontFamily: System
    fontSize: 16px
    fontWeight: 400
    lineHeight: 24px
    letterSpacing: 0.5px
  score-display:
    fontFamily: System
    fontSize: 96px
    fontWeight: 900
    lineHeight: 96px
    letterSpacing: -4px
  title-sm:
    fontFamily: System
    fontSize: 14px
    fontWeight: 600
    lineHeight: 20px
    letterSpacing: 0.1px
  label-md:
    fontFamily: System
    fontSize: 12px
    fontWeight: 500
    lineHeight: 16px
    letterSpacing: 0.5px
  headline-sm:
    fontFamily: System
    fontSize: 24px
    fontWeight: 700
    lineHeight: 32px
    letterSpacing: 0px
rounded:
  md: 12px
  xl: 28px
  full: 9999px
spacing:
  xs: 4px
  sm: 8px
  md: 16px
  lg: 32px
  xl: 64px
  grid: 8px
  padding: 16px
components:
  player-score-card:
    backgroundColor: "{colors.surface-normal}"
    rounded: "{rounded.xl}"
  player-score-card-serving:
    backgroundColor: "{colors.surface-serving}"
    rounded: "{rounded.xl}"
    borderColor: "{colors.primary}"
  deuce-indicator:
    backgroundColor: "{colors.secondary}"
    rounded: "{rounded.md}"
  game-sets-indicator:
    backgroundColor: "{colors.primary}"
    rounded: "{rounded.xl}"
  game-bar-action-button:
    rounded: "{rounded.xl}"
  toggle-setting-card:
    rounded: "{rounded.md}"
    height: 120px
  small-icon-button:
    backgroundColor: "{colors.button-bg}"
    size: 32px
---

# Score-Count Design System

## Overview

Score-Count is an Android table tennis score tracking application built with Jetpack Compose. The design system has large text and touch targets so players can read the score and tap controls during a match.

> [!NOTE]
> **Android Implementation Rule (Units Translation)**
> To comply with the `spec.md` schema, all design system tokens in this document's frontmatter are defined in standard pixels (`px`). During Android application development, map all layout token values 1:1 to Density-independent Pixels (`dp`) and all typography token values 1:1 to Scale-independent Pixels (`sp`).

### Design goals
* **Readability**: The score is visible from across a standard table tennis table.
* **Touch targets**: Buttons are large enough to tap quickly without looking.
* **Feedback**: The app vibrates when a player increments the score.

### Target Audience
* Table tennis players and referees requiring a digital scoreboard easily readable from across the table.

---

## Colors

The app has light and dark themes.

### Light mode
* **Primary (#1E3A8A)**: Navy blue. Used for active outlines, selected states, and main text.
* **Secondary (#22C55E)**: Green. Used only for the deuce status indicator.
* **Background & Surface (#F8FAFC)**: Off-white. Used for screen and default container backgrounds.
* **Surface Serving (#EFF6FF)**: Light blue. Used for the background of the card of the player who is serving.
* **Surface Normal (#F1F5F9)**: Light gray. Used for the background of the non-serving player's card.
* **On Surface (#1F2937)**: Charcoal. Used for body text.
* **On Surface Variant (#6B7280)**: Gray. Used for labels and secondary text.
* **Button Background (#BFDBFE)**: Light blue. Used for small icon buttons.

### Dark mode
* **Primary (#8B5CF6)**: Purple. Used for active outlines, selected states, and main text.
* **Secondary (#34D399)**: Mint green. Used for the deuce status indicator.
* **Background & Surface (#1A1A2E)**: Dark navy. Used for screen backgrounds.
* **Surface Serving (#363A59)**: Slate blue. Used for the background of the serving player's card.
* **Surface Normal (#282A40)**: Dark gray. Used for the background of the non-serving player's card.
* **On Surface (#E0E0E0)**: Off-white. Used for body text.
* **On Surface Variant (#B0B0B0)**: Muted gray. Used for labels and secondary text.
* **Button Background (#4A4E69)**: Dark slate. Used for small icon buttons.

---

## Typography

The app uses the default system sans-serif font.

* **Score Display (`score-display`)**: 96px (maps to 96sp in Android). Weight is black (900) with -4px letter spacing. Used for the main score numbers.
* **Headlines (`headline-sm`)**: 24px (24sp) bold. Used for won sets count.
* **Body Large (`body-lg`)**: 16px (16sp) regular. Used for descriptions.
* **Titles (`title-sm`)**: 14px (14sp) semi-bold. Used for headers and button labels.
* **Labels (`label-md`)**: 12px (12sp) medium. Used for settings option labels.

---

## Layout

The layout changes depending on device orientation:

* **Portrait**: Stacks the two player score cards vertically. Controls sit in a bottom bar.
* **Landscape (Compact)**: Places player score cards side-by-side. Actions sit in a narrow vertical column between the cards to save vertical space.
* **Landscape (Taller)**: Places player score cards side-by-side and retains the bottom control bar.
* **Spacing**: Spacing uses an 8px grid. Margins and card paddings are 16px, with 4px used for tight spacing.

---

## Elevation & Depth

The app uses container colors and opacity instead of drop shadows to show depth.
* Cards use surface colors (`surface-normal` and `surface-serving`) to show which player is serving.
* Scoring cards are 0.85 opacity by default. The serving card is 1.0 opacity. Finished cards are 0.75 opacity.
* Bottom sheets use a 3dp elevation overlay.

---

## Shapes

Corner roundness uses three sizes:
* **28px (rounded.xl)**: Used for player cards and set indicators.
* **12px (rounded.md)**: Used for deuce indicators and settings cards.
* **9999px (rounded.full)**: Used for buttons.

---

## Components

Component styling rules:

### Player Score Card
* **Background Color**: Uses `{colors.surface-serving}` when the player is actively serving, and `{colors.surface-normal}` in the normal non-serving state.
* **Border Outline**: Border is a 2px outline of `{colors.primary}` when serving. Otherwise, there is no outline.
* **Serve Indicator**: The top-left corner of the serving card has a shimmering icon and a "SERVING" label with 14px padding.

### Deuce Indicator
* Uses `{colors.secondary}` background.
* Has a 1px inner border of `onSecondary` with 2px padding.

### Game Sets Pill (`game-sets-indicator`)
* A horizontal pill with `{colors.primary}` background and 28px rounded corners. It shows the current set and best-of value separated by a divider.

### Toggle Setting Card
* Uses 12px rounded corners and is 120px tall. The outline is `{colors.primary}` when selected, and `{colors.on-surface-variant}` when unselected.

---

## Do's and Don'ts

* **Do** show the active server outline only during gameplay.
* **Do** use the vertical control column in landscape mode.
* **Don't** leave player card backgrounds transparent.
* **Don't** override Material 3 shapes globally.
