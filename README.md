# 🚂 Bitcoin Fear & Tilt

A premium, skeuomorphic game-art style Android application that visualizes Bitcoin price dynamics and market sentiment in real-time. 

Instead of dry financial charts, **Bitcoin Fear & Tilt** visualizes market psychology using an animated wooden minecart on a physics-based tilting slope. The slope angle indicates the price movement direction and velocity, while the coin's facial expression mirrors the current market sentiment (from ecstatic greed to panic-stricken fear).

---

## 🎨 Design & Key Features

### 🎮 Skeuomorphic Dashboard
- **Animated Wooden Cart**: A high-fidelity cartoon wooden cart that tilts dynamically using **Jetpack Compose spring-physics animations** to reflect price changes.
- **Double LCD Displays**: Retro-illuminated game-art monitors showing:
  - **Live Bitcoin Price** in USD.
  - **Price Change Percentage** (selectable timeframes: 30m, 24h, or start-of-day).
  - **Fear & Greed Index** value accompanied by a dynamic sentiment classification text.
- **Sentiment Lamps**: Glow lamps that shift color based on sentiment thresholds (customizable red/green orientation, including inverted Chinese-style color mappings).

### 🛠️ Pinned Settings Console
- **Local Settings Buffering**: Changes made inside the settings console (timeframe, speed threshold, sensitivity, thresholds, widget background) are buffered locally in memory. Settings are only written to the database when clicking **Apply & SAVE**. Going back or swiping back discards changes.
- **Auto-Keyboard Dismissal**: Released focus and closed soft keyboards automatically on exit to prevent system gesture conflicts.
- **Interactive Reset Defaults**: Revert all preferences to default presets with a single click before choosing to save or cancel.

### 📱 Home Screen Widget (Jetpack Glance)
- **Adaptive Sizing**: Adapts seamlessly to both vertical and horizontal widget boundaries.
- **Auto-Cropping Illustrations**: Code dynamically samples background colors and crops empty canvas margins from source images, enabling the cart illustration to be significantly larger and sharper on high-DPI screens.
- **Three-Line LCD Readout**: Displays the live price, Fear & Greed Index, and the direction/percentage of price change.
- **Flexible Transparency**: Background card transparency toggles in settings to match any home screen wallpaper.

### 🎭 Dynamic Launcher Icons (Duolingo Style)
- **Adaptive Full-Bleed Design**: Dynamic icons constructed as standard Android Adaptive Icons (`mipmap-anydpi-v26`) combining transparent foreground illustrations with a solid sky-blue background (`#9EBAC5`). The icon masks perfectly into circles, squares, or squircles without white borders.
- **Changing Expressions**: The launcher icon dynamically changes based on the Bitcoin Fear & Greed Index, swapping between 5 distinct cartoon expressions:
  - 🤩 **Extreme Greed** (Ecstatic open grin)
  - 🙂 **Greed** (Cheerful smile)
  - 😐 **Neutral** (Default straight expression)
  - 😟 **Fear** (Worried / anxious expression)
  - 😱 **Extreme Fear** (Panic-stricken screaming)
- **Legacy Fallbacks**: High-density square PNG files automatically configured inside `mipmap/` to support older Android versions.

---

## 🛠️ Technology Stack

- **Language**: Kotlin
- **UI Architecture**: Jetpack Compose (Modern declarative UI)
- **Home Screen Widget**: Jetpack Glance (Jetpack Compose wrapper for RemoteViews)
- **Persistent Storage**: Preferences DataStore (Typed, thread-safe asynchronous storage)
- **Background Tasks**: Android WorkManager (Periodic updates scheduled every 15 minutes)
- **Network Client**: Retrofit & OKHttp (Fetches price updates from CoinGecko/Binance APIs and sentiment indices from Alternative.me)

---

## 🚀 Building & Installation

### Prerequisites
- JDK 21
- Android SDK (Target API 36, Minimum API 24)

### Compile & Build via CLI
Configure your Java home path and execute the debug assembly:
```powershell
# Set JDK 21 environment variable
$env:JAVA_HOME="C:\Program Files\Java\jdk-21"

# Clean build artifacts and compile APK
.\gradlew.bat clean assembleDebug
```
The compiled APK will be available at:
`app/build/outputs/apk/debug/app-debug.apk`

### Deploy & Run
Install the compiled APK on a connected device/emulator:
```powershell
# Install APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch App
adb shell monkey -p com.example.myapplication -c android.intent.category.LAUNCHER 1
```

---

## 💡 Android Studio Run Tip (Dynamic Icon Debugging)

Because the app uses `<activity-alias>` elements in the `AndroidManifest.xml` to dynamically swap the launcher icon, Android Studio's runner might output a verification error:
`Activity class {com.example.myapplication/com.example.myapplication.MainActivityNeutral} does not exist`

To resolve this launcher verification issue in your IDE:
1. Click the **Run Configuration** dropdown (next to the green play button) and choose **Edit Configurations...**.
2. Locate the **Launch Options** section.
3. Change **Launch** from `Default Activity` to `Specified Activity`.
4. In the **Activity** text box, enter:
   `com.example.myapplication.MainActivity`
5. Click **Apply** and **OK**.