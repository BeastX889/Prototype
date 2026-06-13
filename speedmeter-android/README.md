# Speed Meter (Android)

A small Kotlin app that recreates the old Samsung-style **network speed
indicator** — it shows your live **download (↓)** and **upload (↑)** throughput
in the status bar, auto-scaling between B/s, KB/s and MB/s.

This is a standalone Android project that lives inside the `Prototype` repo; it
is unrelated to the Jekyll web page at the repo root.

## How it works

- A foreground **`SpeedMonitorService`** samples `TrafficStats.getTotalRxBytes()`
  / `getTotalTxBytes()` once per second and computes the per-second delta.
- The speed text is drawn onto a small bitmap (`SpeedIconRenderer`) and set as
  the notification's **small icon**, so the numbers appear in the status bar.
- A one-screen Jetpack **Compose** UI (`SpeedScreen`) shows the live readout and
  a **Start/Stop** toggle. Service ↔ UI state is shared via `SpeedState`.

No special permissions are needed beyond `FOREGROUND_SERVICE*` and (on Android
13+) `POST_NOTIFICATIONS`; `TrafficStats` avoids the usage-access flow that
per-app stats would require.

## Project layout

```
app/src/main/java/com/example/speedmeter/
  MainActivity.kt              # entry point, permission + start/stop intents
  ui/SpeedScreen.kt            # Compose UI
  ui/theme/Theme.kt            # Material 3 (dynamic color on 12+)
  service/SpeedMonitorService.kt
  service/SpeedState.kt        # shared StateFlow holder
  util/SpeedFormatter.kt       # bytes/s -> "1.2 MB/s" (unit-tested)
  util/SpeedIconRenderer.kt    # speed text -> status-bar bitmap icon
  model/SpeedSample.kt
app/src/test/java/.../SpeedFormatterTest.kt
```

## Build & install

Requires the Android SDK (e.g. via Android Studio) with a `local.properties`
pointing at it (`sdk.dir=/path/to/Android/sdk`).

```bash
cd speedmeter-android
./gradlew testDebugUnitTest   # run the formatter unit tests
./gradlew assembleDebug       # build app/build/outputs/apk/debug/app-debug.apk
./gradlew installDebug        # install onto a connected device/emulator
```

## Using it

1. Launch **Speed Meter** and tap **Start**.
2. Grant the notification permission when prompted (Android 13+).
3. Load a page or start a download — the ↓/↑ figures update every second, both
   on screen and as the status-bar icon. Tap **Stop** (in-app or via the
   notification action) to end monitoring.

> Note: on emulators `TrafficStats` may report unsupported (-1); test on a real
> device for accurate readings.

## Roadmap (not in this MVP)

Daily data-usage stats, a settings screen, boot auto-start, and theme options.
