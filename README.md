# 🚂 Bitcoin Fear & Tilt

*Languages: [English](README.md) | [中文](README_zh.md)*

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/ololobin/Bitcoin-Fear-and-Tilt/android.yml?style=flat-square&logo=github)
![GitHub License](https://img.shields.io/github/license/ololobin/Bitcoin-Fear-and-Tilt?style=flat-square)
![Android Version](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=flat-square&logo=android)

**Bitcoin Fear & Tilt** is an Android application designed for real-time tracking of Bitcoin price dynamics and the market "Fear and Greed Index". Moving beyond traditional financial charts, it utilizes **physics-based animations**: a skeuomorphic wooden minecart dynamically reacts to market velocity and directional price changes.

> [!TIP]
> The application functions as a comprehensive full-screen dashboard, as well as a **highly customizable home screen widget**.

<!-- PLACEHOLDER FOR MAIN BANNER / SCREENSHOT -->
<!-- Insert the link to your beautiful screenshot replacing /path/to/hero_image.png -->
![App Main Screen](/path/to/hero_image.png)

## 🌟 Key Features

- **Physics-Based Animation:** The interface responds to price volatility with spring-physics calculations. The visual state dynamically transitions to reflect the spectrum of market sentiment.
- **Retro LCD Displays:** Provides real-time metrics including the current asset price, percentage change, and the exact Fear & Greed index value.
- **Advanced Customization:** Users can configure tracking timeframes (30 mins, 24 hours, since midnight), adjust tilt sensitivity, and define custom sentiment index thresholds.
- **Home Screen Widget:** Delivers essential market data (Price, Index, Trend) directly to your home screen for immediate access.
- **Dynamic Launcher Icon:** The application icon programmatically updates its visual state within the system launcher to mirror current market conditions.
- **Multilingual Support:** Fully localized for English, Russian, Spanish, Chinese, and French.

## 📱 Home Screen Widget

Deploy a lightweight, auto-updating widget to your home screen. It features customizable background transparency to ensure seamless integration with any system wallpaper.

<!-- PLACEHOLDER FOR WIDGET SCREENSHOT -->
<!-- Insert the link to the widget screenshot replacing /path/to/widget_image.png -->
![Widget Screenshot](/path/to/widget_image.png)

## 🛡️ Security & Build Transparency

To ensure complete security and transparency for our users, this application employs an automated and public build process:

1. **Automated CI/CD:** The release APK is compiled directly from the open-source codebase using GitHub Actions. There are no manual or opaque build steps.
2. **Verifiable Logs:** The entire compilation process, including dependency resolution and build scripts, is publicly available in the [Actions](../../actions) tab for independent audit.
3. **External Verification:** Users are encouraged to verify the integrity of the downloaded APK files through independent security services such as [VirusTotal](https://www.virustotal.com/) prior to installation.

## 📥 Installation

1. Navigate to the **[Releases](../../releases)** section on GitHub.
2. Download the latest compiled `app-debug.apk` (Production releases will be available shortly).
3. Install the application on your Android device (ensure installation from unknown sources is permitted in system settings).

---

## 🤝 Acknowledgments & Credits

* **Concept Inspiration:** This project is conceptually inspired by the well-known Bitcoin Rollercoaster meme and the platform [rollercoasterguy.github.io](https://rollercoasterguy.github.io/).
* **Artwork:** The skeuomorphic graphical assets (including the cart, coins, and expressions) were originally generated and tailored using **Nanobanana** AI tools.

## 🛠️ For Developers

The application is engineered in **Kotlin**, leveraging the modern **Jetpack Compose** UI toolkit and **Jetpack Glance** for widget implementation.

To compile the project locally, clone the repository and execute:
```bash
./gradlew assembleDebug
```