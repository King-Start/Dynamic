<p align="center">
  <img src="docs/logo.png" width="120" alt="Logo Dynamic Island">
</p>

<h1 align="center">Dynamic Island untuk Android</h1>

<p align="center">
Kapsul ala Dynamic Island di sekitar kamera HP Android mana pun, dengan tiga belas desain notifikasi, enam tata letak isi, dan beberapa gaya island.
</p>

<p align="center">
  <img alt="Android 8+" src="https://img.shields.io/badge/Android-8.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white">
  <img alt="Kotlin" src="https://img.shields.io/badge/Kotlin-2.1-7F52FF?style=flat-square&logo=kotlin&logoColor=white">
  <img alt="Jetpack Compose" src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=flat-square&logo=jetpackcompose&logoColor=white">
  <img alt="Tanpa iklan" src="https://img.shields.io/badge/iklan-0-black?style=flat-square">
</p>

[English](README.en.md)

## Fitur

Notifikasi dengan balasan · navigasi · pembayaran · musik dengan lirik sinkron · panggilan · timer · unduhan · pengisian baterai · senter · hotspot · cuaca · aplikasi dan kontak tersemat · Brief (jam, langkah, cuaca) · Liquid Glass · warna dan font kustom · event sistem.

## Desain notifikasi

Kartu notifikasi aplikasi punya tiga belas desain dengan bentuk yang berbeda, bukan sekadar efek: Klasik, Kartu, Ticker (teks berjalan di samping kamera), Obrolan, Spanduk, Terbelah, Judul besar, Sorotan, Tumpuk, Terminal, Lini masa, Hanya teks, dan Lencana. Balasan dan tombol aksi berfungsi di semua desain. Pilih lewat galeri di **Tampilan → Tema dan tata letak**, atau pakai **Tema siap pakai** untuk mengatur gaya, tata letak, dan notifikasi sekaligus.

## Tata letak dan gaya

Selain desain notifikasi, isi island punya enam tata letak: Klasik, Cincin, Berlabel, Dua baris, Garis progres, dan Mungil. Berlaku untuk musik, timer, stopwatch, jam, panggilan (termasuk kartu panggilan masuk), senter, unduhan, dan pengisian.

Gaya island: Standar (mengikuti pengaturan Warna dan bingkai, dengan Liquid Glass), HyperIsland, Neon, Gradien, iPhone, Aurora, Senja, dan Es Kristal.

## Instalasi

1. Unduh APK dari halaman **Releases** dan pasang.
2. Beri izin overlay, akses notifikasi, dan aksesibilitas sesuai petunjuk di aplikasi.
3. Aktifkan island di layar Beranda.

## Build

Proyek Gradle standar (AGP 8.7, Kotlin 2.1, Compose, minSdk 26):

```bash
./gradlew assembleRelease
```

GitHub Actions membangun APK di setiap push. Tanpa kunci, APK tetap dibuat dengan kunci sementara dan hanya jadi artefak (tidak dirilis, dan update antar-build mengharuskan uninstall dulu). Untuk rilis dan update yang mulus, buat kunci tetap sekali saja:

```bash
keytool -genkeypair -v -keystore island.p12 -storetype PKCS12 -alias island -keyalg RSA -keysize 2048 -validity 10000
base64 -w0 island.p12
```

Lalu di repo buka Settings → Secrets and variables → Actions dan isi secret `KEYSTORE_BASE64` (hasil `base64`) dan `KEYSTORE_PASSWORD` (kata sandi kunci). Simpan file `island.p12` di tempat aman: kalau hilang, update tidak bisa lagi dipasang di atas versi lama. Opsional: variabel repo `SIGNING_CERT_SHA256` untuk mengunci sidik jari sertifikat.

Untuk merilis versi baru, naikkan `versionName` dan `versionCode` di `app/build.gradle.kts` lalu tambahkan bagian `## [x.y.z]` di `CHANGELOG.md` dan `CHANGELOG.en.md`.

## Kredit

Proyek ini dikembangkan dari aplikasi sumber terbuka **Dynamic Island** oleh [Arusaky (dulu Akira9021)](https://github.com/Arusaky/Dynamic-Island-). Terima kasih kepada pembuat asli dan para kontributor. Komponen pihak ketiga: font [Inter](docs/licenses/Inter-OFL.txt) (OFL) dan ikon [Ionicons](docs/licenses/Ionicons-MIT.txt) (MIT); pemberitahuan lain ada di `app/src/main/assets/third-party`.

Logo aplikasi, set ikon, tema aplikasi, desain notifikasi, tata letak isi, serta gaya Neon, Gradien, iPhone, Aurora, Senja, dan Es Kristal dibuat khusus untuk versi ini.
