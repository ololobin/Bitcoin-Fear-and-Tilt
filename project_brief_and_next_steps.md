# Agent Briefing: Bitcoin Fear & Tilt Project

Welcome to the **Bitcoin Fear & Tilt** project! This document outlines the codebase architecture, critical gotchas, resolved bugs, and key file mappings to help you get up to speed instantly in a new session.

---

## 1. Codebase Architecture

The app is a Jetpack Compose Android application that fetches Bitcoin price data and the Fear & Greed (F&G) Index, animating a skeuomorphic game-art wooden cart on a slope. The slope and direction of the cart represent price movement.

### Core Architecture Components:
*   **Preferences DataStore**: Local persistent storage for user preferences (timeframe, sensitivity, thresholds, language, widget background transparency).
*   **Retrofit & OKHttp**: Handles API calls to CoinGecko, Binance, Alternative.me, etc.
*   **WorkManager**: Schedules periodic background updates (`UpdateWorker`) every 15 minutes.
*   **Jetpack Glance**: Manages the home screen widget.
*   **Jetpack Compose**: Powers the main skeuomorphic wood-panel settings and dashboard console UI.

---

## 2. Key Files Map

All paths are relative to the project root:

1.  **Widget Implementation**:
    *   [BtcWidget.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/widget/BtcWidget.kt): Glance widget composition, adaptive layouts, dynamic image scaling, and pixel-level transparency background stripping.
    *   [BtcWidgetReceiver.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/widget/BtcWidgetReceiver.kt): Receiver that binds the widget.
    *   [btc_widget_info.xml](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/res/xml/btc_widget_info.xml): Home screen widget metadata.
2.  **App Screens (UI)**:
    *   [MainScreen.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/ui/screen/MainScreen.kt): App dashboard containing the tilted cart, spring-physics rotation animation, LCD screens, and status lamps.
    *   [SettingsScreen.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/ui/screen/SettingsScreen.kt): Console settings screen. Pinned "Apply & Save" button at the top, scrollable options list, and "Reset Defaults" at the very bottom. Uses `BackHandler` to intercept the system back gesture and return to the dashboard.
    *   [SkeuomorphicComponents.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/ui/component/SkeuomorphicComponents.kt): Core styling building blocks (wood cards, dials, rivets, glow lamps).
3.  **Data & Network Layers**:
    *   [AppSettings.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/data/pref/AppSettings.kt): AppSettings data class, Preferences DataStore manager, and `getLocalizedString` helper for localization.
    *   [CryptoRepository.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/data/repository/CryptoRepository.kt): Coordinates data updates. Implements cascades, caching, a 30s manual refresh rate-limiter, and psychological price crossover cart triggers.
    *   [CryptoViewModel.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/ui/CryptoViewModel.kt): UI state holder, wires settings writes, and triggers widget updates via `BtcWidget().updateAll(context)`.
    *   [BtcPriceApis.kt](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/java/com/example/myapplication/data/api/BtcPriceApis.kt): API Retrofit definitions.
4.  **Localization Resources**:
    *   [strings.xml](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/res/values/strings.xml): English strings.
    *   [strings.xml (Russian)](file:///o:/vibecoding/Bitcoin-Fear-and-Tilt/app/src/main/res/values-ru/strings.xml): Russian strings.

---

## 3. Critical Gotchas & Crash Resolutions

Keep these in mind to prevent regression bugs:

### A. Jetpack Glance Memory Limit (Binder Size Limit)
*   **Problem**: Jetpack Glance sends layouts to the launcher via IPC (Binder). A 2MB total layout memory size limit exists (`ensureWidgetViewsMemoryLimitLocked`). 
*   **Bad Approach**: Using `SizeMode.Responsive` compiles and serializes layouts for 5 distinct sizes at the same time. If each size decodes a large rotated bitmap inside `provideContent`, the Binder transaction size gets exceeded, breaking/crashing the widget.
*   **Fixed Approach**: 
    1.  Use `override val sizeMode = SizeMode.Exact` so that only one layout (the current physical widget size) is generated in memory.
    2.  Scale the decoded image dynamically depending on screen density, capped at a maximum of `400px` (e.g. `minDim * density` coerced to `200..400` pixels). This keeps the image sharp on high-DPI tablets/phones, but limits its memory size to ~640KB.
    3.  Directly resize the decoded bitmap in `rotateBitmap` using `Bitmap.createScaledBitmap` to match `targetSizePx` exactly, recycling intermediate bitmaps immediately to free heap memory.

### B. Russian Localization `%` String Format Crash
*   **Problem**: Russian strings containing literal percentage symbols (e.g., `ПОРОГ СКОРОСТИ (X%)` or `% изменения`) caused the app to crash with `UnknownFormatConversionException` when loaded via `resources.getString(resId, *formatArgs)`. If `formatArgs` is empty, Java's formatting engine still interprets the `%` character as a format specifier.
*   **Fixed Approach**: In `getLocalizedString` inside `AppSettings.kt`, perform a check: if `formatArgs` is empty, fetch the raw string using the single-parameter `getString(resId)` which bypasses the formatter completely.

### C. Widget Sync Lag
*   **Problem**: Writing to Preferences DataStore does not trigger Glance widget redraws automatically.
*   **Fixed Approach**: In `CryptoViewModel.kt`, every setter method (or price fetch) that modifies preferences must explicitly call `BtcWidget().updateAll(getApplication())` to invalidate the Glance session and push updates to the home screen instantly.

### D. Overlay Overlaps on Small Widgets
*   **Problem**: Overlaying text on top of the image in the vertical layout can cover the main cart illustration, which is highly visible on small widgets.
*   **Fixed Approach**: The vertical layout must stack components in a `Column` (Image on top, text backing card at the bottom) so they never overlap. The image is given weight `.defaultWeight().fillMaxWidth()` to take up all remaining vertical space.

### E. Solid Background Text Backing Card
*   **Problem**: Putting a dark backing card behind widget text is useful for legibility on transparent backgrounds, but is unnecessary and distracting on solid backgrounds.
*   **Fixed Approach**: Dynamically apply the background modifier to the text column based on `settings.isWidgetBackgroundTransparent`:
    ```kotlin
    if (settings.isWidgetBackgroundTransparent) {
        GlanceModifier.background(ColorProvider(Color(red = 0f, green = 0f, blue = 0f, alpha = 0.6f)))
    } else {
        GlanceModifier
    }
    ```

---

## 4. Run & Verification Commands

To compile, build, and deploy the application, configure your Java home correctly:

```powershell
# Set JDK 21 paths
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"

# Compile and build the debug APK
.\gradlew.bat assembleDebug

# Deploy debug APK to the active device
C:\Users\admin\AppData\Local\Android\Sdk\platform-tools\adb.exe install -r app/build/outputs/apk/debug/app-debug.apk

# Launch app using ADB monkey
C:\Users\admin\AppData\Local\Android\Sdk\platform-tools\adb.exe shell monkey -p com.example.myapplication -c android.intent.category.LAUNCHER 1

# View application errors and logs
C:\Users\admin\AppData\Local\Android\Sdk\platform-tools\adb.exe logcat --pid=$(C:\Users\admin\AppData\Local\Android\Sdk\platform-tools\adb.exe shell pidof com.example.myapplication) -d
```
