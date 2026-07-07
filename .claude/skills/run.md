---
name: run
description: Build and launch the Calculator Android app on an emulator
---

# Run the Calculator app

Builds the app and launches it on a running Android emulator.

## How it works

1. Run `./gradlew assembleDebug` to build a debug APK
2. Install it on the emulator: `adb install -r app/build/outputs/apk/debug/*.apk`
3. Start the calculator activity: `adb shell am start -n com.example.calculator_v01/.MainActivity`

## Requirements

- Android SDK tools installed (adb, emulator)
- Emulator running (start separately if not already up)
- ANDROID_HOME set to the SDK root
