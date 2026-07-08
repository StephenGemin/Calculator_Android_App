# Calculator Android App

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-purple.svg)](https://kotlinlang.org/)
[![API](https://img.shields.io/badge/API-21%2B-green.svg)](https://android-arsenal.com/api?level=21)
[![Target SDK](https://img.shields.io/badge/Target%20SDK-35-orange.svg)](https://developer.android.com/about/versions/15)

A precision-focused Android calculator with **dual portrait/landscape layouts** (basic & scientific) and **arbitrary-precision arithmetic** using BigDecimal. Written entirely in Kotlin.

## Quick Start

* build: `make build`
* install: `make install`
* launch: `make launch`
    * build, install and launch app

### Prerequisites
- JDK 17 or higher
- Android SDK 21+ (minimum API level)
- Android build tools 35+

### Using Android Studio
1. Open the project in Android Studio
2. Click **Run** or press **Shift + F10** to build and launch on an emulator or device

<div align="center">

| Basic Calculator (Light) | Basic Calculator (Dark) |
|:---:|:---:|
| <img src="./images/basic_calculator_layout_light.jpg" alt="Basic Calculator Light" width="280"> | <img src="./images/basic_calculator_layout_dark.jpg" alt="Basic Calculator Dark" width="280"> |

</div>   

**Scientific Calculator**

| Light |
|---|
| ![Scientific Calculator Light](./images/scientific_calculator_layout_light.jpg) |

| Dark |
|---|
| ![Scientific Calculator Dark](./images/scientific_calculator_layout_dark.jpg) |

## Features

- **BigDecimal precision arithmetic** — uses [EvalEx](https://github.com/uklimaschewski/EvalEx) for exact decimal calculations (no floating-point rounding errors)
- **Reverse Polish Notation (RPN)** evaluation for reliable expression parsing
- **Dual-mode UI** — basic four-function calculator in portrait, full scientific calculator in landscape
- **Modern Android stack** — Kotlin, AndroidX, Material Design compatible
- **Unit tested** — includes parameterized JUnit tests for calculation validation

## Architecture

The app uses a single-Activity design. `MainActivity` is a thin UI layer that wires button clicks to `CalculatorEngine` and renders its state; all calculation logic and state live in `CalculatorEngine`, which delegates expression evaluation to EvalEx.

### Key Components
- **CalculatorEngine** (`app/src/main/java/com/example/calculator_v01/CalculatorEngine.kt`) — state machine for calculator input/eval, EvalEx integration
- **MainActivity** (`app/src/main/java/com/example/calculator_v01/MainActivity.kt`) — button handlers and rendering only, no calc logic
- **Layouts** — `activity_main.xml` (portrait) and `layout-land/activity_main.xml` (landscape)
- **Tests** — `CalculatorEngineTest.kt` (state behavior) and `CalculatorEvalTest.kt` (parameterized arithmetic evaluation)

## Build Information

| Property | Value |
|---|---|
| Gradle | 8.7+ |
| AGP (Android Gradle Plugin) | 8.5+ |
| Kotlin | 2.0.0 |
| Compile SDK | 35 |
| Min SDK | 21 |
| Target SDK | 35 |
| Java/Kotlin JVM | 17 |

## Acknowledgments

- [EvalEx](https://github.com/uklimaschewski/EvalEx) — expression evaluation library with BigDecimal precision
- [Android Developers](https://developer.android.com) — comprehensive Android documentation
