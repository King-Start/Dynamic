# Dynamic Island + HyperIsland — Aplikasi Gabungan

Penggabungan dua aplikasi dynamic island menjadi **satu aplikasi**:

| Sumber | Peran |
|---|---|
| **Dynamic Island** (`com.akira.island`, Akira9021) | **Basis.** Aplikasi overlay standalone, jalan di Android 8+ **tanpa root**, semua merek HP. |
| **HyperIsland** (`io.github.hyperisland`, 1812z) | **Sumber gaya visual + path HyperOS.** Modul LSPosed untuk HyperOS 3/4 (Xiaomi). |

## Apa yang bisa dilakukan aplikasi gabungan ini

1. **Tanpa root** — mode overlay biasa (aksesibilitas + notifikasi), seperti Dynamic Island asli. Jalan di semua HP, termasuk Xiaomi/HyperOS.
2. **Dengan Shizuku** — tambahan hak sistem tanpa root (izin tinggi, aksi dengan hak sistem). Shizuku tidak wajib: tanpa Shizuku aplikasi tetap jalan.
3. **Dengan root / HyperOS** — jika di HP Xiaomi HyperOS dengan LSPosed, path HyperIsland tersedia (butuh modul Xposed terpisah; lihat catatan di bawah).
4. **Dua tampilan island, pilih sendiri**:
   - **Dynamic Island** — kapsul asli (pilihan bawaan).
   - **HyperIsland** — "kaca" gelap gaya HyperOS: badan transparan, cahaya terarah, krom terang, garis cahaya di tepi atas.
   - Pilihan ada di **Оформление → Внешний вид острова** (tab "Оформление", paling atas).

## Apa yang digabung

- `data/Settings.kt` — enum `IslandStyle` (AKIRA / HYPERISLAND) + field `style`, `hyperHighlight`, `hyperLight`.
- `island/HyperSurface.kt` — baru: efek kaca gaya HyperIsland (`hyperSurface`, `hyperCard`).
- `island/IslandOverlay.kt` — permukaan island memilih gaya sesuai `style`.
- `shizuku/ShizukuBridge.kt` — baru: status Shizuku, deteksi root, deteksi HyperOS.
- `ui/screens/StyleScreen.kt` — baru: layar pilih gaya + opsi kaca + status akses sistem.
- `ui/MainActivity.kt` + `ui/screens/CapsuleScreen.kt` — navigasi dan pintu masuk layar baru.
- `app/build.gradle.kts` — dependensi Shizuku (`dev.rikka.shizuku:api`, `:provider`).
- `AndroidManifest.xml` — provider Shizuku (`${applicationId}.shizuku`).

## Catatan penting

- **Path HyperIsland (LSPosed/Xposed) tidak bisa di-port penuh** ke dalam satu APK ini tanpa dependensi HyperIsland asli (Miuix dari GitHub Packages butuh token, native lib HyperSpectrum, dan API libxposed). Aplikasi gabungan ini **sudah bekerja di HyperOS/Xiaomi tanpa root** lewat overlay aksesibilitas + listener notifikasi. Untuk intersepsi SystemUI ala HyperIsland (download manager, focus notification, dll.), tetap perlu modul Xposed tersendiri — itu di luar lingkup satu APK standalone.
- **Shizuku**: instal aplikasi Shizuku, aktifkan lewat ADB, lalu di **Оформление → Внешний вид острова** tekan "Запросить доступ Shizuku". Tanpa itu semua tetap jalan.
- Sumber dua proyek asli utuh di `Zip/` (tidak diubah): `Dynamic-Island--claude-android-dynamic-island-yrzsvq.zip` dan `HyperIsland-main.zip`.

## Cara build

Proyek standar Gradle Android (AGP 8.7.3, Kotlin 2.1.0, Compose, minSdk 26, targetSdk 35):

```bash
./gradlew assembleRelease   # atau assembleDebug
```
