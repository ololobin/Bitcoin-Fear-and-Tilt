# 🚂 Bitcoin Fear & Tilt

*Languages: [English](README.md) | [中文](README_zh.md)*

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/ololobin/Bitcoin-Fear-and-Tilt/android.yml?style=flat-square&logo=github)
![GitHub License](https://img.shields.io/github/license/ololobin/Bitcoin-Fear-and-Tilt?style=flat-square)
![Android Version](https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=flat-square&logo=android)

**Bitcoin Fear & Tilt** 是一款独特的 Android 应用程序，用于追踪比特币价格和“恐慌与贪婪指数”。它没有使用枯燥的财务图表，而是利用了**物理和动画**：一辆木制矿车会根据价格变化的速度和方向动态倾斜！

> [!TIP]
> 该应用既可作为全屏体验，也可作为**便捷的主屏幕小组件**运行！

<!-- 主横幅 / 截图占位符 -->
<!-- 将链接插入您漂亮的截图以替换 /path/to/hero_image.png -->
![应用主屏幕](/path/to/hero_image.png)

## 🌟 主要功能

- **动画矿车：** 随着价格的跳动而倾斜（使用弹性物理）。硬币的表情会从极度恐慌变为欣喜若狂！
- **复古 LCD 屏幕：** 显示当前价格、百分比变化以及精确的恐慌与贪婪指数。
- **深度定制：** 根据您的喜好配置时间范围（30分钟、24小时、从午夜开始）、矿车的倾斜灵敏度以及指数阈值。
- **主屏幕小组件：** 所有基本信息（价格、指数、趋势）始终在您眼前。
- **动态启动器图标：** 应用程序图标会根据当前市场状态在您的手机应用抽屉中改变表情！（Duolingo 风格）。
- **多语言：** 全面支持英语、俄语、西班牙语、中文和法语。

## 📱 主屏幕小组件

您可以添加一个自动更新的紧凑小组件。背景可以是纯色或完全透明，与您的壁纸完美融合。

<!-- 小组件截图占位符 -->
<!-- 将链接插入小组件截图以替换 /path/to/widget_image.png -->
![小组件截图](/path/to/widget_image.png)

## 🛡️ 安全与信任（无病毒）

我们了解直接从互联网安装 APK 文件可能会引起担忧。因此，该应用的构建过程是完全**透明**的：

1. **没有隐藏的构建：** 开箱即用的 APK 文件由 GitHub 服务器（通过 GitHub Actions）直接从该存储库中的开源代码自动构建。
2. **公共日志：** 您可以随时查看 [Actions](../../actions) 选项卡并亲自查看整个编译过程。作为开发人员，我无法秘密替换该文件。
3. **VirusTotal 验证：** 如果您仍有疑问，只需从 releases 版本中下载 APK，并在安装前将其上传到 [VirusTotal](https://www.virustotal.com/)。

## 📥 安装

1. 转到 GitHub 上的 **[Releases](../../releases)** 部分。
2. 下载最新的 `app-debug.apk`（Release 版本即将推出）。
3. 将其安装在您的 Android 智能手机上（您可能需要允许安装来自未知来源的应用）。

---

## 🤝 致谢与版权 (Acknowledgments & Credits)

* **概念灵感 (Concept Inspiration)：** 该应用程序深受著名的比特币过山车模因 (Bitcoin Rollercoaster meme) 的启发，特别是网站 [rollercoasterguy.github.io](https://rollercoasterguy.github.io/)。
* **艺术资产 (Artwork)：** 应用程序中的卡通和拟物化图形资产（矿车、硬币、表情）最初是使用 **Nanobanana** AI 工具生成和定制的。

## 🛠️ 给开发者

该应用程序使用现代的 **Jetpack Compose** 堆栈和 **Jetpack Glance**（用于小组件）采用 **Kotlin** 编写。

若要进行本地构建，只需克隆存储库并运行：
```bash
./gradlew assembleDebug
```
