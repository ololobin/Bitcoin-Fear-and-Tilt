# 🚂 Bitcoin Fear & Tilt

*Languages: [English](README.md) | [中文](README_zh.md)*

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/ololobin/Bitcoin-Fear-and-Tilt/android.yml?style=flat-square&logo=github)
![GitHub License](https://img.shields.io/github/license/ololobin/Bitcoin-Fear-and-Tilt?style=flat-square)
![Android Version](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=flat-square&logo=android)

**Bitcoin Fear & Tilt** is a unique Android application for tracking Bitcoin's price and the "Fear and Greed Index". Instead of boring financial charts, it uses **physics and animation**: a wooden minecart dynamically tilts based on the speed and direction of price changes!

> [!TIP]
> The app works both as a full-screen experience and as a **convenient home screen widget**!

<!-- PLACEHOLDER FOR MAIN BANNER / SCREENSHOT -->
<!-- Insert the link to your beautiful screenshot replacing /path/to/hero_image.png -->
![App Main Screen](/path/to/hero_image.png)

## 🌟 Key Features

- **Animated Minecart:** Reacts to price jumps by tilting (using Spring Physics). The coin's face changes from panic-stricken fear to ecstatic greed!
- **Retro LCD Screens:** Display the current price, percentage change, and the exact Fear & Greed index.
- **Deep Customization:** Configure the timeframe (30 mins, 24 hours, since midnight), the cart's tilt sensitivity, and the index thresholds to your liking.
- **Home Screen Widget:** All essential information (Price, Index, Trend) is always in front of you.
- **Dynamic Launcher Icon:** The app icon changes its facial expression in your phone's app drawer based on the current market state! (Duolingo style).
- **Multilingual:** Full support for English, Russian, Spanish, Chinese, and French.

## 📱 Home Screen Widget

You can add a compact widget that updates automatically. The background can be solid or fully transparent to blend beautifully with your wallpaper.

<!-- PLACEHOLDER FOR WIDGET SCREENSHOT -->
<!-- Insert the link to the widget screenshot replacing /path/to/widget_image.png -->
![Widget Screenshot](/path/to/widget_image.png)

## 🛡️ Security & Trust (No Viruses)

We understand that installing APK files directly from the internet can be concerning. That's why the build process for this app is completely **transparent**:

1. **No hidden builds:** The ready-to-use APK file is built automatically by GitHub servers (via GitHub Actions) directly from the open-source code in this repository.
2. **Public logs:** You can always check the [Actions](../../actions) tab and personally review the entire compilation process. As a developer, I cannot secretly swap the file.
3. **VirusTotal Verification:** If you still have doubts, simply download the APK from the releases and upload it to [VirusTotal](https://www.virustotal.com/) before installing.

## 📥 Installation

1. Go to the **[Releases](../../releases)** section on GitHub.
2. Download the latest `app-debug.apk` (a Release version is coming soon).
3. Install it on your Android smartphone (you may need to allow installation from unknown sources).

---

## 🤝 Acknowledgments & Credits

* **Concept Inspiration:** This app is heavily inspired by the famous Bitcoin Rollercoaster meme and specifically the website [rollercoasterguy.github.io](https://rollercoasterguy.github.io/).
* **Artwork:** The skeuomorphic and cartoonish graphical assets (the cart, the coins, the expressions) were originally generated and tailored using **Nanobanana** AI tools.

## 🛠️ For Developers

The app is written in **Kotlin** using the modern **Jetpack Compose** stack and **Jetpack Glance** (for widgets).

For a local build, simply clone the repository and run:
```bash
./gradlew assembleDebug
```