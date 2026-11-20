package com.style.island.island

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.style.island.data.IslandSettings
import com.style.island.data.NoticeDesign

/** Data satu notifikasi yang sudah siap tampil. Murni data, tanpa efek samping, jadi aman dipakai untuk pratinjau. */
class MsgView(
    val appName: String,
    val title: String,
    val text: String,
    val avatar: ImageBitmap?,
    val appIcon: ImageBitmap?,
    val fallback: ImageVector,
    val accent: Color,
    val count: Int = 1,
    val time: String,
)

private val Dim = Color.White.copy(alpha = 0.55f)

/** Foto, atau ikon aplikasi, atau ikon cadangan dalam bentuk [shape]. */
@Composable
private fun Pic(v: MsgView, size: Dp, shape: Shape) {
    val big = v.avatar ?: v.appIcon
    if (big != null) {
        Image(big, null, Modifier.size(size).clip(shape), contentScale = ContentScale.Crop)
    } else {
        Box(Modifier.size(size).clip(shape).background(Color(0xFF2C2E33)), contentAlignment = Alignment.Center) {
            Icon(v.fallback, null, tint = Color.White, modifier = Modifier.size(size * 0.5f))
        }
    }
}

/**
 * Isi kartu notifikasi dalam salah satu desain. [footer] adalah tempat tombol aksi atau kolom balasan;
 * desain menaruhnya di posisi yang pas dengan susunannya sendiri.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoticeDesignBody(
    design: NoticeDesign,
    v: MsgView,
    s: IslandSettings,
    width: Dp,
    footer: @Composable () -> Unit,
) {
    val gap = compactGap(s)
    val top = messageTop(s)
    val lines = s.msgLines.coerceIn(1, 8)

    when (design) {
        // Kartu asli: bilah aplikasi di sejajar kamera, di bawahnya foto bulat dan teks
        NoticeDesign.CLASSIC -> Column(Modifier.width(width).padding(bottom = 12.dp)) {
            Row(Modifier.fillMaxWidth().height(s.height.dp).padding(horizontal = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    if (v.appIcon != null) {
                        Image(v.appIcon, null, Modifier.size(15.dp).clip(RoundedCornerShape(4.dp)))
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(v.appName, color = Dim, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(gap))
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                    Text(v.time, color = Dim, fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }
            Spacer(Modifier.height((top - s.height.dp).coerceAtLeast(4.dp)))
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.Top) {
                Pic(v, 40.dp, CircleShape)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(v.title, color = Color.White, fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(v.text, color = Color.White.copy(alpha = 0.88f), fontSize = 14.sp, lineHeight = 19.sp, maxLines = lines, overflow = TextOverflow.Ellipsis)
                }
            }
            Box(Modifier.padding(start = 68.dp, end = 16.dp)) { footer() }
        }

        // Kartu: ikon kotak membulat besar, judul tebal, teks dua baris; aksi berupa tombol kapsul
        NoticeDesign.CARD -> Column(Modifier.width(width).padding(horizontal = 16.dp).padding(bottom = 14.dp)) {
            Spacer(Modifier.height(top))
            Row(verticalAlignment = Alignment.Top) {
                Pic(v, 48.dp, RoundedCornerShape(15.dp))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(v.appName, color = Dim, fontSize = 11.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        Text(v.time, color = Dim, fontSize = 11.5.sp, maxLines = 1, softWrap = false)
                    }
                    Text(v.title, color = Color.White, fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(v.text, color = Color.White.copy(alpha = 0.80f), fontSize = 14.sp, lineHeight = 19.sp, maxLines = lines, overflow = TextOverflow.Ellipsis)
                }
            }
            Box(Modifier.padding(start = 60.dp)) { footer() }
        }

        // Ticker: kapsul tipis; nama pengirim di kiri kamera, teks berjalan di kanan kamera
        NoticeDesign.TICKER -> Column(Modifier.width(width * 0.88f)) {
            Row(
                Modifier.fillMaxWidth().height(activeHeight(s) + 6.dp).padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Pic(v, 24.dp, CircleShape)
                    Spacer(Modifier.width(8.dp))
                    Text(v.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(gap))
                Box(Modifier.weight(1f).clipToBounds()) {
                    Text(
                        v.text.replace('\n', ' '),
                        color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, maxLines = 1, softWrap = false,
                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE, velocity = 40.dp),
                    )
                }
            }
            Box(Modifier.padding(start = 14.dp, end = 14.dp, bottom = 8.dp)) { footer() }
        }

        // Obrolan: pengirim di bilah kamera, pesan di dalam gelembung
        NoticeDesign.CHAT -> Column(Modifier.width(width).padding(bottom = 14.dp)) {
            Row(Modifier.fillMaxWidth().height(s.height.dp).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Pic(v, 26.dp, CircleShape)
                    Spacer(Modifier.width(8.dp))
                    Text(v.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.width(gap))
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                    Text(v.time, color = Dim, fontSize = 12.sp, maxLines = 1, softWrap = false)
                }
            }
            Spacer(Modifier.height((top - s.height.dp).coerceAtLeast(2.dp)))
            Box(Modifier.padding(horizontal = 14.dp)) {
                Box(
                    Modifier.clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 6.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    Text(v.text, color = Color.White.copy(alpha = 0.92f), fontSize = 15.sp, lineHeight = 20.sp, maxLines = lines, overflow = TextOverflow.Ellipsis)
                }
            }
            Box(Modifier.padding(start = 14.dp, end = 14.dp, top = 6.dp)) { footer() }
        }

        // Spanduk: garis warna di kiri, nama aplikasi berhuruf kapital berwarna aksen
        NoticeDesign.BANNER -> Column(Modifier.width(width).padding(horizontal = 18.dp).padding(bottom = 14.dp)) {
            Spacer(Modifier.height(top))
            Row(Modifier.height(IntrinsicSize.Min)) {
                Box(Modifier.width(4.dp).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(v.accent))
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            v.appName.uppercase(), color = v.accent, fontSize = 11.sp, letterSpacing = 0.8.sp,
                            fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f),
                        )
                        Text(v.time, color = Dim, fontSize = 11.5.sp, maxLines = 1, softWrap = false)
                    }
                    Text(v.title, color = Color.White, fontSize = 17.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(v.text, color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, lineHeight = 19.sp, maxLines = lines, overflow = TextOverflow.Ellipsis)
                    footer()
                }
            }
        }

        // Terbelah: blok warna aksen berisi ikon di kiri, teks di kanan
        NoticeDesign.SPLIT -> Column(Modifier.width(width).padding(horizontal = 14.dp).padding(bottom = 14.dp)) {
            Spacer(Modifier.height(top))
            Row(Modifier.height(IntrinsicSize.Min)) {
                Box(
                    Modifier.width(72.dp).fillMaxHeight().heightIn(min = 72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.verticalGradient(listOf(v.accent.copy(alpha = 0.95f), v.accent.copy(alpha = 0.55f)))),
                    contentAlignment = Alignment.Center,
                ) { Pic(v, 44.dp, RoundedCornerShape(14.dp)) }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f).align(Alignment.CenterVertically)) {
                    Text(v.title, color = Color.White, fontSize = 16.sp, lineHeight = 21.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(v.text, color = Color.White.copy(alpha = 0.80f), fontSize = 14.sp, lineHeight = 19.sp, maxLines = lines, overflow = TextOverflow.Ellipsis)
                    Text(v.appName + "  ·  " + v.time, color = Dim, fontSize = 11.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp))
                    footer()
                }
            }
        }

        // Judul besar: murni tipografi, tanpa ikon; garis aksen pendek sebagai penanda
        NoticeDesign.HEADLINE -> Column(Modifier.width(width).padding(horizontal = 22.dp).padding(bottom = 18.dp)) {
            Spacer(Modifier.height(top))
            Text(v.appName.uppercase() + "   ·   " + v.time, color = Dim, fontSize = 11.sp, letterSpacing = 1.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(6.dp))
            Text(v.title, color = Color.White, fontSize = 22.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Box(Modifier.width(28.dp).height(3.dp).clip(RoundedCornerShape(2.dp)).background(v.accent))
            Spacer(Modifier.height(8.dp))
            Text(v.text, color = Color.White.copy(alpha = 0.78f), fontSize = 15.sp, lineHeight = 21.sp, maxLines = lines, overflow = TextOverflow.Ellipsis)
            footer()
        }

        // Lencana: kapsul kecil berisi foto dan judul, plus jumlah pesan
        NoticeDesign.BADGE -> Row(
            Modifier.width(s.width.dp + 150.dp).height(activeHeight(s) + 4.dp).padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Pic(v, 26.dp, CircleShape)
            Spacer(Modifier.weight(1f))
            if (v.count > 1) {
                Box(
                    Modifier.clip(CircleShape).background(v.accent).padding(horizontal = 7.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(v.count.toString(), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(6.dp))
            }
            Text(
                v.title, color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold,
                maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 96.dp),
            )
        }
    }
}
