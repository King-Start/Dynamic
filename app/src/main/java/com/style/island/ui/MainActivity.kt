package com.style.island.ui

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color as GColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.style.island.R
import com.style.island.core.LangScope
import com.style.island.core.tr
import com.style.island.data.SettingsRepo
import com.style.island.ui.screens.AnimScreen
import com.style.island.ui.screens.AppInfoScreen
import com.style.island.ui.screens.BannersScreen
import com.style.island.ui.screens.BanksScreen
import com.style.island.ui.screens.BehaviorScreen
import com.style.island.ui.screens.CapsuleScreen
import com.style.island.ui.screens.CardsScreen
import com.style.island.ui.screens.AppFilterScreen
import com.style.island.ui.screens.FeatureStylesScreen
import com.style.island.ui.screens.ColorsScreen
import com.style.island.ui.screens.DiagScreen
import com.style.island.ui.screens.EffectsScreen
import com.style.island.ui.screens.EventsScreen
import com.style.island.ui.screens.FeaturesScreen
import com.style.island.ui.screens.GesturesScreen
import com.style.island.ui.screens.HomeScreen
import com.style.island.ui.screens.IdeaScreen
import com.style.island.ui.screens.InstructionScreen
import com.style.island.ui.screens.KeepAliveScreen
import com.style.island.ui.screens.LookScreen
import com.style.island.ui.screens.LyricsScreen
import com.style.island.ui.screens.NotificationsScreen
import com.style.island.ui.screens.PayDiagScreen
import com.style.island.ui.screens.PermissionsScreen
import com.style.island.ui.screens.SizeScreen
import com.style.island.ui.screens.StyleScreen
import com.style.island.ui.screens.TestsScreen
import com.style.island.service.KeepAliveService
import com.style.island.update.UpdateChecker

enum class Screen {
    CARDS, EVENTS, GESTURES, LYRICS, SIZE, COLORS, INSTRUCTION, ABOUT, DIAG, LOOK, ANIM, EFFECTS, BANNERS,
    PERMISSIONS, TESTS, NOTIFICATIONS, BEHAVIOR, PAYDIAG, KEEPALIVE, BANKS, STYLE, APPS, FEATURE_STYLES,
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsRepo.init(this)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent { IslandTheme { AppRoot() } }
    }

    override fun onResume() {
        super.onResume()
        // «Не выгружать остров»: из фона Android может не запустить службу — запускаем, пока приложение открыто
        KeepAliveService.sync(this)
    }
}

@Composable
private fun AppRoot() {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var stack by remember { mutableStateOf(listOf<Screen>()) }
    var forward by remember { mutableStateOf(true) }
    val open: (Screen) -> Unit = {
        forward = true
        stack = stack + it
    }
    val back: () -> Unit = {
        forward = false
        stack = stack.dropLast(1)
    }
    BackHandler(enabled = stack.isNotEmpty(), onBack = back)

    // Раз в 3 часа спрашиваем GitHub, не вышла ли новая версия; после обновления — «Что нового»
    val context = LocalContext.current
    LaunchedEffect(Unit) { UpdateChecker.check(context.applicationContext, force = false) }
    // Сменили язык — экраны рисуются заново на нём, а открытая вкладка и экран остаются
    LangScope {
        WhatsNewDialog()

        Box(Modifier.fillMaxSize().background(Ui.Bg)) {
            val key = stack.lastOrNull()?.name ?: "tab$tab"
            AnimatedContent(
                targetState = key,
                transitionSpec = {
                    if (targetState.startsWith("tab") && initialState.startsWith("tab")) {
                        fadeIn(tween(220)) togetherWith fadeOut(tween(120))
                    } else if (forward) {
                        (slideInHorizontally { it / 4 } + fadeIn()) togetherWith (slideOutHorizontally { -it / 8 } + fadeOut())
                    } else {
                        (slideInHorizontally { -it / 8 } + fadeIn()) togetherWith (slideOutHorizontally { it / 4 } + fadeOut())
                    }
                },
                label = "nav",
            ) { k ->
                when (k) {
                    "tab0" -> HomeScreen(open)
                    "tab1" -> FeaturesScreen(open)
                    "tab2" -> CapsuleScreen(open)
                    "tab3" -> AppInfoScreen(open)
                    Screen.CARDS.name -> CardsScreen(back) { open(Screen.INSTRUCTION) }
                    Screen.EVENTS.name -> EventsScreen(back)
                    Screen.GESTURES.name -> GesturesScreen(back)
                    Screen.LYRICS.name -> LyricsScreen(back)
                    Screen.SIZE.name -> SizeScreen(back)
                    Screen.COLORS.name -> ColorsScreen(back)
                    Screen.INSTRUCTION.name -> InstructionScreen(back)
                    Screen.ABOUT.name -> IdeaScreen(back)
                    Screen.DIAG.name -> DiagScreen(back)
                    Screen.PAYDIAG.name -> PayDiagScreen(back)
                    Screen.KEEPALIVE.name -> KeepAliveScreen(back)
                    Screen.BANKS.name -> BanksScreen(back)
                    Screen.LOOK.name -> LookScreen(back)
                    Screen.ANIM.name -> AnimScreen(back)
                    Screen.EFFECTS.name -> EffectsScreen(back)
                    Screen.BANNERS.name -> BannersScreen(back)
                    Screen.PERMISSIONS.name -> PermissionsScreen(back, open)
                    Screen.TESTS.name -> TestsScreen(back)
                    Screen.NOTIFICATIONS.name -> NotificationsScreen(back)
                    Screen.BEHAVIOR.name -> BehaviorScreen(back)
                    Screen.STYLE.name -> StyleScreen(back, open)
                    Screen.APPS.name -> AppFilterScreen(back)
                    Screen.FEATURE_STYLES.name -> FeatureStylesScreen(back)
                }
            }

            AnimatedVisibility(
                visible = stack.isEmpty(),
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut(),
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                BottomBar(tab) { tab = it }
            }
        }
    }
}

/** Вкладки: главная, что показывает остров, как он выглядит, и о приложении — отдельно от настроек. */
private fun tabTitles() = listOf(tr("Главная", "Home"), tr("Функции", "Features"), tr("Оформление", "Appearance"), tr("О приложении", "About"))

/**
 * Navigasi bawah dengan pil "liquid glass": pil kaca mengikuti jari saat ditekan dan digeser,
 * membesar dan melar sedikit saat bergerak, ikon di bawahnya ikut membesar seperti lensa,
 * lalu menetap di tab terdekat saat dilepas.
 */
@Composable
private fun BottomBar(selected: Int, onSelect: (Int) -> Unit) {
    val titles = tabTitles()
    val icons = listOf(
        ImageVector.vectorResource(R.drawable.ic_tab_island),
        Icons.Rounded.Widgets,
        Icons.Rounded.Palette,
        Icons.Rounded.Info,
    )
    val count = titles.size
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    val barShape = RoundedCornerShape(32.dp)
    val barHeight = 60.dp
    val onSelectState by rememberUpdatedState(onSelect)

    var widthPx by remember { mutableFloatStateOf(0f) }
    val slot = if (widthPx > 0f) widthPx / count else 1f
    fun centerOf(i: Int) = slot * i + slot / 2f

    var pressed by remember { mutableStateOf(false) }
    var live by remember { mutableFloatStateOf(0f) }

    // Saat ditekan pil mengikuti jari dengan sedikit tertinggal (efek cairan); saat dilepas ia memantul ke tab terpilih
    val goal = if (pressed) live else centerOf(selected)
    val center by animateFloatAsState(
        targetValue = goal,
        animationSpec = when {
            widthPx == 0f -> snap()
            pressed -> spring(dampingRatio = 0.82f, stiffness = 900f)
            else -> spring(dampingRatio = 0.62f, stiffness = 420f)
        },
        label = "navCenter",
    )
    val press by animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 520f),
        label = "navPress",
    )
    val hovered = ((if (pressed) live else center) / slot).toInt().coerceIn(0, count - 1)
    LaunchedEffect(hovered, pressed) {
        if (pressed) haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }

    Box(
        Modifier
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            .widthIn(max = 480.dp)
            .fillMaxWidth()
            .shadow(18.dp, barShape, ambientColor = GColor.Black, spotColor = GColor.Black)
            .clip(barShape)
            .background(Brush.verticalGradient(listOf(GColor(0xFF25272D).copy(alpha = 0.95f), GColor(0xFF17181C).copy(alpha = 0.97f))))
            .border(1.dp, Brush.verticalGradient(listOf(GColor.White.copy(alpha = 0.16f), GColor.White.copy(alpha = 0.04f))), barShape)
            .padding(6.dp),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(barHeight)
                .onSizeChanged { widthPx = it.width.toFloat() }
                .pointerInput(count) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val w = size.width.toFloat()
                        live = down.position.x.coerceIn(0f, w)
                        pressed = true
                        do {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull { it.id == down.id }
                            if (change != null && change.pressed) {
                                live = change.position.x.coerceIn(0f, w)
                                change.consume()
                            }
                        } while (event.changes.any { it.pressed })
                        val index = (live / (w / count)).toInt().coerceIn(0, count - 1)
                        pressed = false
                        onSelectState(index)
                    }
                },
        ) {
            // Pil kaca: melebar dan sedikit melar mengikuti kecepatan gerak
            val lag = kotlin.math.abs(goal - center)
            val pillW = slot * 0.9f * (1f + 0.14f * press) + lag * 0.35f
            val pillH = barHeight - 8.dp + 6.dp * press
            GlassPill(
                press,
                Modifier
                    .offset { IntOffset((center - pillW / 2f).roundToInt(), ((barHeight.toPx() - pillH.toPx()) / 2f).roundToInt()) }
                    .size(with(density) { pillW.toDp() }, pillH),
            )

            Row(Modifier.fillMaxSize()) {
                titles.forEachIndexed { i, title ->
                    val active = if (pressed) hovered == i else selected == i
                    val tint by animateColorAsState(if (active) Ui.Text else Ui.Sub, tween(160), label = "tabTint")
                    // Ikon yang dekat dengan pil membesar seperti dilihat lewat lensa kaca
                    val proximity = (1f - kotlin.math.abs(centerOf(i) - center) / slot).coerceIn(0f, 1f)
                    val scale = 1f + 0.18f * press * proximity
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .semantics(mergeDescendants = true) {
                                role = Role.Tab
                                this.selected = i == selected
                                onClick { onSelectState(i); true }
                            },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            icons[i], null, tint = tint,
                            modifier = Modifier.size(24.dp).graphicsLayer { scaleX = scale; scaleY = scale },
                        )
                        Spacer(Modifier.height(3.dp))
                        TabLabel(title, tint, active)
                    }
                }
            }
        }
    }
}

/** Pil kaca: isi tipis ber-aksen, tepi terang di sudut atas, kilau di bagian atas, dan cahaya saat ditekan. */
@Composable
private fun GlassPill(press: Float, modifier: Modifier) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier
            .drawBehind {
                val g = 9.dp.toPx() * press
                if (g > 0.5f) {
                    drawRoundRect(
                        Ui.Primary.copy(alpha = 0.16f * press),
                        topLeft = Offset(-g, -g),
                        size = Size(size.width + 2 * g, size.height + 2 * g),
                        cornerRadius = CornerRadius(26.dp.toPx() + g),
                    )
                }
            }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(Ui.Primary.copy(alpha = 0.22f + 0.10f * press), Ui.Primary.copy(alpha = 0.07f + 0.06f * press)),
                ),
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(GColor.White.copy(alpha = 0.55f), GColor.White.copy(alpha = 0.08f), GColor.White.copy(alpha = 0.30f))),
                shape,
            )
            .drawBehind {
                // Kilau spekuler di bagian atas pil
                drawOval(
                    Brush.radialGradient(
                        listOf(GColor.White.copy(alpha = 0.26f + 0.18f * press), GColor.Transparent),
                        center = Offset(size.width * 0.32f, size.height * 0.14f),
                        radius = size.width * 0.5f,
                    ),
                    topLeft = Offset(size.width * 0.08f, 2.dp.toPx()),
                    size = Size(size.width * 0.84f, size.height * 0.46f),
                )
            },
    )
}

/** Подпись вкладки: если не влезает (крупный шрифт Android), чуть уменьшается, а не обрезается. */
@Composable
private fun TabLabel(text: String, color: androidx.compose.ui.graphics.Color, bold: Boolean) {
    val measurer = rememberTextMeasurer()
    val base = LocalTextStyle.current
    val weight = if (bold) FontWeight.SemiBold else FontWeight.Medium
    BoxWithConstraints(Modifier.fillMaxWidth().padding(horizontal = 2.dp), contentAlignment = Alignment.Center) {
        val room = constraints.maxWidth
        val size = remember(text, room, weight) {
            var sz = 12f
            while (sz > 8f && measurer.measure(text, base.copy(fontSize = sz.sp, fontWeight = weight), maxLines = 1).size.width > room) {
                sz -= 0.5f
            }
            sz.sp
        }
        Text(text, color = color, fontSize = size, fontWeight = weight, maxLines = 1, softWrap = false, style = base)
    }
}
