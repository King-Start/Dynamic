<p align="center">
  <img src="docs/logo.png" width="120" alt="Dynamic Island logo">
</p>

<h1 align="center">Dynamic Island for Android</h1>

<p align="center">
A Dynamic Island–style capsule around the camera of any Android phone, with 8 visual styles.
</p>

<p align="center">
  <img alt="Android 8+" src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.1-7F52FF?style=flat-square&logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white">
  <img alt="No ads" src="https://img.shields.io/badge/ads-0-black?style=flat-square">
</p>

[Bahasa Indonesia](README.md)

## Features

Notifications with reply · navigation · payments · music with lyrics · calls · timers · downloads · charging · flashlight · system events.

## Notification designs

App notification cards come in eight genuinely different forms, not just effects: Classic, Card, Ticker (scrolling text beside the camera), Chat (message in a bubble), Banner, Split, Headline and Badge. Replies and action buttons work in every design. Pick one from the gallery in **Appearance → Notification design**, or use **Ready-made themes** to set style, layout and notifications at once.

## Layouts

Besides the style, the island content has six layouts: Classic, Ring, Labeled, Two lines, Progress bar and Mini. Pick one in **Appearance → Layout** or per feature. They apply to music, timer, stopwatch, clock, calls (including the incoming-call card), flashlight, downloads and charging.

## Styles

Pick one in **Appearance → Island style**, or set it per feature (music, timer, calls, charging…).

| Style | Look |
|---|---|
| Dynamic Island | Classic black capsule, optional Liquid Glass |
| HyperIsland | HyperOS-style dark glass with directional light |
| Neon | Dark body with a glowing edge |
| Gradient | Gradient from the accent colour to dark |
| iPhone | Clean black, thin edge |
| Aurora | Two soft coloured glows and a tri-colour rim |
| Sunset | Orange → magenta → deep violet |
| Frost | Translucent dark-blue glass |

## Install

1. Download the APK from **Releases** and install it.
2. Grant the overlay, notification access and accessibility permissions as the app guides you.
3. Turn the island on from Home.

## Build

Standard Gradle project (AGP 8.7, Kotlin 2.1, Compose, minSdk 26):

```bash
./gradlew assembleRelease
```

GitHub Actions builds an APK on every push. For a signed APK add the secrets `KEYSTORE_BASE64` and `KEYSTORE_PASSWORD` (key alias `island`, PKCS12). Optional: the repository variable `SIGNING_CERT_SHA256` pins the certificate fingerprint.

To publish a new version, raise `versionName` and `versionCode` in `app/build.gradle.kts` and add a `## [x.y.z]` section to `CHANGELOG.md` and `CHANGELOG.en.md`.

## Credits

This project builds on the following open-source work. Thanks to the original authors:

- **Dynamic Island** by [Akira9021](https://github.com/Akira9021/Dynamic-Island-) — the overlay app base.
- **HyperIsland** by [1812z](https://github.com/1812z/HyperIsland) — reference for the HyperOS glass style.
- [Inter](docs/licenses/Inter-OFL.txt) font (OFL) and [Ionicons](docs/licenses/Ionicons-MIT.txt) icons (MIT).

The app logo and the Aurora, Sunset and Frost styles were made for this version.
