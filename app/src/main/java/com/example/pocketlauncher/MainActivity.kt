@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.example.pocketlauncher

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.app.role.RoleManager
import android.content.ComponentName
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationManagerCompat
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Xml
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.content.edit
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val launcherViewModel by viewModels<LauncherViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { PocketLauncherTheme { LauncherApp(launcherViewModel) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        launcherViewModel.refreshApps()
        launcherViewModel.requestHome()
    }

    override fun onResume() {
        super.onResume()
        launcherViewModel.refreshApps()
    }
}

enum class LauncherPage { Home, Drawer, Settings, Widgets }
enum class LanguageMode { System, Ukrainian, Russian, English }
enum class SettingsTab { Personalization, Interface, AppMenu, Permissions }

data class LauncherSettings(
    val showClock: Boolean = true,
    val showWeather: Boolean = true,
    val showAppCount: Boolean = true,
    val alignHomeRight: Boolean = false,
    val languageMode: LanguageMode = LanguageMode.System,
    val tileAlpha: Float = 0.76f,
    val tileColor: Color = DefaultTileColor,
    val customTileColor: Color = Color(0xFF4632A5),
    val tileStrokeEnabled: Boolean = true,
    val wallpaperKey: String = "warm_orange",
    val customWallpaperUris: List<String> = emptyList(),
    val iconPackPackage: String? = null,
    val homeIconScale: Float = 1.0f,
    val clockWidth: Float = 152f,
    val clockHeight: Float = 91f,
    val weatherWidth: Float = 228f,
    val weatherHeight: Float = 163f,
    val appWidgetSizes: Map<String, WidgetSize> = emptyMap(),
    val customFolders: Map<String, List<String>> = emptyMap(),
    val folderOrder: List<String> = emptyList(),
    val folderIconScale: Float = 1.0f,
)

data class WeatherReport(val temperature: Int, val condition: String, val location: String, val forecast: List<ForecastDay>)
data class ForecastDay(val day: String, val maxTemp: Int, val minTemp: Int)
data class WallpaperOption(val key: String, val label: String, val drawable: Int? = null, val uri: String? = null)
data class WidgetAppGroup(val packageName: String, val label: String, val icon: Drawable, val providers: List<AppWidgetProviderInfo>)
data class IconPackInfo(val packageName: String?, val label: String, val icon: Drawable?)
data class WidgetSize(val width: Float, val height: Float)
data class Copy(
    val apps: String,
    val back: String,
    val settings: String,
    val searchApps: String,
    val addedToHome: String,
    val showClock: String,
    val showWeather: String,
    val transparency: String,
    val language: String,
    val weatherLocation: String,
    val save: String,
    val cancel: String,
    val loadingWeather: String,
    val installed: (Int) -> String,
    val languageName: (LanguageMode) -> String,
    val mode: LanguageMode = LanguageMode.Ukrainian,
) {
    fun text(uk: String, en: String, ru: String = uk): String = when (if (mode == LanguageMode.System) LanguageMode.Ukrainian else mode) {
        LanguageMode.Ukrainian, LanguageMode.System -> uk
        LanguageMode.Russian -> ru
        LanguageMode.English -> en
    }
}

private data class WeatherCacheEntry(val location: String, val timestamp: Long, val report: WeatherReport)
data class AppFolder(val title: String, val apps: List<LauncherAppInfo>, val isNewApps: Boolean = false, val isCustom: Boolean = false)

private var weatherCache: WeatherCacheEntry? = null
private const val PREFS = "launcher_prefs"
private const val DEFAULT_LOCATION = "Бровари, Київська область, Україна"
private const val HOME_ICON_SCALE_MIN = 0.72f
private const val HOME_ICON_SCALE_MAX = 1.10f
private const val OPEN_FOLDER_ALPHA = 0.95f
private const val APP_WIDGET_HOST_ID = 2048
private const val APP_WIDGET_KEY_PREFIX = "APP_WIDGET:"
private const val HOME_SETTINGS_KEY = "LAUNCHER_SETTINGS"
private const val HOME_WIDGETS_KEY = "LAUNCHER_WIDGETS"
private val PROTECTED_HOME_KEYS = setOf(
    "WIDGET_CLOCK",
    "WIDGET_WEATHER",
)
private const val SYSTEM_RECENTS_START_WIDTH_DP = 108f
private const val SYSTEM_RECENTS_SWIPE_THRESHOLD_DP = 66f
private val DefaultTileColor = Color(0xFF756D2A)
private val WallpaperColor = Color(0xFF181510)
private val DefaultTileStroke = Color.White.copy(alpha = 0.78f)
private val LocalCopy = staticCompositionLocalOf { translations(LanguageMode.Ukrainian) }
private val LocalTileAlpha = staticCompositionLocalOf { 0.76f }
private val LocalTileColor = staticCompositionLocalOf { DefaultTileColor }
private val LocalTileStrokeEnabled = staticCompositionLocalOf { true }
private val LocalTileStrokeColor = staticCompositionLocalOf { DefaultTileStroke }
private val StandardFillColors = listOf(
    "Графітовий сірий" to Color(0xFF8C8E91),
    "Холодний попіл" to Color(0xFFA1A5A7),
    "Світлий туман" to Color(0xFFB8BFC0),
    "Сталевий" to Color(0xFF84909A),
    "Сланцевий" to Color(0xFF707C88),
)
private val LauncherColors: ColorScheme = darkColorScheme(
    primary = Color(0xFF0A84FF),
    onPrimary = Color.White,
    secondary = Color(0xFF64D2FF),
    onSecondary = Color(0xFF001D2B),
    background = Color(0xFF0B0B10),
    onBackground = Color(0xFFF7F7FA),
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xFFF7F7FA),
    onSurfaceVariant = Color(0xFFD1D1D6),
)
private val iconPackFilterCache = mutableMapOf<String, Map<String, String>>()
private val themedIconCache = mutableMapOf<String, Drawable?>()
private val appIconBitmapCache = mutableMapOf<String, androidx.compose.ui.graphics.ImageBitmap>()
private var iconPackListCache: List<IconPackInfo>? = null
private var widgetGroupListCache: List<WidgetAppGroup>? = null
private val widgetPreviewCache = mutableMapOf<String, Drawable?>()
private val iconCacheLock = Any()
private val DrawerPageAnimationSpec = tween<Float>(durationMillis = 170, easing = FastOutSlowInEasing)
private val PageFadeInSpec = tween<Float>(durationMillis = 110, delayMillis = 0)
private val PageFadeOutSpec = tween<Float>(durationMillis = 80, delayMillis = 0)
@Suppress("DEPRECATION")
@Composable
private fun PocketLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LauncherColors,
        content = content,
    )
}

@Composable
private fun Modifier.noPressClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = clickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = enabled,
    onClick = onClick,
)

@Composable
private fun Modifier.noPressCombinedClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
): Modifier = combinedClickable(
    interactionSource = remember { MutableInteractionSource() },
    indication = null,
    enabled = enabled,
    onClick = onClick,
    onLongClick = onLongClick,
)

private suspend fun Animatable<Float, AnimationVector1D>.playTapPulse(onClick: () -> Unit) {
    stop()
    animateTo(0.94f, tween(durationMillis = 65))
    animateTo(1f, tween(durationMillis = 105, easing = FastOutSlowInEasing))
    onClick()
}

@Composable
fun LauncherApp(viewModel: LauncherViewModel) {
    val context = LocalContext.current
    val view = LocalView.current
    val density = LocalDensity.current
    val apps by viewModel.apps.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val recentApps by viewModel.recentApps.collectAsStateWithLifecycle()
    val notificationPackages by NotificationService.activeNotificationPackages.collectAsStateWithLifecycle()
    var page by remember { mutableStateOf(LauncherPage.Home) }
    var editMode by remember { mutableStateOf(false) }
    var settings by remember { mutableStateOf(loadLauncherSettings(context)) }
    var weatherLocation by remember { mutableStateOf(loadWeatherLocation(context)) }
    val homeAppKeys = remember { mutableStateListOf<String>() }
    val newAppKeys = remember { mutableStateListOf<String>() }
    val hiddenAppKeys = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        homeAppKeys.clear()
        homeAppKeys.addAll(loadHomeAppKeys(context))
        hiddenAppKeys.clear()
        hiddenAppKeys.addAll(loadStringSet(context, "hidden_apps"))
    }

    LaunchedEffect(Unit) {
        viewModel.homeRequests.collect {
            page = LauncherPage.Home
            viewModel.setQuery("")
        }
    }

    LaunchedEffect(apps) {
        val keys = apps.map { it.componentKey() }.toSet()
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val savedHome = prefs.getStringSet("home_apps", null)
        if (savedHome == null && apps.isNotEmpty()) {
            val defaultKeys = defaultHomeKeys(apps)
            if (defaultKeys.isNotEmpty()) {
                homeAppKeys.clear()
                homeAppKeys.addAll(defaultKeys)
                saveHomeAppKeys(context, defaultKeys)
            }
        }
        val known = prefs.getStringSet("known_apps", null)
        if (known == null) {
            prefs.edit { putStringSet("known_apps", keys) }
            newAppKeys.clear()
        } else {
            newAppKeys.clear()
            newAppKeys.addAll((keys - known).sorted())
        }
    }

    LaunchedEffect(page) {
        if (page != LauncherPage.Home) editMode = false
    }

    val editGestureExclusionPx = with(density) {
        64.dp.toPx().roundToInt()
    }
    DisposableEffect(editMode, view, editGestureExclusionPx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val updateExclusion = {
                val width = view.width
                val height = view.height
                if (editMode && width > 0 && height > 0) {
                    val edge = editGestureExclusionPx.coerceAtMost(width)
                    // На старих API (Android 10-12) є ліміт 200dp для жестів назад,
                    // тому ми намагаємось покрити якомога більше, але система може обрізати.
                    // Для Samsung Android 12 важливо оновлювати ректи при зміні лейауту.
                    val rects = listOf(
                        Rect(0, 0, edge, height),
                        Rect((width - edge).coerceAtLeast(0), 0, width, height),
                        Rect(0, (height - edge).coerceAtLeast(0), width, height),
                    )
                    view.systemGestureExclusionRects = rects
                } else {
                    view.systemGestureExclusionRects = emptyList()
                }
            }

            val listener = android.view.View.OnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                updateExclusion()
            }
            
            view.addOnLayoutChangeListener(listener)
            view.post { updateExclusion() }
            
            onDispose {
                view.removeOnLayoutChangeListener(listener)
                runCatching { view.systemGestureExclusionRects = emptyList() }
            }
        } else {
            onDispose { }
        }
    }

    BackHandler(enabled = editMode) {
        // У режимі редагування ігноруємо системний Back, включно з жестом від краю.
    }

    val copy = translations(settings.languageMode)
    val saveHomeApps = { saveHomeAppKeys(context, homeAppKeys) }
    val saveHiddenApps = { saveStringSet(context, "hidden_apps", hiddenAppKeys.toSet()) }
    val saveSettings: (LauncherSettings) -> Unit = {
        settings = it
        saveLauncherSettings(context, it)
    }
    val removeHomeItem: (String) -> Unit = removeHomeItem@ { key ->
        val isLauncherIcon = apps.any { app ->
            app.componentKey() == key && app.packageName == context.packageName
        }
        if (key in PROTECTED_HOME_KEYS || isLauncherIcon) return@removeHomeItem
        homeAppKeys.remove(key)
        deleteHomeWidget(context, key)
        if (key.startsWith(APP_WIDGET_KEY_PREFIX)) {
            saveSettings(settings.copy(appWidgetSizes = settings.appWidgetSizes - key))
        }
        saveHomeApps()
    }
    val resetHomeScreen = {
        val defaultKeys = defaultHomeKeys(apps)
        homeAppKeys.clear()
        homeAppKeys.addAll(defaultKeys)
        saveHomeAppKeys(context, defaultKeys)
        saveSettings(
            settings.copy(
                showClock = true,
                showWeather = true,
                alignHomeRight = false,
                clockWidth = 304f,
                clockHeight = 182f,
                weatherWidth = 456f,
                weatherHeight = 326f,
            ),
        )
    }

    BackHandler(enabled = !editMode && page != LauncherPage.Home) {
        page = LauncherPage.Home
        viewModel.setQuery("")
    }

    val systemRecentsStartWidthPx = with(density) {
        SYSTEM_RECENTS_START_WIDTH_DP.dp.toPx()
    }

    @Composable
    fun HomePage() {
        HomeScreen(
            apps = apps,
            homeAppKeys = homeAppKeys,
            settings = settings,
            copy = copy,
            editMode = editMode,
            onEditModeChange = { editMode = it },
            weatherLocation = weatherLocation,
            notificationPackages = notificationPackages,
            onWeatherLocationChange = { weatherLocation = it; saveWeatherLocation(context, it) },
            onOpenDrawer = { },
            onOpenRecents = { openSystemRecents(context) },
            onOpenSettings = { page = LauncherPage.Settings },
            onOpenWidgets = { page = LauncherPage.Widgets },
            onRemoveApp = removeHomeItem,
            onLaunchApp = { app ->
                viewModel.addToRecent(app)
                launchApp(context, app)
            },
            onSettingsChange = saveSettings,
            onHomeAppsChange = saveHomeApps,
        )
    }

    @Composable
    fun DrawerPage(backHandlerEnabled: Boolean) {
        AppDrawer(
            apps = apps,
            query = query,
            copy = copy,
            showAppCount = settings.showAppCount,
            hiddenAppKeys = hiddenAppKeys.toSet(),
            newAppKeys = newAppKeys.toSet(),
            customFolders = settings.customFolders,
            folderOrder = settings.folderOrder,
            notificationPackages = notificationPackages,
            recentApps = recentApps,
            onQueryChange = viewModel::setQuery,
            onBack = { page = LauncherPage.Home; viewModel.setQuery("") },
            onAppClick = { app ->
                viewModel.addToRecent(app)
                launchApp(context, app)
                page = LauncherPage.Home
                viewModel.setQuery("")
            },
            onAddToHome = { app ->
                val key = app.componentKey()
                if (!homeAppKeys.contains(key)) {
                    homeAppKeys.add(key)
                    saveHomeApps()
                }
                Toast.makeText(context, copy.addedToHome, Toast.LENGTH_SHORT).show()
            },
            onHideApp = { app ->
                val key = app.componentKey()
                if (!hiddenAppKeys.contains(key)) {
                    hiddenAppKeys.add(key)
                    saveHiddenApps()
                }
                homeAppKeys.remove(key)
                saveHomeApps()
            },
            onUninstall = { requestAppUninstall(context, it) },
            iconPackPackage = settings.iconPackPackage,
            onMarkNewSeen = {
                saveStringSet(context, "known_apps", apps.map { app -> app.componentKey() }.toSet())
                newAppKeys.clear()
            },
            onUpdateFolderApps = { folderName, selectedKeys ->
                val nextCustomFolders = settings.customFolders.toMutableMap()
                nextCustomFolders.keys.forEach { name ->
                    if (name != folderName) {
                        nextCustomFolders[name] = nextCustomFolders[name]?.filterNot { it in selectedKeys } ?: emptyList()
                    }
                }
                nextCustomFolders[folderName] = selectedKeys.toList()
                saveSettings(settings.copy(customFolders = nextCustomFolders))
            },
            allApps = apps,
            folderIconScale = settings.folderIconScale,
            backHandlerEnabled = backHandlerEnabled,
        )
    }

    Box(Modifier.fillMaxSize().background(WallpaperColor)) {
        LauncherWallpaper(settings.wallpaperKey)
        CompositionLocalProvider(
            LocalCopy provides copy,
            LocalTileAlpha provides settings.tileAlpha,
            LocalTileColor provides settings.tileColor,
            LocalTileStrokeEnabled provides settings.tileStrokeEnabled,
            LocalTileStrokeColor provides adaptiveTileStroke(settings.wallpaperKey),
        ) {
            if (page == LauncherPage.Home || page == LauncherPage.Drawer) {
                var drawerProgress by remember { mutableStateOf(if (page == LauncherPage.Drawer) 1f else 0f) }
                val drawerAnimatable = remember { Animatable(drawerProgress) }
                val drawerAnimationScope = rememberCoroutineScope()
                BoxWithConstraints(
                    Modifier
                        .fillMaxSize()
                ) {
                    val widthPx = with(LocalDensity.current) { maxWidth.toPx() }

                    LaunchedEffect(page, widthPx) {
                        val target = if (page == LauncherPage.Drawer) 1f else 0f
                        if (widthPx > 0f && drawerProgress != target) {
                            drawerAnimatable.snapTo(drawerProgress)
                            drawerAnimatable.animateTo(
                                target,
                                DrawerPageAnimationSpec,
                            ) {
                                drawerProgress = value
                            }
                        }
                    }

                    Box(
                        Modifier
                            .fillMaxSize()
                            .pointerInput(page, widthPx, editMode) {
                                if (widthPx <= 0f || editMode) return@pointerInput
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
                                    if (page == LauncherPage.Home && down.position.x <= systemRecentsStartWidthPx) {
                                        return@awaitEachGesture
                                    }

                                    var horizontalDistance = 0f
                                    var verticalDistance = 0f
                                    var horizontalGesture = false
                                    val startProgress = drawerProgress

                                    while (true) {
                                        val event = awaitPointerEvent(PointerEventPass.Initial)
                                        val change = event.changes.firstOrNull { pointer -> pointer.id == down.id } ?: break

                                        if (change.changedToUpIgnoreConsumed()) {
                                            if (!horizontalGesture) {
                                                break
                                            }
                                            val target = if (
                                                (drawerProgress >= 0.5f || horizontalDistance < -widthPx * 0.16f)
                                            ) {
                                                1f
                                            } else {
                                                0f
                                            }
                                            drawerAnimationScope.launch {
                                                drawerAnimatable.snapTo(drawerProgress)
                                                drawerAnimatable.animateTo(
                                                    target,
                                                    DrawerPageAnimationSpec,
                                                ) {
                                                    drawerProgress = value
                                                }
                                                page = if (target == 1f) LauncherPage.Drawer else LauncherPage.Home
                                                if (target == 0f) viewModel.setQuery("")
                                            }
                                            break
                                        }

                                        val delta = change.positionChange()
                                        horizontalDistance += delta.x
                                        verticalDistance += delta.y

                                        if (!horizontalGesture && abs(horizontalDistance) > 12f && abs(horizontalDistance) > abs(verticalDistance) * 1.25f) {
                                            horizontalGesture = page == LauncherPage.Home && horizontalDistance < 0f ||
                                                page == LauncherPage.Drawer && horizontalDistance > 0f
                                        }

                                        if (horizontalGesture) {
                                            val nextProgress = (startProgress - horizontalDistance / widthPx).coerceIn(0f, 1f)
                                            drawerProgress = nextProgress
                                            change.consume()
                                        }
                                    }
                                }
                            },
                    ) {
                        val progress = drawerProgress
                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer { translationX = -progress * widthPx },
                        ) {
                            HomePage()
                        }
                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer { translationX = (1f - progress) * widthPx },
                        ) {
                            DrawerPage(backHandlerEnabled = page == LauncherPage.Drawer)
                        }
                    }
                }
            } else {
                AnimatedContent(
                    targetState = page,
                    transitionSpec = {
                        fadeIn(PageFadeInSpec) togetherWith fadeOut(PageFadeOutSpec)
                    },
                    label = "launcher_page_fade",
                ) { targetPage ->
                    when (targetPage) {
                        LauncherPage.Settings -> SettingsScreen(
                            settings = settings,
                            copy = copy,
                            onSettingsChange = saveSettings,
                            onResetHomeScreen = resetHomeScreen,
                            hiddenApps = apps.filter { it.componentKey() in hiddenAppKeys },
                            onUnhideApp = { app ->
                                hiddenAppKeys.remove(app.componentKey())
                                saveHiddenApps()
                            },
                            onBack = { page = LauncherPage.Home },
                            onDefaultLauncher = { openDefaultLauncherSettings(context) },
                            onNotificationAccess = { context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) },
                            onAccessibility = { context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) },
                            allApps = apps,
                            newAppKeys = newAppKeys.toSet(),
                        )
                        LauncherPage.Widgets -> WidgetPickerScreen(
                            copy = copy,
                            alpha = settings.tileAlpha,
                            onWidgetAdded = { widgetId ->
                                val key = appWidgetKey(widgetId)
                                if (!homeAppKeys.contains(key)) {
                                    homeAppKeys.add(key)
                                    saveHomeApps()
                                }
                                page = LauncherPage.Home
                            },
                            onBack = { page = LauncherPage.Home },
                        )
                        else -> HomePage()
                    }
                }
            }
        }
    }
}

@Composable
fun LauncherWallpaper(wallpaperKey: String) {
    val customUri = customWallpaperUri(wallpaperKey)
    val customPainter = customUri?.let { rememberUriPainter(it) }
    Image(
        painter = customPainter ?: painterResource(wallpaperDrawable(wallpaperKey)),
        contentDescription = null,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun HomeScreen(
    apps: List<LauncherAppInfo>,
    homeAppKeys: MutableList<String>,
    settings: LauncherSettings,
    copy: Copy,
    editMode: Boolean,
    onEditModeChange: (Boolean) -> Unit,
    weatherLocation: String,
    notificationPackages: Set<String>,
    onWeatherLocationChange: (String) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenRecents: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenWidgets: () -> Unit,
    onRemoveApp: (String) -> Unit,
    onLaunchApp: (LauncherAppInfo) -> Unit,
    onSettingsChange: (LauncherSettings) -> Unit,
    onHomeAppsChange: () -> Unit,
) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var homeMenuPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    val density = LocalDensity.current
    val openDrawerDistance = with(density) { 88.dp.toPx() }
    val openNotificationsDistance = with(density) { 64.dp.toPx() }
    val systemRecentsStartWidthPx = with(density) { SYSTEM_RECENTS_START_WIDTH_DP.dp.toPx() }
    val systemRecentsSwipeThresholdPx = with(density) { SYSTEM_RECENTS_SWIPE_THRESHOLD_DP.dp.toPx() }
    val lazyListState = rememberLazyListState()
    val dragDropState = remember { mutableStateOf<DragDropData?>(null) }

    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(
                lazyListState,
                editMode,
                openDrawerDistance,
                openNotificationsDistance,
                systemRecentsStartWidthPx,
                systemRecentsSwipeThresholdPx,
            ) {
                awaitEachGesture {
                    val down = awaitFirstDown(
                        requireUnconsumed = false,
                        pass = PointerEventPass.Initial,
                    )
                    val startedAtTop = !editMode &&
                        lazyListState.firstVisibleItemIndex == 0 &&
                        lazyListState.firstVisibleItemScrollOffset == 0
                    val startedFromLeftSide = !editMode && down.position.x <= systemRecentsStartWidthPx
                    var pullDistance = 0f
                    var horizontalDistance = 0f
                    var verticalDistance = 0f
                    var openedForGesture = false

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { pointer ->
                            pointer.id == down.id
                        } ?: break

                        if (change.changedToUpIgnoreConsumed()) break
                        if (editMode) continue

                        val delta = change.positionChange()
                        horizontalDistance += delta.x
                        verticalDistance += delta.y

                        if (
                            startedFromLeftSide &&
                            !openedForGesture &&
                            horizontalDistance >= systemRecentsSwipeThresholdPx &&
                            abs(horizontalDistance) > abs(verticalDistance) * 1.35f
                        ) {
                            openedForGesture = true
                            onOpenRecents()
                            change.consume()
                        }

                        if (
                            !openedForGesture &&
                            horizontalDistance <= -openDrawerDistance &&
                            abs(horizontalDistance) > abs(verticalDistance) * 1.35f
                        ) {
                            openedForGesture = true
                            onOpenDrawer()
                            change.consume()
                        }

                        if (startedAtTop) {
                            val isStillAtTop = lazyListState.firstVisibleItemIndex == 0 &&
                                lazyListState.firstVisibleItemScrollOffset == 0
                            val deltaY = delta.y

                            if (deltaY > 0f && isStillAtTop) {
                                pullDistance += deltaY
                                if (!openedForGesture && pullDistance >= openNotificationsDistance) {
                                    openedForGesture = true
                                    if (!openNotificationShade(context)) {
                                        Toast.makeText(context, "Enable accessibility permission for the notification gesture", Toast.LENGTH_SHORT).show()
                                    }
                                    change.consume()
                                }
                            } else if (deltaY < 0f) {
                                pullDistance = 0f
                            }
                        }
                    }
                }
            },
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(editMode) {
                    detectTapGestures(
                        onTap = { if (editMode) onEditModeChange(false) },
                        onLongPress = { pressOffset ->
                            if (!editMode) {
                                homeMenuPosition = pressOffset
                                showMenu = true
                            }
                        },
                    )
                },
            state = lazyListState,
            contentPadding = PaddingValues(top = 51.dp, bottom = 99.dp, start = 26.dp, end = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = if (settings.alignHomeRight) Alignment.End else Alignment.Start,
        ) {
            itemsIndexed(
                homeAppKeys,
                key = { _, key -> key },
                contentType = { _, key ->
                    if (key.startsWith(APP_WIDGET_KEY_PREFIX) || key.startsWith("WIDGET_")) "home_widget" else "home_app"
                },
            ) { _, key ->
                val isDragging = dragDropState.value?.key == key
                val offset = if (isDragging) dragDropState.value?.offset ?: 0f else 0f
                
                Box(
                    modifier = Modifier
                        .zIndex(if (isDragging) 10f else 1f)
                        .graphicsLayer { translationY = offset }
                        .then(if (isDragging) Modifier else Modifier.animateItem())
                        .pointerInput(editMode, key) {
                            if (!editMode) return@pointerInput
                            val reorderStep = with(density) { 96.dp.toPx() }
                            detectDragGestures(
                                onDragStart = { dragDropState.value = DragDropData(key) },
                                onDragEnd = {
                                    dragDropState.value = null
                                    onHomeAppsChange()
                                },
                                onDragCancel = { dragDropState.value = null }
                            ) { change, dragAmount ->
                                change.consume()
                                val current = dragDropState.value ?: return@detectDragGestures
                                var nextOffset = current.offset + dragAmount.y
                                var currentIndex = homeAppKeys.indexOf(current.key)
                                if (currentIndex == -1) return@detectDragGestures

                                while (nextOffset > reorderStep && currentIndex < homeAppKeys.lastIndex) {
                                    val item = homeAppKeys.removeAt(currentIndex)
                                    homeAppKeys.add(currentIndex + 1, item)
                                    currentIndex += 1
                                    nextOffset -= reorderStep
                                }
                                while (nextOffset < -reorderStep && currentIndex > 0) {
                                    val item = homeAppKeys.removeAt(currentIndex)
                                    homeAppKeys.add(currentIndex - 1, item)
                                    currentIndex -= 1
                                    nextOffset += reorderStep
                                }

                                dragDropState.value = current.copy(offset = nextOffset)
                            }
                        }
                ) {
                    when {
                        key == "WIDGET_CLOCK" -> if (settings.showClock) {
                            ClockWidget(
                                width = settings.clockWidth,
                                height = settings.clockHeight,
                                alpha = settings.tileAlpha,
                                editMode = editMode,
                                onResize = { width, height ->
                                    onSettingsChange(settings.copy(
                                        clockWidth = width.coerceIn(220f, 520f),
                                        clockHeight = height.coerceIn(112f, 320f)
                                    ))
                                },
                            )
                        }
                        key == "WIDGET_WEATHER" -> if (settings.showWeather) {
                            WeatherWidget(
                                location = weatherLocation,
                                copy = copy,
                                alpha = settings.tileAlpha,
                                editMode = editMode,
                                width = settings.weatherWidth,
                                height = settings.weatherHeight,
                                onResize = { width, height ->
                                    onSettingsChange(settings.copy(
                                        weatherWidth = width.coerceIn(280f, 560f),
                                        weatherHeight = height.coerceIn(180f, 430f)
                                    ))
                                },
                                onLocationChange = onWeatherLocationChange,
                            )
                        }
                        key.startsWith(APP_WIDGET_KEY_PREFIX) -> {
                            AppWidgetHomeItem(
                                key = key,
                                size = settings.appWidgetSizes[key],
                                editMode = editMode,
                                onResize = { width, height ->
                                    onSettingsChange(
                                        settings.copy(
                                            appWidgetSizes = settings.appWidgetSizes + (key to WidgetSize(width, height)),
                                        ),
                                    )
                                },
                                onRemove = { onRemoveApp(key) },
                            )
                        }
                        key == HOME_SETTINGS_KEY -> {
                            LauncherActionTile(
                                label = copy.settings,
                                alpha = settings.tileAlpha,
                                iconScale = settings.homeIconScale,
                                editMode = editMode,
                                onClick = onOpenSettings,
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                        key == HOME_WIDGETS_KEY -> {
                            LauncherActionTile(
                                label = copy.text("Додати віджет", "Add widget", "Добавить виджет"),
                                alpha = settings.tileAlpha,
                                iconScale = settings.homeIconScale,
                                editMode = editMode,
                                onClick = onOpenWidgets,
                            ) {
                                Text(
                                    text = "+",
                                    color = Color.White,
                                    fontSize = 44.sp,
                                    fontWeight = FontWeight.Normal,
                                    lineHeight = 44.sp,
                                )
                            }
                        }
                        else -> {
                            val app = apps.firstOrNull { it.componentKey() == key }
                            if (app != null) {
                                AppTile(
                                    app,
                                    settings.tileAlpha,
                                    settings.iconPackPackage,
                                    settings.homeIconScale,
                                    editMode,
                                    canRemove = app.packageName != context.packageName,
                                    hasNotification = app.packageName in notificationPackages,
                                    onClick = { onLaunchApp(app) },
                                    onRemove = { onRemoveApp(key) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (showMenu) {
            HomeContextMenu(
                copy = copy,
                alpha = settings.tileAlpha,
                position = homeMenuPosition,
                onDismiss = { showMenu = false },
                onWidgets = { showMenu = false; onOpenWidgets() },
                onSettings = { showMenu = false; onOpenSettings() },
                onEdit = { showMenu = false; onEditModeChange(true) },
            )
        }

    }
}

@Composable
fun HomeContextMenu(copy: Copy, alpha: Float, position: androidx.compose.ui.geometry.Offset, onDismiss: () -> Unit, onWidgets: () -> Unit, onSettings: () -> Unit, onEdit: () -> Unit) {
    val density = LocalDensity.current
    var menuSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .noPressClickable(onClick = onDismiss),
    ) {
        val scope = this
        val edgePaddingPx = with(density) { 8.dp.toPx() }
        val maxWidthPx = with(density) { scope.maxWidth.toPx() }
        val maxHeightPx = with(density) { scope.maxHeight.toPx() }

        Column(
            modifier = Modifier
                .onSizeChanged { menuSize = it }
                .offset {
                    val menuWidth = menuSize.width.toFloat()
                    val menuHeight = menuSize.height.toFloat()
                    
                    val maxX = (maxWidthPx - menuWidth - edgePaddingPx).coerceAtLeast(edgePaddingPx)
                    val maxY = (maxHeightPx - menuHeight - edgePaddingPx).coerceAtLeast(edgePaddingPx)
                    
                    val targetX = if (position.x > maxWidthPx / 2) {
                        position.x - menuWidth
                    } else {
                        position.x
                    }

                    IntOffset(
                        x = targetX.coerceIn(edgePaddingPx, maxX).roundToInt(),
                        y = position.y.coerceIn(edgePaddingPx, maxY).roundToInt(),
                    )
                },
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = if (position.x > maxWidthPx / 2) Alignment.End else Alignment.Start
        ) {
            HomeMenuPill(copy.text("Додати віджет", "Add widget", "Добавить виджет"), alpha, onWidgets)
            HomeMenuPill(copy.settings, alpha, onSettings)
            HomeMenuPill(copy.text("Редагувати", "Edit", "Редактировать"), alpha, onEdit)
        }
    }
}

@Composable
fun HomeMenuPill(text: String, alpha: Float, onClick: () -> Unit) {
    TileContainer(
        modifier = Modifier
            .wrapContentWidth()
            .height(38.dp)
            .noPressClickable(onClick = onClick),
        alpha = alpha,
    ) {
        Box(Modifier.height(38.dp).padding(horizontal = 15.dp), contentAlignment = Alignment.CenterStart) {
            Text(text, color = Color.White, fontSize = 16.sp, maxLines = 1)
        }
    }
}

@Composable
fun WidgetPickerScreen(copy: Copy, alpha: Float, onWidgetAdded: (Int) -> Unit, onBack: () -> Unit) {
    val context = LocalContext.current
    val appWidgetManager = remember(context) { AppWidgetManager.getInstance(context) }
    val appWidgetHost = remember(context) { AppWidgetHost(context, APP_WIDGET_HOST_ID) }
    val cachedWidgetGroups = widgetGroupListCache
    val widgetGroupsState by produceState(
        initialValue = (cachedWidgetGroups ?: emptyList()) to (cachedWidgetGroups != null),
        key1 = context,
    ) {
        value = withContext(Dispatchers.IO) {
            val groups = loadWidgetGroups(context.applicationContext)
            widgetGroupListCache = groups
            groups to true
        }
    }
    val widgetGroups = widgetGroupsState.first
    val widgetGroupsLoaded = widgetGroupsState.second
    var expandedPackage by remember { mutableStateOf<String?>(null) }
    var pendingWidgetId by remember { mutableStateOf<Int?>(null) }
    var pendingProvider by remember { mutableStateOf<AppWidgetProviderInfo?>(null) }
    fun completeWidgetAdd(widgetId: Int) {
        onWidgetAdded(widgetId)
        Toast.makeText(context, copy.text("Віджет додано на головний екран", "Widget added to home screen", "Виджет добавлен на главный экран"), Toast.LENGTH_SHORT).show()
    }
    val configureWidgetLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val widgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId ?: -1) ?: pendingWidgetId
        if (result.resultCode == Activity.RESULT_OK && widgetId != null && widgetId != -1) {
            completeWidgetAdd(widgetId)
        } else {
            widgetId?.takeIf { it != -1 }?.let { appWidgetHost.deleteAppWidgetId(it) }
        }
        pendingWidgetId = null
        pendingProvider = null
    }
    fun startConfigureOrComplete(widgetId: Int, provider: AppWidgetProviderInfo) {
        if (provider.configure != null) {
            pendingWidgetId = widgetId
            pendingProvider = provider
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                component = provider.configure
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
            configureWidgetLauncher.launch(intent)
        } else {
            completeWidgetAdd(widgetId)
        }
    }
    val bindWidgetLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val widgetId = pendingWidgetId
        val provider = pendingProvider
        if (result.resultCode == Activity.RESULT_OK && widgetId != null && provider != null) {
            startConfigureOrComplete(widgetId, provider)
        } else {
            widgetId?.let { appWidgetHost.deleteAppWidgetId(it) }
        }
        pendingWidgetId = null
        pendingProvider = null
    }
    fun addWidget(provider: AppWidgetProviderInfo) {
        val widgetId = appWidgetHost.allocateAppWidgetId()
        val bound = runCatching {
            appWidgetManager.bindAppWidgetIdIfAllowed(widgetId, provider.provider)
        }.getOrDefault(false)
        if (bound) {
            startConfigureOrComplete(widgetId, provider)
        } else {
            pendingWidgetId = widgetId
            pendingProvider = provider
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, provider.provider)
            }
            bindWidgetLauncher.launch(intent)
        }
    }
    BackHandler { onBack() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(start = 20.dp, top = 21.dp, end = 20.dp, bottom = 21.dp),
        contentPadding = PaddingValues(bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        item(key = "widget_header", contentType = "widget_header") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFF202126).copy(alpha = LocalTileAlpha.current))
                        .noPressClickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(copy.text("Додати віджет", "Add widget", "Добавить виджет"), color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        item(key = "widget_header_gap", contentType = "widget_gap") {
            Spacer(Modifier.height(6.dp))
        }
        items(widgetGroups, key = { it.packageName }, contentType = { "widget_group" }) { group ->
            val expanded = expandedPackage == group.packageName
            WidgetProviderRow(
                group = group,
                alpha = alpha,
                expanded = expanded,
                onClick = { expandedPackage = if (expanded) null else group.packageName },
            )
            if (expanded) {
                WidgetProviderPreview(
                    group = group,
                    alpha = alpha,
                    onClick = { provider -> addWidget(provider) },
                )
            }
        }
        if (!widgetGroupsLoaded && widgetGroups.isEmpty()) {
            item(key = "widget_loading", contentType = "widget_state") {
                SettingsCard(copy.text("Завантаження віджетів", "Loading widgets", "Загрузка виджетов"), copy.text("Список зʼявиться за мить", "The list will appear shortly", "Список появится через мгновение"))
            }
        } else if (widgetGroups.isEmpty()) {
            item(key = "widget_empty", contentType = "widget_state") {
            SettingsCard(copy.text("Віджети не знайдено", "No widgets found", "Виджеты не найдены"), copy.text("Система не повернула доступні віджети для встановлених додатків", "The system did not return available widgets for installed apps", "Система не вернула доступные виджеты для установленных приложений"))
            }
        }
    }
}

@Composable
fun WidgetProviderRow(group: WidgetAppGroup, alpha: Float, expanded: Boolean, onClick: () -> Unit) {
    TileContainer(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .noPressClickable(onClick = onClick),
        alpha = alpha,
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(start = 15.dp, end = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(rememberDrawablePainter(group.icon), null, tint = Color.Unspecified, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(11.dp)))
            Spacer(Modifier.width(15.dp))
            Text(group.label, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Text(group.providers.size.toString(), color = Color.White.copy(alpha = 0.60f), fontSize = 16.sp)
            Spacer(Modifier.width(14.dp))
            Text(if (expanded) "⌃" else "⌄", color = Color.White.copy(alpha = 0.70f), fontSize = 25.sp, lineHeight = 25.sp)
        }
    }
}

@Composable
fun WidgetProviderPreview(group: WidgetAppGroup, alpha: Float, onClick: (AppWidgetProviderInfo) -> Unit) {
    val context = LocalContext.current
    val provider = group.providers.firstOrNull()
    val previewKey = provider?.provider?.flattenToString()
    val cachedPreview = previewKey?.let { key -> synchronized(iconCacheLock) { widgetPreviewCache[key] } }
    val preview by produceState<Drawable?>(initialValue = cachedPreview, previewKey) {
        value = if (provider == null || previewKey == null) {
            null
        } else if (synchronized(iconCacheLock) { widgetPreviewCache.containsKey(previewKey) }) {
            synchronized(iconCacheLock) { widgetPreviewCache[previewKey] }
        } else {
            withContext(Dispatchers.IO) {
                runCatching { provider.loadPreviewImage(context.applicationContext, 0) }.getOrNull()
            }.also { loaded ->
                synchronized(iconCacheLock) { widgetPreviewCache[previewKey] = loaded }
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TileContainer(
            modifier = Modifier
                .fillMaxWidth()
                .height(194.dp)
                .padding(horizontal = 24.dp)
                .clickable { provider?.let(onClick) },
            alpha = alpha,
        ) {
            Box(Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
                val loadedPreview = preview
                if (loadedPreview != null) {
                    Image(
                        painter = rememberDrawablePainter(loadedPreview),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(112.dp).clip(RoundedCornerShape(24.dp)),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    WidgetPreviewFallback(group)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(group.label, color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(provider?.let { "${maxOf(1, it.minWidth / 72)} x ${maxOf(1, it.minHeight / 72)}" } ?: "1 x 1", color = Color.White.copy(alpha = 0.55f), fontSize = 17.sp)
        Spacer(Modifier.height(14.dp))
    }
}

@Composable
fun WidgetPreviewFallback(group: WidgetAppGroup) {
    Row(
        modifier = Modifier
            .width(294.dp)
            .height(94.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF282830)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(rememberDrawablePainter(group.icon), null, tint = Color.Unspecified, modifier = Modifier.size(42.dp).clip(RoundedCornerShape(12.dp)))
        Spacer(Modifier.width(14.dp))
        Text(group.label, color = Color.White.copy(alpha = 0.86f), fontSize = 20.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun AppWidgetHomeItem(
    key: String,
    size: WidgetSize?,
    editMode: Boolean,
    onResize: (Float, Float) -> Unit,
    onRemove: () -> Unit,
) {
    val context = LocalContext.current
    val widgetId = remember(key) { appWidgetIdFromKey(key) }
    val appWidgetManager = remember(context) { AppWidgetManager.getInstance(context) }
    val appWidgetHost = remember(context) { AppWidgetHost(context, APP_WIDGET_HOST_ID) }
    val providerInfo = remember(widgetId) { widgetId?.let { appWidgetManager.getAppWidgetInfo(it) } }

    DisposableEffect(appWidgetHost) {
        appWidgetHost.startListening()
        onDispose { appWidgetHost.stopListening() }
    }

    if (widgetId == null || providerInfo == null) {
        TileContainer(
            modifier = Modifier.width(280.dp).height(120.dp),
            alpha = LocalTileAlpha.current,
        ) {
            Box(Modifier.fillMaxSize().padding(18.dp), contentAlignment = Alignment.Center) {
                Text("Widget unavailable", color = Color.White.copy(alpha = 0.76f), fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        return
    }

    val defaultWidth = providerInfo.minWidth.coerceIn(220, 560).toFloat()
    val defaultHeight = providerInfo.minHeight.coerceIn(110, 420).toFloat()
    Box {
        EditableWidgetFrame(
            width = size?.width ?: defaultWidth,
            height = size?.height ?: defaultHeight,
            minWidth = 180f,
            maxWidth = 580f,
            minHeight = 90f,
            maxHeight = 520f,
            editMode = editMode,
            onResize = onResize,
        ) {
            TileContainer(Modifier.fillMaxSize(), LocalTileAlpha.current) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                        .clip(RoundedCornerShape(24.dp)),
                ) {
                    AndroidView(
                        factory = {
                            appWidgetHost.createView(it, widgetId, providerInfo).apply {
                                setAppWidget(widgetId, providerInfo)
                            }
                        },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
        if (editMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(42.dp)
                    .noPressClickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                RemoveBadge()
            }
        }
    }
}

@Composable
fun ClockWidget(width: Float, height: Float, alpha: Float, editMode: Boolean, onResize: (Float, Float) -> Unit) {
    EditableWidgetFrame(
        width = width,
        height = height,
        minWidth = 220f,
        maxWidth = 520f,
        minHeight = 112f,
        maxHeight = 320f,
        editMode = editMode,
        onResize = onResize,
    ) {
    TileContainer(Modifier.fillMaxSize(), alpha) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 22.dp), contentAlignment = Alignment.Center) {
            val scope = this
            val timeSize = min(scope.maxWidth.value * 0.29f, scope.maxHeight.value * 0.52f).coerceIn(62f, 96f).sp
            val dateSize = min(scope.maxWidth.value * 0.078f, scope.maxHeight.value * 0.18f).coerceIn(19f, 27f).sp
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text(currentTime(), fontSize = timeSize, fontWeight = FontWeight.Light, color = Color.White, maxLines = 1)
                Text(currentDate(), fontSize = dateSize, color = Color.White.copy(alpha = 0.75f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
    }
}

@Composable
fun WeatherWidget(
    location: String,
    copy: Copy,
    alpha: Float,
    editMode: Boolean,
    width: Float,
    height: Float,
    onResize: (Float, Float) -> Unit,
    onLocationChange: (String) -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    val weather by produceState<WeatherReport?>(initialValue = weatherCache?.takeIf { it.location == location }?.report, location) {
        value = getWeather(location)
    }
    EditableWidgetFrame(
        width = width,
        height = height,
        minWidth = 280f,
        maxWidth = 560f,
        minHeight = 180f,
        maxHeight = 430f,
        editMode = editMode,
        onResize = onResize,
    ) {
    TileContainer(Modifier.fillMaxSize().clickable { if (!editMode) showDialog = true }, alpha) {
        BoxWithConstraints(Modifier.fillMaxSize().padding(start = 28.dp, top = 28.dp, end = 28.dp, bottom = 22.dp)) {
            val scope = this
            val report = weather
            val big = min(scope.maxWidth.value * 0.19f, scope.maxHeight.value * 0.27f).coerceIn(56f, 86f).sp
            val normal = min(scope.maxWidth.value * 0.057f, scope.maxHeight.value * 0.086f).coerceIn(18f, 25f).sp
            val small = min(scope.maxWidth.value * 0.046f, scope.maxHeight.value * 0.062f).coerceIn(13f, 19f).sp
            Column(Modifier.fillMaxSize()) {
                Text(if (report == null) "--°" else "${report.temperature}°", fontSize = big, fontWeight = FontWeight.Light, color = Color.White, maxLines = 1)
                Text(report?.condition?.let { localizedWeatherCondition(it, copy) } ?: copy.loadingWeather, fontSize = normal, color = Color.White.copy(alpha = 0.82f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(localizedWeatherLocation(report?.location ?: location, copy), fontSize = small, color = Color.White.copy(alpha = 0.68f), maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.weight(1f))
                val forecastColumns = if (scope.maxWidth.value < 380f) 5 else 7
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    (report?.forecast ?: emptyList()).take(forecastColumns).forEach { day ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(localizedDayLabel(day.day, copy), fontSize = small, color = Color.White.copy(alpha = 0.72f), maxLines = 1)
                            Text("${day.maxTemp}°/", fontSize = small, color = Color.White, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
    }
    if (showDialog) {
        WeatherLocationDialog(location, copy, alpha, { showDialog = false }) {
            onLocationChange(it.ifBlank { DEFAULT_LOCATION })
            showDialog = false
        }
    }
}

@Composable
fun WeatherLocationDialog(initialLocation: String, copy: Copy, alpha: Float, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember(initialLocation) { mutableStateOf(initialLocation) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = LocalTileColor.current.copy(alpha = alpha.coerceIn(0.45f, 0.95f)),
        title = { Text(copy.weatherLocation, color = Color.White) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.12f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.10f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                ),
            )
        },
        confirmButton = { TextButton(onClick = { onSave(text.trim()) }) { Text(copy.save, color = Color.White) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(copy.cancel, color = Color.White.copy(alpha = 0.78f)) } },
    )
}

@Composable
fun AppTile(app: LauncherAppInfo, alpha: Float, iconPackPackage: String?, iconScale: Float, editMode: Boolean, canRemove: Boolean, hasNotification: Boolean, onClick: () -> Unit, onRemove: () -> Unit) {
    val tapScale = remember { Animatable(1f) }
    val tapScope = rememberCoroutineScope()
    val scale = iconScale.coerceIn(HOME_ICON_SCALE_MIN, HOME_ICON_SCALE_MAX)
    val tileHeight = (84f * scale).dp
    val iconSize = (58f * scale).dp
    val minWidth = (150f * scale).dp
    val maxWidth = (270f * scale).dp
    val horizontalPaddingStart = (15f * scale).dp
    val horizontalPaddingEnd = (21f * scale).dp
    val iconTextGap = (18f * scale).dp
    val titleSize = (18f * scale).sp
    Box {
        TileContainer(
            Modifier
            .wrapContentWidth()
            .widthIn(min = minWidth, max = maxWidth)
            .height(tileHeight)
            .graphicsLayer {
                scaleX = tapScale.value
                scaleY = tapScale.value
            }
            .noPressCombinedClickable(
                onClick = { if (!editMode) tapScope.launch { tapScale.playTapPulse(onClick) } },
                onLongClick = if (canRemove) onRemove else null,
            ),
            alpha,
        ) {
            Row(Modifier.wrapContentWidth().height(tileHeight).padding(start = horizontalPaddingStart, end = horizontalPaddingEnd), verticalAlignment = Alignment.CenterVertically) {
                Box {
                    Icon(rememberAppIconPainter(app, iconPackPackage), null, tint = Color.Unspecified, modifier = Modifier.size(iconSize))
                    if (hasNotification) {
                        NotificationDot(Modifier.align(Alignment.TopEnd).offset(x = 1.dp, y = (-1).dp))
                    }
                }
                Spacer(Modifier.width(iconTextGap))
                Text(localizedAppLabel(app, LocalCopy.current), fontSize = titleSize, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        if (editMode && canRemove) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size((32f * scale).dp)
                    .noPressClickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                RemoveBadge()
            }
        }
    }
}

@Composable
fun LauncherActionTile(
    label: String,
    alpha: Float,
    iconScale: Float,
    editMode: Boolean,
    onClick: () -> Unit,
    icon: @Composable BoxScope.() -> Unit,
) {
    val tapScale = remember { Animatable(1f) }
    val tapScope = rememberCoroutineScope()
    val scale = iconScale.coerceIn(HOME_ICON_SCALE_MIN, HOME_ICON_SCALE_MAX)
    val tileHeight = (84f * scale).dp
    val iconSize = (58f * scale).dp
    val minWidth = (150f * scale).dp
    val maxWidth = (270f * scale).dp

    TileContainer(
        modifier = Modifier
            .wrapContentWidth()
            .widthIn(min = minWidth, max = maxWidth)
            .height(tileHeight)
            .graphicsLayer {
                scaleX = tapScale.value
                scaleY = tapScale.value
            }
            .noPressClickable {
                if (!editMode) tapScope.launch { tapScale.playTapPulse(onClick) }
            },
        alpha = alpha,
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .height(tileHeight)
                .padding(start = (15f * scale).dp, end = (21f * scale).dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.size(iconSize),
                contentAlignment = Alignment.Center,
                content = icon,
            )
            Spacer(Modifier.width((18f * scale).dp))
            Text(
                text = label,
                fontSize = (18f * scale).sp,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun EditableWidgetFrame(
    width: Float,
    height: Float,
    minWidth: Float,
    maxWidth: Float,
    minHeight: Float,
    maxHeight: Float,
    editMode: Boolean,
    onResize: (Float, Float) -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    var frameWidth by remember(width) { mutableStateOf(width) }
    var frameHeight by remember(height) { mutableStateOf(height) }
    Box(
        modifier = Modifier
            .width(frameWidth.dp)
            .height(frameHeight.dp),
    ) {
        content()
        if (editMode) {
            val cyan = Color(0xFF22E8F0)
            Box(
                Modifier
                    .matchParentSize()
                    .border(3.dp, cyan, RoundedCornerShape(28.dp)),
            )
            listOf(
                ResizeHandle.Top to (Alignment.TopCenter to Modifier.offset(y = (-10).dp)),
                ResizeHandle.Bottom to (Alignment.BottomCenter to Modifier.offset(y = 10.dp)),
                ResizeHandle.Start to (Alignment.CenterStart to Modifier.offset(x = (-10).dp)),
                ResizeHandle.End to (Alignment.CenterEnd to Modifier.offset(x = 10.dp)),
            ).forEach { (handle, placement) ->
                val (alignment, offsetModifier) = placement
                Box(
                    modifier = Modifier
                        .align(alignment)
                        .then(offsetModifier)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(cyan)
                        .border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                        .pointerInput(handle) {
                            detectDragGestures(
                                onDragEnd = { onResize(frameWidth, frameHeight) },
                                onDragCancel = {
                                    frameWidth = width
                                    frameHeight = height
                                },
                            ) { change, dragAmount ->
                                change.consume()
                                val deltaWidth = with(density) { dragAmount.x.toDp().value }
                                val deltaHeight = with(density) { dragAmount.y.toDp().value }
                                frameWidth = when (handle) {
                                    ResizeHandle.Start -> frameWidth - deltaWidth
                                    ResizeHandle.End -> frameWidth + deltaWidth
                                    else -> frameWidth
                                }.coerceIn(minWidth, maxWidth)
                                frameHeight = when (handle) {
                                    ResizeHandle.Top -> frameHeight - deltaHeight
                                    ResizeHandle.Bottom -> frameHeight + deltaHeight
                                    else -> frameHeight
                                }.coerceIn(minHeight, maxHeight)
                            }
                        },
                )
            }
        }
    }
}

private enum class ResizeHandle { Top, Bottom, Start, End }

@Composable
fun NotificationDot(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(Color(0xFF22E8F0))
            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape)
    )
}

@Composable
fun RemoveBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(0xFFE84855))
            .border(1.dp, Color.White.copy(alpha = 0.78f), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text("×", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 18.sp)
    }
}

@Composable
fun AppDrawer(
    apps: List<LauncherAppInfo>,
    query: String,
    copy: Copy,
    showAppCount: Boolean,
    hiddenAppKeys: Set<String>,
    newAppKeys: Set<String>,
    customFolders: Map<String, List<String>>,
    folderOrder: List<String>,
    notificationPackages: Set<String>,
    recentApps: List<LauncherAppInfo>,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onAppClick: (LauncherAppInfo) -> Unit,
    onAddToHome: (LauncherAppInfo) -> Unit,
    onHideApp: (LauncherAppInfo) -> Unit,
    onUninstall: (LauncherAppInfo) -> Unit,
    iconPackPackage: String?,
    onMarkNewSeen: () -> Unit,
    onUpdateFolderApps: (String, List<String>) -> Unit,
    allApps: List<LauncherAppInfo>,
    folderIconScale: Float,
    backHandlerEnabled: Boolean = true,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var selectedFolder by remember { mutableStateOf<AppFolder?>(null) }
    val visibleApps = remember(apps, hiddenAppKeys) {
        apps.filterNot { it.componentKey() in hiddenAppKeys }
    }
    val filtered = remember(visibleApps, query) {
        if (query.isBlank()) {
            visibleApps
        } else {
            visibleApps.filter { it.label.contains(query, true) || it.packageName.contains(query, true) }
        }
    }
    val folders = remember(visibleApps, newAppKeys, hiddenAppKeys, customFolders, folderOrder) {
        buildFolders(visibleApps, newAppKeys - hiddenAppKeys, customFolders, folderOrder)
    }
    val folderNotificationState = remember(folders, notificationPackages) {
        folders.associate { folder ->
            folder.title to folder.apps.any { it.packageName in notificationPackages }
        }
    }
    
    // Слідкуємо за станом відкритої папки, щоб вона оновлювалась при приховуванні додатків або зміні складу
    val activeFolder = selectedFolder?.let { selected ->
        folders.find { it.title == selected.title }
    }
    val density = LocalDensity.current
    val isKeyboardVisible = WindowInsets.ime.getBottom(density) > 0
    var isSearchActive by remember { mutableStateOf(false) }
    val closeDrawerDistance = with(density) { 44.dp.toPx() }
    val openNotificationsDistance = with(density) { 32.dp.toPx() }
    val tileAlpha = LocalTileAlpha.current
    val listState = rememberLazyListState()
    BackHandler(enabled = backHandlerEnabled) { onBack() }
    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible && isSearchActive) {
            isSearchActive = false
            focusManager.clearFocus(force = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(listState, closeDrawerDistance, openNotificationsDistance) {
                awaitEachGesture {
                    val down = awaitFirstDown(
                        requireUnconsumed = false,
                        pass = PointerEventPass.Initial,
                    )
                    
                    val startedAtTop = listState.firstVisibleItemIndex == 0 &&
                        listState.firstVisibleItemScrollOffset == 0
                    var pullDistance = 0f
                    var horizontalDistance = 0f
                    var verticalDistance = 0f
                    var openedForGesture = false

                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val change = event.changes.firstOrNull { pointer ->
                            pointer.id == down.id
                        } ?: break

                        if (change.changedToUpIgnoreConsumed()) {
                            break
                        }

                        val delta = change.positionChange()
                        horizontalDistance += delta.x
                        verticalDistance += delta.y

                        if (
                            !openedForGesture &&
                            horizontalDistance >= closeDrawerDistance &&
                            abs(horizontalDistance) > abs(verticalDistance) * 1.35f
                        ) {
                            openedForGesture = true
                            onBack()
                            change.consume()
                        }

                        if (startedAtTop) {
                            val deltaY = delta.y

                            val isStillAtTop = listState.firstVisibleItemIndex == 0 &&
                                listState.firstVisibleItemScrollOffset == 0
                            if (deltaY > 0f && isStillAtTop) {
                                pullDistance += deltaY
                                if (!openedForGesture && pullDistance >= openNotificationsDistance) {
                                    openedForGesture = true
                                    if (!openNotificationShade(context)) {
                                        Toast.makeText(context, "Enable accessibility permission for the notification gesture", Toast.LENGTH_SHORT).show()
                                    }
                                    change.consume()
                                }
                            } else if (deltaY < 0f) {
                                pullDistance = 0f
                            }
                        }
                    }
                }
            }
            .statusBarsPadding()
            .padding(start = 6.dp, top = 34.dp, end = 6.dp),
        state = listState,
    ) {
        item(key = "drawer_header", contentType = "drawer_header") {
            Text(copy.apps, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
            if (showAppCount) {
                Text(copy.installed(visibleApps.size), fontSize = 16.sp, color = Color.White.copy(alpha = 0.78f), maxLines = 1)
            }
            Spacer(Modifier.height(22.dp))
        }
        item(key = "drawer_search", contentType = "drawer_search") {
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(63.dp)
                    .onFocusChanged { isSearchActive = it.isFocused },
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                cursorBrush = SolidColor(Color.White),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color.White.copy(alpha = 0.55f), RoundedCornerShape(22.dp))
                            .clip(RoundedCornerShape(22.dp))
                            .padding(horizontal = 14.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (query.isEmpty()) {
                            Text(copy.searchApps, color = Color.White.copy(alpha = 0.74f), fontSize = 18.sp)
                        }
                        innerTextField()
                    }
                },
            )
            Spacer(Modifier.height(if (query.isBlank()) 18.dp else 10.dp))
        }
        if (query.isBlank()) {
            if (isSearchActive && isKeyboardVisible && recentApps.isNotEmpty()) {
                item(key = "drawer_recent", contentType = "drawer_recent") {
                    Text(
                        copy.text("Останні відкриті", "Recently opened", "Недавно открытые"),
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
                    )
                    AppGrid(
                        recentApps,
                        notificationPackages,
                        onAppClick,
                        onAddToHome,
                        onHideApp,
                        onUninstall,
                        iconPackPackage = iconPackPackage,
                        modifier = Modifier.height(gridHeightForApps(recentApps.size))
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }

            item(key = "drawer_folders", contentType = "drawer_folders") {
                BoxWithConstraints(Modifier.fillMaxWidth()) {
                    val scope = this
                    val columnGap = 8.dp
                    val rowGap = 21.dp
                    val rowCount = (folders.size + 1) / 2
                    val tileWidth = (scope.maxWidth - columnGap) / 2
                    val rowHeight = tileWidth + 36.dp
                    val gridHeight = if (rowCount == 0) 0.dp else {
                        (rowHeight * rowCount.toFloat()) + (rowGap * (rowCount - 1).toFloat())
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier.fillMaxWidth().height(gridHeight),
                        userScrollEnabled = false,
                        horizontalArrangement = Arrangement.spacedBy(columnGap),
                        verticalArrangement = Arrangement.spacedBy(rowGap),
                    ) {
                        items(folders, key = { it.title }, contentType = { "app_drawer_folder" }) { folder ->
                            FolderTile(
                                folder,
                                iconPackPackage,
                                hasNotification = folderNotificationState[folder.title] == true,
                            ) {
                                selectedFolder = folder
                                if (folder.isNewApps) onMarkNewSeen()
                            }
                        }
                    }
                }
            }
        } else {
            items(filtered, key = { it.componentKey() }, contentType = { "drawer_search_row" }) { app ->
                SearchAppRow(
                    app = app,
                    iconPackPackage = iconPackPackage,
                    hasNotification = app.packageName in notificationPackages,
                    onClick = { onAppClick(app) },
                    onAddToHome = { onAddToHome(app) },
                    onHideApp = { onHideApp(app) },
                    onUninstall = { onUninstall(app) },
                )
                Spacer(Modifier.height(7.dp))
            }
        }
        item(key = "drawer_bottom_spacer", contentType = "drawer_bottom_spacer") {
            Spacer(Modifier.navigationBarsPadding().height(6.dp))
        }
    }

    activeFolder?.let { folder ->
        CategoryDialog(
            folder, 
            copy, 
            notificationPackages,
            { selectedFolder = null }, 
            { selectedFolder = null; onAppClick(it) }, 
            onAddToHome, 
            onHideApp, 
            onUninstall, 
            iconPackPackage,
            allApps = allApps,
            onUpdateApps = { onUpdateFolderApps(folder.title, it) },
            folderIconScale = folderIconScale
        )
    }
}

private fun buildFolders(apps: List<LauncherAppInfo>, newAppKeys: Set<String>, customFolders: Map<String, List<String>> = emptyMap(), folderOrder: List<String> = emptyList()): List<AppFolder> {
    val assignedKeys = mutableSetOf<String>()
    
    // 1. Спочатку обробляємо кастомні папки (найвищий пріоритет)
    val customAppFolders = customFolders.map { (name, appKeys) ->
        val folderApps = apps.filter { it.componentKey() in appKeys }
        assignedKeys.addAll(appKeys)
        AppFolder(name, folderApps, isCustom = true)
    }

    val visibleApps = apps.filterNot { it.componentKey() in assignedKeys }
    
    // Функція-помічник для відфільтровування вже призначених додатків
    fun List<LauncherAppInfo>.filterAvailable() = this.filter { it.componentKey() !in assignedKeys }

    // 2. Визначаємо кандидатів для автоматичних папок
    val newAppsCandidates = apps.filter { it.componentKey() in newAppKeys }.filterAvailable()
    val googleCandidates = apps.filter { it.packageName.startsWith("com.google") || it.packageName.contains("android.google") }.filterAvailable()
    val gamesCandidates = apps.filter { it.category == android.content.pm.ApplicationInfo.CATEGORY_GAME || it.packageName.contains(".game") }.filterAvailable()
    
    val socialCandidates = apps.filter { 
        it.category == android.content.pm.ApplicationInfo.CATEGORY_SOCIAL || 
        listOf("telegram", "whatsapp", "viber", "messenger", "facebook", "instagram", "tiktok", "twitter", "discord", "snapchat").any { p -> it.packageName.contains(p) }
    }.filterAvailable()
    
    val mediaCandidates = apps.filter { 
        it.category == android.content.pm.ApplicationInfo.CATEGORY_AUDIO || 
        it.category == android.content.pm.ApplicationInfo.CATEGORY_VIDEO ||
        listOf("youtube", "music", "player", "video", "spotify", "netflix", "megogo").any { p -> it.packageName.contains(p) }
    }.filterAvailable()
    
    val toolsCandidates = apps.filter { 
        it.category == android.content.pm.ApplicationInfo.CATEGORY_PRODUCTIVITY || 
        it.packageName.contains("settings") || it.packageName.contains("calculator") || 
        it.packageName.contains("clock") || it.packageName.contains("calendar") || 
        it.packageName.contains("file") || it.packageName.contains("notes") || 
        it.packageName.contains("mail") || it.packageName.contains("browser") ||
        it.packageName.contains("chrome") || it.packageName.contains("firefox") ||
        it.packageName.contains("camera") || it.packageName.contains("gallery")
    }.filterAvailable()
    
    val shoppingCandidates = apps.filter { 
        listOf("shop", "market", "amazon", "ebay", "aliexpress", "olx", "rozetka", "prom.ua").any { p -> it.packageName.contains(p) }
    }.filterAvailable()
    
    val financeCandidates = apps.filter { 
        listOf("bank", "finance", "wallet", "pay", "crypto", "privat24", "monobank").any { p -> it.packageName.contains(p) }
    }.filterAvailable()
    
    val travelCandidates = apps.filter { 
        it.category == android.content.pm.ApplicationInfo.CATEGORY_MAPS || 
        listOf("maps", "navigation", "uber", "bolt", "taxi", "booking", "trip", "airbnb", "waze").any { p -> it.packageName.contains(p) }
    }.filterAvailable()

    val unsorted = buildList {
        // Додаємо кастомні
        addAll(customAppFolders)

        // Додаємо автоматичні папки з перевіркою на дублікати за ланцюжком
        // (Додаток потрапляє в першу ж підходящу папку і виключається з наступних)
        
        if (newAppsCandidates.isNotEmpty() && !customFolders.containsKey("Нові додатки")) {
            add(AppFolder("Нові додатки", newAppsCandidates, isNewApps = true))
            assignedKeys.addAll(newAppsCandidates.map { it.componentKey() })
        }
        
        val google = googleCandidates.filterAvailable()
        if (google.isNotEmpty() && !customFolders.containsKey("Google сервіси")) {
            add(AppFolder("Google сервіси", google))
            assignedKeys.addAll(google.map { it.componentKey() })
        }

        val social = socialCandidates.filterAvailable()
        if (social.isNotEmpty() && !customFolders.containsKey("Соцмережі")) {
            add(AppFolder("Соцмережі", social))
            assignedKeys.addAll(social.map { it.componentKey() })
        }

        val games = gamesCandidates.filterAvailable()
        if (games.isNotEmpty() && !customFolders.containsKey("Ігри")) {
            add(AppFolder("Ігри", games))
            assignedKeys.addAll(games.map { it.componentKey() })
        }

        val finance = financeCandidates.filterAvailable()
        if (finance.isNotEmpty() && !customFolders.containsKey("Фінанси")) {
            add(AppFolder("Фінанси", finance))
            assignedKeys.addAll(finance.map { it.componentKey() })
        }

        val shopping = shoppingCandidates.filterAvailable()
        if (shopping.isNotEmpty() && !customFolders.containsKey("Шопінг")) {
            add(AppFolder("Шопінг", shopping))
            assignedKeys.addAll(shopping.map { it.componentKey() })
        }

        val tools = toolsCandidates.filterAvailable()
        if (tools.isNotEmpty() && !customFolders.containsKey("Інструменти")) {
            add(AppFolder("Інструменти", tools))
            assignedKeys.addAll(tools.map { it.componentKey() })
        }

        val media = mediaCandidates.filterAvailable()
        if (media.isNotEmpty() && !customFolders.containsKey("Відео та Музика")) {
            add(AppFolder("Відео та Музика", media))
            assignedKeys.addAll(media.map { it.componentKey() })
        }

        val travel = travelCandidates.filterAvailable()
        if (travel.isNotEmpty() && !customFolders.containsKey("Подорожі")) {
            add(AppFolder("Подорожі", travel))
            assignedKeys.addAll(travel.map { it.componentKey() })
        }

        // "Головне" тепер містить залишок з перших додатків списку, які не потрапили в категорії
        val remainingVisible = apps.filter { it.componentKey() !in assignedKeys }
        if (remainingVisible.take(24).isNotEmpty() && !customFolders.containsKey("Головне")) {
            add(AppFolder("Головне", remainingVisible.take(24)))
        }

        add(AppFolder("Усі додатки", apps)) // "Усі додатки" завжди містять все
    }

    if (folderOrder.isEmpty()) return unsorted

    val orderMap = folderOrder.withIndex().associate { it.value to it.index }
    return unsorted.sortedBy { folder -> orderMap[folder.title] ?: (1000 + unsorted.indexOf(folder)) }
}
@Composable
fun FolderTile(folder: AppFolder, iconPackPackage: String?, hasNotification: Boolean, onClick: () -> Unit) {
    val copy = LocalCopy.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            TileContainer(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .noPressClickable(onClick = onClick),
                LocalTileAlpha.current,
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val scope = this
                    val gap = 10.dp
                    val iconSize = ((scope.maxWidth - gap) / 2).coerceAtMost(78.dp)
                    val previewApps = folder.apps.take(4)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(gap),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        repeat(2) { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(gap),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                repeat(2) { column ->
                                    val app = previewApps.getOrNull(row * 2 + column)
                                    if (app != null) {
                                        Icon(
                                            rememberAppIconPainter(app, iconPackPackage),
                                            null,
                                            tint = Color.Unspecified,
                                            modifier = Modifier
                                                .size(iconSize)
                                                .clip(RoundedCornerShape(8.dp)),
                                        )
                                    } else {
                                        Spacer(Modifier.size(iconSize))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (hasNotification) {
                NotificationDot(Modifier.align(Alignment.TopEnd).padding(5.dp))
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            localizedFolderTitle(folder.title, copy),
            color = Color.White,
            fontSize = 15.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun CategoryDialog(
    folder: AppFolder, 
    copy: Copy, 
    notificationPackages: Set<String>,
    onDismiss: () -> Unit, 
    onAppClick: (LauncherAppInfo) -> Unit, 
    onAddToHome: (LauncherAppInfo) -> Unit, 
    onHideApp: (LauncherAppInfo) -> Unit, 
    onUninstall: (LauncherAppInfo) -> Unit, 
    iconPackPackage: String?,
    allApps: List<LauncherAppInfo>,
    onUpdateApps: (List<String>) -> Unit,
    folderIconScale: Float,
) {
    var showAppSelector by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalTileAlpha provides OPEN_FOLDER_ALPHA) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noPressClickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.85f)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .noPressClickable(enabled = false) {},
                shape = RoundedCornerShape(27.dp),
                color = LocalTileColor.current.copy(alpha = OPEN_FOLDER_ALPHA),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp, vertical = 21.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            localizedFolderTitle(folder.title, copy),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (folder.title != "Усі додатки") {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .tileStrokeBorder(CircleShape)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                    ) { showAppSelector = true },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text("+", color = Color.White, fontSize = 18.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    FolderAppGrid(
                        folder.apps,
                        notificationPackages,
                        onAppClick,
                        onAddToHome,
                        onHideApp,
                        onUninstall,
                        iconPackPackage,
                        folderIconScale,
                        Modifier.weight(1f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(21.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                        border = tileBorderStrokeOrNull(),
                    ) {
                        Text(copy.back, fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }

    if (showAppSelector) {
        FolderAppSelectorDialog(
            copy = copy,
            folderName = folder.title,
            allApps = allApps,
            selectedKeys = folder.apps.map { it.componentKey() }.toSet(),
            onDismiss = { showAppSelector = false },
            onSave = { 
                onUpdateApps(it.toList())
                showAppSelector = false
            }
        )
    }
}

@Composable
fun FolderAppGrid(apps: List<LauncherAppInfo>, notificationPackages: Set<String>, onAppClick: (LauncherAppInfo) -> Unit, onAddToHome: (LauncherAppInfo) -> Unit, onHideApp: (LauncherAppInfo) -> Unit, onUninstall: (LauncherAppInfo) -> Unit, iconPackPackage: String?, folderIconScale: Float, modifier: Modifier = Modifier) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(21.dp),
    ) {
        items(apps, key = { it.componentKey() }, contentType = { "folder_app" }) { app ->
            FolderDialogAppIcon(
                app, 
                iconPackPackage, 
                folderIconScale, 
                hasNotification = app.packageName in notificationPackages,
                onClick = { onAppClick(app) }, 
                onAddToHome = { onAddToHome(app) }, 
                onHideApp = { onHideApp(app) }, 
                onUninstall = { onUninstall(app) }
            )
        }
    }
}

@Composable
fun FolderDialogAppIcon(app: LauncherAppInfo, iconPackPackage: String?, iconScale: Float, hasNotification: Boolean, onClick: () -> Unit, onAddToHome: () -> Unit, onHideApp: () -> Unit, onUninstall: () -> Unit) {
    val copy = LocalCopy.current
    var menu by remember { mutableStateOf(false) }
    var itemRootPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var menuPosition by remember { mutableStateOf(IntOffset.Zero) }
    val scale = iconScale.coerceIn(HOME_ICON_SCALE_MIN, HOME_ICON_SCALE_MAX)
    val iconSize = (64f * scale).dp
    val titleSize = (12f * scale).sp
    val containerWidth = (76f * scale).dp
    Box {
        Column(
            Modifier
                .width(containerWidth)
                .onGloballyPositioned { itemRootPosition = it.positionInRoot() }
                .pointerInput(app.componentKey()) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = { pressOffset ->
                            menuPosition = IntOffset(
                                x = (itemRootPosition.x + pressOffset.x).roundToInt(),
                                y = (itemRootPosition.y + pressOffset.y).roundToInt(),
                            )
                            menu = true
                        },
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                Icon(rememberAppIconPainter(app, iconPackPackage), null, tint = Color.Unspecified, modifier = Modifier.size(iconSize))
                if (hasNotification) {
                    NotificationDot(Modifier.align(Alignment.TopEnd).offset(x = 1.dp, y = (-1).dp))
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(localizedAppLabel(app, copy), color = Color.White, fontSize = titleSize, maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
        LauncherContextMenu(expanded = menu, onDismissRequest = { menu = false }, position = menuPosition) {
            LauncherDropdownItem(copy.text("На головний", "Add to home", "На главный")) { menu = false; onAddToHome() }
            LauncherDropdownItem(copy.text("Приховати", "Hide", "Скрыть")) { menu = false; onHideApp() }
            if (!app.isSystemApp) {
                LauncherDropdownItem(copy.text("Видалити", "Uninstall", "Удалить"), Color(0xFFFF5F5F)) { menu = false; onUninstall() }
            }
        }
    }
}
@Composable
fun AppGrid(apps: List<LauncherAppInfo>, notificationPackages: Set<String>, onAppClick: (LauncherAppInfo) -> Unit, onAddToHome: (LauncherAppInfo) -> Unit, onHideApp: (LauncherAppInfo) -> Unit, onUninstall: (LauncherAppInfo) -> Unit, iconPackPackage: String?, modifier: Modifier = Modifier) {
    // Обмежуємо ширину сітки, щоб іконки були компактно згруповані
    Box(modifier = modifier.fillMaxWidth()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4), 
            modifier = Modifier.width(214.dp), // 4 іконки по 52dp + проміжки
            horizontalArrangement = Arrangement.spacedBy(2.dp), 
            verticalArrangement = Arrangement.spacedBy(14.dp),
            userScrollEnabled = false
        ) {
            items(apps, key = { it.componentKey() }, contentType = { "drawer_app" }) { app ->
                AppIcon(
                    app, 
                    iconPackPackage, 
                    hasNotification = app.packageName in notificationPackages,
                    onClick = { onAppClick(app) }, 
                    onAddToHome = { onAddToHome(app) }, 
                    onHideApp = { onHideApp(app) }, 
                    onUninstall = { onUninstall(app) }
                )
            }
        }
    }
}

@Composable
fun SearchAppList(apps: List<LauncherAppInfo>, notificationPackages: Set<String>, onAppClick: (LauncherAppInfo) -> Unit, onAddToHome: (LauncherAppInfo) -> Unit, onHideApp: (LauncherAppInfo) -> Unit, onUninstall: (LauncherAppInfo) -> Unit, iconPackPackage: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        apps.forEach { app ->
            SearchAppRow(
                app = app,
                iconPackPackage = iconPackPackage,
                hasNotification = app.packageName in notificationPackages,
                onClick = { onAppClick(app) },
                onAddToHome = { onAddToHome(app) },
                onHideApp = { onHideApp(app) },
                onUninstall = { onUninstall(app) },
            )
        }
    }
}

@Composable
fun SearchAppRow(app: LauncherAppInfo, iconPackPackage: String?, hasNotification: Boolean, onClick: () -> Unit, onAddToHome: () -> Unit, onHideApp: () -> Unit, onUninstall: () -> Unit) {
    val copy = LocalCopy.current
    var menu by remember { mutableStateOf(false) }
    var itemRootPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var menuPosition by remember { mutableStateOf(IntOffset.Zero) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(LocalTileColor.current.copy(alpha = LocalTileAlpha.current * 0.72f))
                .onGloballyPositioned { itemRootPosition = it.positionInRoot() }
                .pointerInput(app.componentKey()) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = { pressOffset ->
                            menuPosition = IntOffset(
                                x = (itemRootPosition.x + pressOffset.x).roundToInt(),
                                y = (itemRootPosition.y + pressOffset.y).roundToInt(),
                            )
                            menu = true
                        },
                    )
                }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                Icon(
                    rememberAppIconPainter(app, iconPackPackage),
                    null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(11.dp)),
                )
                if (hasNotification) {
                    NotificationDot(Modifier.align(Alignment.TopEnd).offset(x = 1.dp, y = (-1).dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Text(localizedAppLabel(app, copy), color = Color.White, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        }
        LauncherContextMenu(expanded = menu, onDismissRequest = { menu = false }, position = menuPosition) {
            LauncherDropdownItem(copy.text("На головний", "Add to home", "На главный")) { menu = false; onAddToHome() }
            LauncherDropdownItem(copy.text("Приховати", "Hide", "Скрыть")) { menu = false; onHideApp() }
            if (!app.isSystemApp) {
                LauncherDropdownItem(copy.text("Видалити", "Uninstall", "Удалить"), Color(0xFFFF5F5F)) { menu = false; onUninstall() }
            }
        }
    }
}

@Composable
fun AppIcon(app: LauncherAppInfo, iconPackPackage: String?, hasNotification: Boolean, onClick: () -> Unit, onAddToHome: () -> Unit, onHideApp: () -> Unit, onUninstall: () -> Unit) {
    val copy = LocalCopy.current
    var menu by remember { mutableStateOf(false) }
    var itemRootPosition by remember { mutableStateOf(androidx.compose.ui.geometry.Offset.Zero) }
    var menuPosition by remember { mutableStateOf(IntOffset.Zero) }
    Box {
        Column(
            Modifier
                .width(52.dp)
                .onGloballyPositioned { itemRootPosition = it.positionInRoot() }
                .pointerInput(app.componentKey()) {
                    detectTapGestures(
                        onTap = { onClick() },
                        onLongPress = { pressOffset ->
                            menuPosition = IntOffset(
                                x = (itemRootPosition.x + pressOffset.x).roundToInt(),
                                y = (itemRootPosition.y + pressOffset.y).roundToInt(),
                            )
                            menu = true
                        },
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.size(48.dp).clip(RoundedCornerShape(15.dp)).background(LocalTileColor.current.copy(alpha = LocalTileAlpha.current)).padding(8.dp), contentAlignment = Alignment.Center) {
                Box {
                    Icon(rememberAppIconPainter(app, iconPackPackage), null, tint = Color.Unspecified, modifier = Modifier.fillMaxSize())
                    if (hasNotification) {
                        NotificationDot(Modifier.align(Alignment.TopEnd).offset(x = 1.dp, y = (-1).dp))
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                localizedAppLabel(app, copy), 
                color = Color.White, 
                fontSize = 12.sp, 
                maxLines = 2, 
                lineHeight = 12.sp,
                textAlign = TextAlign.Center
            )
        }
        LauncherContextMenu(expanded = menu, onDismissRequest = { menu = false }, position = menuPosition) {
            LauncherDropdownItem(copy.text("На головний", "Add to home", "На главный")) { menu = false; onAddToHome() }
            LauncherDropdownItem(copy.text("Приховати", "Hide", "Скрыть")) { menu = false; onHideApp() }
            if (!app.isSystemApp) {
                LauncherDropdownItem(copy.text("Видалити", "Uninstall", "Удалить"), Color(0xFFFF5F5F)) { menu = false; onUninstall() }
            }
        }
    }
}

@Composable
fun SettingsScreen(
    settings: LauncherSettings,
    copy: Copy,
    onSettingsChange: (LauncherSettings) -> Unit,
    onResetHomeScreen: () -> Unit,
    hiddenApps: List<LauncherAppInfo>,
    onUnhideApp: (LauncherAppInfo) -> Unit,
    onBack: () -> Unit,
    onDefaultLauncher: () -> Unit,
    onNotificationAccess: () -> Unit,
    onAccessibility: () -> Unit,
    allApps: List<LauncherAppInfo>,
    newAppKeys: Set<String>,
) {
    var tab by remember { mutableStateOf(SettingsTab.Personalization) }
    var showWallpapers by remember { mutableStateOf(false) }
    var showHiddenApps by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()
    val tabScroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scroll)
                .padding(top = 21.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(Color(0xFF202126).copy(alpha = LocalTileAlpha.current))
                        .clickable {
                            if (showWallpapers) {
                                showWallpapers = false
                            } else {
                                onBack()
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back_24),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(copy.settings, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }

            if (!showWallpapers) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tabScroll),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Spacer(Modifier.width(8.dp))
                    SettingsTabPill(copy.text("Персоналізація", "Personalization", "Персонализация"), tab == SettingsTab.Personalization) { tab = SettingsTab.Personalization }
                    SettingsTabPill(copy.text("Інтерфейс", "Interface", "Интерфейс"), tab == SettingsTab.Interface) { tab = SettingsTab.Interface }
                    SettingsTabPill(copy.text("Меню додатків", "App menu", "Меню приложений"), tab == SettingsTab.AppMenu) { tab = SettingsTab.AppMenu }
                    SettingsTabPill(copy.text("Дозволи", "Permissions", "Разрешения"), tab == SettingsTab.Permissions) { tab = SettingsTab.Permissions }
                    Spacer(Modifier.width(8.dp))
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 13.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                if (showWallpapers) {
                    WallpaperPickerScreen(
                        copy = copy,
                        settings = settings,
                        alpha = settings.tileAlpha,
                        onBack = { showWallpapers = false },
                        onSelect = { onSettingsChange(settings.copy(wallpaperKey = it)) },
                        onAddCustom = { uri ->
                            val uriString = uri.toString()
                            val nextUris = (settings.customWallpaperUris + uriString).distinct()
                            onSettingsChange(settings.copy(wallpaperKey = customWallpaperKey(uriString), customWallpaperUris = nextUris))
                        },
                        onDeleteCustom = { uriString ->
                            val nextUris = settings.customWallpaperUris.filterNot { it == uriString }
                            val nextKey = if (settings.wallpaperKey == customWallpaperKey(uriString)) "warm_orange" else settings.wallpaperKey
                            onSettingsChange(settings.copy(wallpaperKey = nextKey, customWallpaperUris = nextUris))
                        },
                    )
                } else {
                    when (tab) {
                        SettingsTab.Personalization -> PersonalizationSettings(copy, settings, onSettingsChange, { showWallpapers = true }, onResetHomeScreen)
                        SettingsTab.Interface -> InterfaceSettings(copy, settings, onSettingsChange, onResetHomeScreen, hiddenApps, { showHiddenApps = true })
                        SettingsTab.AppMenu -> AppMenuSettings(copy, settings, onSettingsChange, allApps, newAppKeys)
                        SettingsTab.Permissions -> PermissionSettings(copy, onDefaultLauncher, onNotificationAccess, onAccessibility)
                    }
                }
            }
        }

        if (showHiddenApps) {
            HiddenAppsDialog(
                copy = copy,
                apps = hiddenApps,
                iconPackPackage = settings.iconPackPackage,
                onDismiss = { showHiddenApps = false },
                onUnhideApp = onUnhideApp,
            )
        }
    }
}

@Composable
fun SettingsTabPill(label: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) Color(0xFF2C526E) else LocalTileColor.current
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = LocalTileAlpha.current))
            .tileStrokeBorder(RoundedCornerShape(20.dp))
            .noPressClickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 15.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, maxLines = 1)
    }
}

@Composable
fun PersonalizationSettings(
    copy: Copy,
    settings: LauncherSettings,
    onSettingsChange: (LauncherSettings) -> Unit,
    onChooseWallpapers: () -> Unit,
    onResetHomeScreen: () -> Unit,
) {
    SettingsCard(copy.text("Шпалери робочого столу", "Home screen wallpapers", "Обои рабочего стола"), copy.text("Оберіть стандартні шпалери або додайте власні", "Choose a built-in wallpaper or add your own", "Выберите стандартные обои или добавьте свои"))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        SettingsSmallButton(copy.text("Обрати шпалери", "Choose wallpaper", "Выбрать обои"), Color(0xFF1FA4FF), onChooseWallpapers)
        SettingsSmallButton(copy.text("Прибрати", "Remove", "Убрать"), Color.White) { onSettingsChange(settings.copy(wallpaperKey = "warm_orange")) }
    }
    SettingsCard(copy.text("Пак іконок", "Icon pack", "Пак иконок"), copy.text("Застосовує встановлений пакет іконок", "Applies an installed icon pack", "Применяет установленный пакет иконок"))
    IconPackSelector(settings.iconPackPackage) { onSettingsChange(settings.copy(iconPackPackage = it)) }
    SliderSettingsCard(copy.text("Розмір іконок на головному екрані", "Home screen icon size", "Размер иконок на главном экране"), copy.text("Змінює масштаб додатків на головному екрані", "Changes the app icon scale on the home screen", "Меняет масштаб приложений на главном экране"), settings.homeIconScale, valueRange = HOME_ICON_SCALE_MIN..HOME_ICON_SCALE_MAX) {
        onSettingsChange(settings.copy(homeIconScale = it.coerceIn(HOME_ICON_SCALE_MIN, HOME_ICON_SCALE_MAX)))
    }
    SliderSettingsCard(copy.text("Прозорість плиток", "Tile transparency", "Прозрачность плиток"), copy.text("Прозорість фону під іконкою та назвою на головному екрані й у меню додатків", "Background transparency behind icons and labels", "Прозрачность фона под иконкой и названием"), settings.tileAlpha) {
        onSettingsChange(settings.copy(tileAlpha = it.coerceIn(0.35f, 0.95f)))
    }
    FillColorPalette(copy, settings.tileColor, settings.customTileColor) { selected, custom ->
        onSettingsChange(settings.copy(tileColor = selected, customTileColor = custom))
    }
    SettingsSwitchCard(copy.text("Обведення плиток", "Tile outline", "Обводка плиток"), copy.text("Додає адаптивну тонку рамку навколо заливки плиток", "Adds a thin adaptive border around tiles", "Добавляет тонкую адаптивную рамку вокруг плиток"), settings.tileStrokeEnabled) {
        onSettingsChange(settings.copy(tileStrokeEnabled = it))
    }
}

@Composable
fun IconPackSelector(selectedPackage: String?, onSelect: (String?) -> Unit) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val packs by produceState(initialValue = iconPackListCache ?: listOf(IconPackInfo(null, "Default", null)), context) {
        value = withContext(Dispatchers.IO) { loadIconPacks(context.applicationContext) }
    }
    val selected = packs.firstOrNull { it.packageName == selectedPackage } ?: packs.first()
    Box {
        SettingsSmallButton(selected.label, Color.White) { expanded = true }
        LauncherDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            packs.forEach { pack ->
                LauncherDropdownItem(
                    label = pack.label,
                    color = if (pack.packageName == selectedPackage) Color(0xFF22E8F0) else Color.White,
                ) {
                    expanded = false
                    onSelect(pack.packageName)
                }
            }
        }
    }
}

@Composable
fun WallpaperPickerScreen(
    copy: Copy,
    settings: LauncherSettings,
    alpha: Float,
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onAddCustom: (Uri) -> Unit,
    onDeleteCustom: (String) -> Unit,
) {
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            runCatching {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            onAddCustom(it)
        }
    }
    val options = remember(settings.customWallpaperUris) {
        wallpaperOptions() + settings.customWallpaperUris.mapIndexed { index, uri ->
            WallpaperOption(customWallpaperKey(uri), copy.text("Власні ${index + 1}", "Custom ${index + 1}", "Свои ${index + 1}"), uri = uri)
        }
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(copy.text("Шпалери", "Wallpapers", "Обои"), color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text(
            copy.text("Натисніть на прев'ю, щоб установити шпалери", "Tap a preview to set the wallpaper", "Нажмите на превью, чтобы установить обои"),
            color = Color.White.copy(alpha = 0.78f),
            fontSize = 15.sp,
            lineHeight = 20.sp,
        )
    }
    Spacer(Modifier.height(10.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        WallpaperActionButton(copy.text("Додати власні", "Add custom", "Добавить свои"), Color(0xFF1FA4FF)) { imagePicker.launch(arrayOf("image/*")) }
        WallpaperActionButton(copy.text("Прибрати", "Remove", "Убрать"), Color(0xFFFF5F5F)) { onSelect("warm_orange") }
    }
    Spacer(Modifier.height(12.dp))
    options.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            row.forEach { option ->
                WallpaperPreview(
                    option = option,
                    selected = option.key == settings.wallpaperKey,
                    alpha = alpha,
                    onClick = { onSelect(option.key) },
                    onDelete = option.uri?.let { uri ->
                        {
                            runCatching {
                                context.contentResolver.releasePersistableUriPermission(Uri.parse(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            onDeleteCustom(uri)
                        }
                    },
                    modifier = Modifier.weight(1f),
                )
            }
            repeat(2 - row.size) {
                Spacer(Modifier.weight(1f))
            }
        }
        Spacer(Modifier.height(10.dp))
    }
}

@Composable
fun WallpaperActionButton(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, color.copy(alpha = 0.86f), RoundedCornerShape(22.dp))
            .noPressClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = color, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun WallpaperPreview(option: WallpaperOption, selected: Boolean, alpha: Float, onClick: () -> Unit, onDelete: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier.noPressClickable(onClick = onClick), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.78f)
                .clip(RoundedCornerShape(14.dp))
                .border(
                    width = if (selected) 2.dp else if (LocalTileStrokeEnabled.current) 1.dp else 0.dp,
                    color = if (selected) Color(0xFF22E8F0) else LocalTileStrokeColor.current,
                    shape = RoundedCornerShape(14.dp),
                ),
        ) {
            Image(
                painter = option.uri?.let { rememberUriPainter(it) } ?: painterResource(option.drawable ?: R.drawable.wallpaper_warm_orange),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            onDelete?.let { delete ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(5.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.58f))
                        .noPressClickable(onClick = delete),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("×", color = Color.White, fontSize = 18.sp, lineHeight = 18.sp, textAlign = TextAlign.Center)
                }
            }
            if (selected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22E8F0).copy(alpha = alpha.coerceIn(0.55f, 0.95f))),
                )
            }
        }
        Text(option.label, color = Color.White, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun InterfaceSettings(
    copy: Copy,
    settings: LauncherSettings,
    onSettingsChange: (LauncherSettings) -> Unit,
    onResetHomeScreen: () -> Unit,
    hiddenApps: List<LauncherAppInfo>,
    onShowHiddenApps: () -> Unit,
) {
    var languageMenuExpanded by remember { mutableStateOf(false) }

    SettingsCard(copy.language, copy.text("Оберіть мову інтерфейсу лаунчера", "Choose the launcher interface language", "Выберите язык интерфейса лаунчера"))
    Box {
        SettingsSmallButton(copy.languageName(settings.languageMode), Color(0xFF22E8F0)) {
            languageMenuExpanded = true
        }
        LauncherDropdownMenu(
            expanded = languageMenuExpanded,
            onDismissRequest = { languageMenuExpanded = false },
        ) {
            LanguageMode.entries.forEach { mode ->
                LauncherDropdownItem(
                    label = copy.languageName(mode),
                    color = if (settings.languageMode == mode) Color(0xFF22E8F0) else Color.White,
                ) {
                    languageMenuExpanded = false
                    onSettingsChange(settings.copy(languageMode = mode))
                }
            }
        }
    }
    SettingsSwitchCard(copy.showClock, copy.text("Показувати стандартний віджет годинника на головному екрані", "Show the standard clock widget on the home screen", "Показывать стандартный виджет часов на главном экране"), settings.showClock) {
        onSettingsChange(settings.copy(showClock = it))
    }
    SettingsSwitchCard(copy.showWeather, copy.text("Показувати стандартний віджет погоди на головному екрані", "Show the standard weather widget on the home screen", "Показывать стандартный виджет погоды на главном экране"), settings.showWeather) {
        onSettingsChange(settings.copy(showWeather = it))
    }
    SettingsSwitchCard(copy.text("Показувати кількість додатків", "Show app count", "Показывать количество приложений"), copy.text("Рядок із кількістю встановлених додатків у меню", "Shows the installed app count in the menu", "Строка с количеством установленных приложений в меню"), settings.showAppCount) {
        onSettingsChange(settings.copy(showAppCount = it))
    }
    SettingsSwitchCard(copy.text("Вирівнювання праворуч", "Align right", "Выравнивание справа"), copy.text("Притискати іконки та віджети до правого краю екрана", "Align icons and widgets to the right edge", "Прижимать иконки и виджеты к правому краю экрана"), settings.alignHomeRight) {
        onSettingsChange(settings.copy(alignHomeRight = it))
    }
    SettingsCard(copy.text("Додатки головного екрана", "Home screen apps", "Приложения главного экрана"), copy.text("Повертає початковий набір додатків на головному екрані", "Restores the initial set of home screen apps", "Возвращает начальный набор приложений на главном экране"))
    SettingsSmallButton(copy.text("Скинути додатки головного екрана", "Reset home screen apps", "Сбросить приложения главного экрана"), Color(0xFFFFB0A8), onResetHomeScreen)
    SettingsCard(copy.text("Приховані додатки", "Hidden apps", "Скрытые приложения"), copy.text("Приховано: ${hiddenApps.size}. Довге натискання в списку поверне додаток.", "Hidden: ${hiddenApps.size}. Long-press in the list to restore an app.", "Скрыто: ${hiddenApps.size}. Долгое нажатие в списке вернет приложение."))
    SettingsSmallButton(copy.text("Показати всі приховані додатки", "Show all hidden apps", "Показать все скрытые приложения"), Color.White.copy(alpha = 0.55f)) {
        onShowHiddenApps()
    }
}

@Composable
fun AppMenuSettings(
    copy: Copy,
    settings: LauncherSettings,
    onSettingsChange: (LauncherSettings) -> Unit,
    allApps: List<LauncherAppInfo>,
    newAppKeys: Set<String>,
) {
    var showCreateFolder by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var renamingFolder by remember { mutableStateOf<String?>(null) }
    var renamingText by remember { mutableStateOf("") }
    val folderDragDropState = remember { mutableStateOf<DragDropData?>(null) }
    val density = LocalDensity.current
    
    val currentOrder = remember(settings.folderOrder, allApps, newAppKeys, settings.customFolders) {
        buildFolders(allApps, newAppKeys, settings.customFolders, settings.folderOrder).map { it.title }
    }
    val localOrder = remember { mutableStateListOf<String>() }
    
    LaunchedEffect(isEditing, currentOrder) {
        if (!isEditing) {
            localOrder.clear()
            localOrder.addAll(currentOrder)
        }
    }

    SliderSettingsCard(copy.text("Розмір іконок у папках", "Folder icon size", "Размер иконок в папках"), copy.text("Змінює масштаб додатків всередині відкритих папок", "Changes the app scale inside open folders", "Меняет масштаб приложений внутри открытых папок"), settings.folderIconScale, valueRange = HOME_ICON_SCALE_MIN..HOME_ICON_SCALE_MAX) {
        onSettingsChange(settings.copy(folderIconScale = it.coerceIn(HOME_ICON_SCALE_MIN, HOME_ICON_SCALE_MAX)))
    }
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingsWideButton(copy.text("Нова папка", "New folder", "Новая папка")) { showCreateFolder = !showCreateFolder; renamingFolder = null }
        SettingsWideButton(if (isEditing) copy.text("Завершити", "Done", "Завершить") else copy.text("Редагувати", "Edit", "Редактировать")) {
            if (isEditing) {
                onSettingsChange(settings.copy(folderOrder = localOrder.toList()))
            }
            isEditing = !isEditing
        }
    }

    if (showCreateFolder) {
        TileContainer(modifier = Modifier.fillMaxWidth(), alpha = LocalTileAlpha.current) {
            Column(Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 12.dp)) {
                TextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Назва папки", color = Color.White.copy(alpha = 0.4f), fontSize = 20.sp) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF22E8F0),
                        focusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.2f),
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { showCreateFolder = false; newFolderName = "" }) {
                        Text(copy.cancel, color = Color(0xFF1FA4FF), fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.width(12.dp))
                    TextButton(onClick = {
                        if (newFolderName.isNotBlank() && !settings.customFolders.containsKey(newFolderName)) {
                            val folderName = newFolderName.trim()
                            val nextMap = settings.customFolders + (folderName to emptyList<String>())
                            val nextOrder = (settings.folderOrder + folderName).distinct()
                            onSettingsChange(settings.copy(customFolders = nextMap, folderOrder = nextOrder))
                        }
                        showCreateFolder = false
                        newFolderName = ""
                    }) {
                        Text(copy.text("Створити", "Create", "Создать"), color = Color.White.copy(alpha = 0.7f), fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }

    if (renamingFolder != null) {
        TileContainer(modifier = Modifier.fillMaxWidth(), alpha = LocalTileAlpha.current) {
            Column(Modifier.padding(start = 12.dp, top = 10.dp, end = 12.dp, bottom = 6.dp)) {
                Text(copy.text("Перейменувати «$renamingFolder»", "Rename \"$renamingFolder\"", "Переименовать «$renamingFolder»"), color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(bottom = 4.dp))
                TextField(
                    value = renamingText,
                    onValueChange = { renamingText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(copy.text("Нова назва", "New name", "Новое название"), color = Color.White.copy(alpha = 0.4f), fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color(0xFF22E8F0),
                        focusedIndicatorColor = Color.White.copy(alpha = 0.3f),
                        unfocusedIndicatorColor = Color.White.copy(alpha = 0.2f),
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = { renamingFolder = null; renamingText = "" }) {
                        Text(copy.cancel, color = Color(0xFF1FA4FF), fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Spacer(Modifier.width(6.dp))
                    TextButton(onClick = {
                        val oldName = renamingFolder ?: ""
                        if (renamingText.isNotBlank() && renamingText != oldName) {
                            val apps = settings.customFolders[oldName] ?: emptyList()
                            val nextFolders = settings.customFolders - oldName + (renamingText.trim() to apps)
                            val nextOrder = settings.folderOrder.map { if (it == oldName) renamingText.trim() else it }
                            onSettingsChange(settings.copy(customFolders = nextFolders, folderOrder = nextOrder))
                        }
                        renamingFolder = null
                        renamingText = ""
                    }) {
                        Text(copy.save, color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }

    val titlesToShow = if (isEditing) localOrder else currentOrder
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .height(((titlesToShow.size + 1) / 2 * 61).dp),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        items(titlesToShow, key = { it }, contentType = { "settings_folder_row" }) { name ->
            val isDragging = folderDragDropState.value?.key == name
            val offset = if (isDragging) folderDragDropState.value?.offset ?: 0f else 0f
            val isCustom = settings.customFolders.containsKey(name)
            val appCount = if (isCustom) settings.customFolders[name]?.size else null
            
            Box(
                Modifier
                    .zIndex(if (isDragging) 10f else 1f)
                    .graphicsLayer { translationY = offset }
                    .then(if (isDragging) Modifier else Modifier.animateItem())
                    .pointerInput(isEditing, name) {
                        if (isEditing) {
                            val reorderStep = with(density) { 54.dp.toPx() }
                            detectDragGestures(
                                onDragStart = { folderDragDropState.value = DragDropData(name) },
                                onDragEnd = {
                                    folderDragDropState.value = null
                                    onSettingsChange(settings.copy(folderOrder = localOrder.toList()))
                                },
                                onDragCancel = { folderDragDropState.value = null },
                            ) { change, dragAmount ->
                            change.consume()
                                val current = folderDragDropState.value ?: return@detectDragGestures
                                var nextOffset = current.offset + dragAmount.y
                                var currentIndex = localOrder.indexOf(current.key)
                                if (currentIndex == -1) return@detectDragGestures

                                while (nextOffset > reorderStep && currentIndex < localOrder.lastIndex) {
                                    val item = localOrder.removeAt(currentIndex)
                                    localOrder.add(currentIndex + 1, item)
                                    currentIndex += 1
                                    nextOffset -= reorderStep
                                }
                                while (nextOffset < -reorderStep && currentIndex > 0) {
                                    val item = localOrder.removeAt(currentIndex)
                                    localOrder.add(currentIndex - 1, item)
                                    currentIndex -= 1
                                    nextOffset += reorderStep
                                }

                                folderDragDropState.value = current.copy(offset = nextOffset)
                            }
                        }
                    }
            ) {
                EditableFolderRow(
                    label = localizedFolderTitle(name, copy),
                    appCount = appCount,
                    isEditing = isEditing,
                    onEdit = { 
                        renamingFolder = name
                        renamingText = name
                        showCreateFolder = false
                    },
                    onDelete = { 
                        if (isCustom) {
                            onSettingsChange(settings.copy(
                                customFolders = settings.customFolders - name,
                                folderOrder = settings.folderOrder.filterNot { it == name },
                            ))
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun EditableFolderRow(
    label: String, 
    appCount: Int?, 
    isEditing: Boolean, 
    onEdit: () -> Unit, 
    onDelete: () -> Unit
) {
    TileContainer(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        alpha = LocalTileAlpha.current
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            
            if (isEditing) {
                IconButton(onClick = onEdit, modifier = Modifier.size(24.dp)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings_24),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(18.dp)
                    )
                }
                if (appCount != null) { // Тільки кастомні папки можна видаляти
                    Spacer(Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Text("×", color = Color(0xFFFFB0A8), fontSize = 21.sp, fontWeight = FontWeight.Light)
                    }
                }
            }
        }
    }
}

@Composable
fun FolderAppSelectorDialog(
    copy: Copy,
    folderName: String,
    allApps: List<LauncherAppInfo>,
    selectedKeys: Set<String>,
    onDismiss: () -> Unit,
    onSave: (Set<String>) -> Unit
) {
    val tempSelected = remember { mutableStateListOf<String>().apply { addAll(selectedKeys) } }
    val sortedApps = remember(allApps) { allApps.sortedBy { it.label.lowercase() } }
    
    CompositionLocalProvider(LocalTileAlpha provides OPEN_FOLDER_ALPHA) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noPressClickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .noPressClickable(enabled = false) {},
                shape = RoundedCornerShape(32.dp),
                color = LocalTileColor.current.copy(alpha = OPEN_FOLDER_ALPHA),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 26.dp)
                ) {
                    Text(
                        copy.text("Додати додатки до «$folderName»", "Add apps to \"$folderName\"", "Добавить приложения в «$folderName»"),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(20.dp))
                    
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                    ) {
                        items(sortedApps, key = { it.componentKey() }, contentType = { "folder_selector_app" }) { app ->
                            val key = app.componentKey()
                            val selected = tempSelected.contains(key)
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (selected) Color.White.copy(alpha = 0.12f) else Color.Transparent)
                                    .noPressClickable { if (selected) tempSelected.remove(key) else tempSelected.add(key) }
                                    .padding(vertical = 12.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box {
                                    Icon(
                                        rememberAppIconPainter(app, null),
                                        null, 
                                        tint = Color.Unspecified, 
                                        modifier = Modifier.size(64.dp)
                                    )
                                    if (selected) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(20.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF22E8F0))
                                                .border(1.dp, Color.White, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("✓", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    localizedAppLabel(app, copy), 
                                    color = Color.White, 
                                    fontSize = 13.sp, 
                                    maxLines = 1, 
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(60.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                            border = tileBorderStrokeOrNull(),
                        ) {
                            Text(copy.cancel, fontSize = 18.sp, color = Color.White.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Button(
                            onClick = { onSave(tempSelected.toSet()) },
                            modifier = Modifier.weight(1f).height(60.dp),
                            shape = RoundedCornerShape(26.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22E8F0).copy(alpha = 0.2f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22E8F0).copy(alpha = 0.6f)),
                        ) {
                            Text(copy.save, fontSize = 18.sp, color = Color(0xFF22E8F0), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionSettings(copy: Copy, onDefaultLauncher: () -> Unit, onNotificationAccess: () -> Unit, onAccessibility: () -> Unit) {
    val context = LocalContext.current
    
    // Стан дозволів
    val isDefault = remember { mutableStateOf(isDefaultLauncher(context)) }
    val isNotification = remember { mutableStateOf(isNotificationAccessGranted(context)) }
    val isAccessibility = remember { mutableStateOf(LauncherAccessibilityService.isEnabled(context)) }

    // Оновлюємо стан при поверненні в налаштування
    LaunchedEffect(Unit) {
        while(true) {
            isDefault.value = isDefaultLauncher(context)
            isNotification.value = isNotificationAccessGranted(context)
            isAccessibility.value = LauncherAccessibilityService.isEnabled(context)
            kotlinx.coroutines.delay(1000) // Перевіряємо кожну секунду поки відкрито
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        SettingsCard(
            title = copy.text("Лаунчер за замовчуванням", "Default launcher", "Лаунчер по умолчанию"),
            subtitle = if (isDefault.value) copy.text("PocketLauncher є основним лаунчером", "PocketLauncher is the main launcher", "PocketLauncher является основным лаунчером") else copy.text("Відкриває системне меню, де можна обрати лаунчер головного екрана", "Opens the system menu where you can choose a home launcher", "Открывает системное меню выбора лаунчера главного экрана"),
            onClick = onDefaultLauncher
        )
        if (!isDefault.value) {
            SettingsSmallButton(copy.text("Зробити лаунчером за замовчуванням", "Set as default launcher", "Сделать лаунчером по умолчанию"), Color(0xFFFF6E63), onDefaultLauncher)
        }

        SettingsCard(
            title = copy.text("Сповіщення", "Notifications", "Уведомления"),
            subtitle = if (isNotification.value) copy.text("Дозвіл надано. Позначки сповіщень відображаються.", "Permission granted. Notification badges are shown.", "Разрешение выдано. Значки уведомлений отображаются.") else copy.text("Необхідно для показу позначок на іконках додатків", "Required to show badges on app icons", "Нужно для показа значков на иконках приложений"),
            onClick = onNotificationAccess
        )

        SettingsCard(
            title = copy.text("Жести та системні дії", "Gestures and system actions", "Жесты и системные действия"),
            subtitle = if (isAccessibility.value) copy.text("Дозвіл надано. Жести можуть відкривати сповіщення та системні екрани.", "Permission granted. Gestures can open notifications and system screens.", "Разрешение выдано. Жесты могут открывать уведомления и системные экраны.") else copy.text("Необхідно для жестів сповіщень, недавніх додатків і системних дій", "Required for notification, recent-apps, and system-action gestures", "Нужно для жестов уведомлений, недавних приложений и системных действий"),
            onClick = onAccessibility
        )
    }
}

@Composable
fun SettingsCard(title: String, subtitle: String, onClick: () -> Unit = {}) {
    TileContainer(
        modifier = Modifier.fillMaxWidth().noPressClickable(onClick = onClick),
        alpha = LocalTileAlpha.current,
    ) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, color = Color.White, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(subtitle, color = Color.White.copy(alpha = 0.72f), fontSize = 15.sp, lineHeight = 21.sp)
        }
    }
}

@Composable
fun SliderSettingsCard(title: String, subtitle: String, value: Float, valueRange: ClosedFloatingPointRange<Float> = 0.35f..0.95f, onValueChange: (Float) -> Unit) {
    TileContainer(modifier = Modifier.fillMaxWidth(), alpha = LocalTileAlpha.current) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)) {
            Text(title, color = Color.White, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, lineHeight = 20.sp)
            Slider(value = value.coerceIn(valueRange.start, valueRange.endInclusive), onValueChange = onValueChange, valueRange = valueRange)
        }
    }
}

@Composable
fun SettingsSwitchCard(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    TileContainer(modifier = Modifier.fillMaxWidth(), alpha = LocalTileAlpha.current) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(title, color = Color.White, fontSize = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(subtitle, color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, lineHeight = 20.sp)
            }
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

@Composable
fun SettingsSmallButton(label: String, color: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(21.dp))
            .background(LocalTileColor.current.copy(alpha = LocalTileAlpha.current))
            .tileStrokeBorder(RoundedCornerShape(21.dp))
            .noPressClickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
    ) {
        Text(label, color = color, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun LauncherDropdownMenu(expanded: Boolean, onDismissRequest: () -> Unit, offset: DpOffset = DpOffset.Zero, content: @Composable () -> Unit) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        offset = offset,
        shape = RoundedCornerShape(24.dp),
        containerColor = LocalTileColor.current.copy(alpha = LocalTileAlpha.current),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        border = tileBorderStrokeOrNull(),
    ) {
        content()
    }
}

@Composable
fun LauncherContextMenu(expanded: Boolean, onDismissRequest: () -> Unit, position: IntOffset, content: @Composable () -> Unit) {
    if (!expanded) return

    Popup(
        alignment = Alignment.TopStart,
        offset = position,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = LocalTileColor.current.copy(alpha = LocalTileAlpha.current),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            border = tileBorderStrokeOrNull(),
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun LauncherDropdownItem(label: String, color: Color = Color.White, onClick: () -> Unit) {
    DropdownMenuItem(
        text = { Text(label, color = color, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        onClick = onClick,
    )
}

@Composable
fun RowScope.SettingsWideButton(label: String, onClick: () -> Unit) {
    Box(
        Modifier
            .weight(1f)
            .height(40.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(LocalTileColor.current.copy(alpha = LocalTileAlpha.current))
            .tileStrokeBorder(RoundedCornerShape(22.dp))
            .noPressClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SettingsListRow(label: String) {
    TileContainer(modifier = Modifier.fillMaxWidth().height(52.dp), alpha = LocalTileAlpha.current) {
        Box(Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
            Text(label, color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun FillColorPalette(copy: Copy, selectedColor: Color, customColor: Color, onColorChange: (Color, Color) -> Unit) {
    var customExpanded by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        TileContainer(modifier = Modifier.fillMaxWidth(), alpha = LocalTileAlpha.current) {
            Column(
                Modifier.fillMaxWidth().padding(start = 24.dp, top = 16.dp, end = 20.dp, bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StandardFillColors.forEach { (name, color) ->
                    FillColorRow(
                        label = localizedFillColorName(name, copy),
                        color = color,
                        selected = selectedColor.sameColorAs(color),
                        onClick = { onColorChange(color, customColor) },
                    )
                }
            }
        }

        AddCustomColorButton(copy) {
            customExpanded = !customExpanded
            if (customExpanded) {
                onColorChange(customColor, customColor)
            }
        }

        if (customExpanded) {
            CustomFillColorEditor(copy, customColor) { color ->
                onColorChange(color, color)
            }
        }
    }
}

@Composable
fun FillColorRow(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp).noPressClickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FillColorSwatch(color = color, selected = selected, onClick = onClick, size = 32.dp)
        Spacer(Modifier.width(12.dp))
        Text(label, color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun FillColorSwatch(color: Color, selected: Boolean, onClick: () -> Unit, size: Dp = 56.dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(color)
            .border(if (selected) 3.dp else 0.dp, Color.White, RoundedCornerShape(14.dp))
            .noPressClickable(onClick = onClick),
    )
}

@Composable
fun AddCustomColorButton(copy: Copy, onClick: () -> Unit) {
    TileContainer(
        modifier = Modifier.fillMaxWidth().height(54.dp).noPressClickable(onClick = onClick),
        alpha = LocalTileAlpha.current,
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Text("+", color = Color.White.copy(alpha = 0.78f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Text(copy.text("Додати свій колір", "Add custom color", "Добавить свой цвет"), color = Color.White, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
fun CustomFillColorEditor(copy: Copy, color: Color, onColorChange: (Color) -> Unit) {
    val red = color.channelRed()
    val green = color.channelGreen()
    val blue = color.channelBlue()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 6.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(62.dp).clip(RoundedCornerShape(14.dp)).background(color))
            Spacer(Modifier.width(16.dp))
            Text(copy.text("Свій колір", "Custom color", "Свой цвет"), color = Color.White, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        RgbSlider(copy.text("Червоний", "Red", "Красный"), red) { onColorChange(Color(it, green, blue)) }
        RgbSlider(copy.text("Зелений", "Green", "Зеленый"), green) { onColorChange(Color(red, it, blue)) }
        RgbSlider(copy.text("Синій", "Blue", "Синий"), blue) { onColorChange(Color(red, green, it)) }
    }
}

@Composable
fun RgbSlider(label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, fontSize = 15.sp, modifier = Modifier.width(81.dp), maxLines = 1, overflow = TextOverflow.Ellipsis)
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt().coerceIn(0, 255)) },
            valueRange = 0f..255f,
            modifier = Modifier.weight(1f),
        )
        Text(value.toString(), color = Color.White.copy(alpha = 0.78f), fontSize = 14.sp, modifier = Modifier.width(28.dp), textAlign = TextAlign.End)
    }
}
@Composable
fun SettingsAction(label: String, color: Color, action: () -> Unit) {
    Text(label, modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).noPressClickable(onClick = action).padding(vertical = 12.dp, horizontal = 6.dp), color = color, fontSize = 17.sp)
}

@Composable
fun TileContainer(modifier: Modifier = Modifier, alpha: Float = 0.76f, content: @Composable BoxScope.() -> Unit) {
    val shape = RoundedCornerShape(28.dp)
    Box(
        modifier.clip(shape).background(LocalTileColor.current.copy(alpha = alpha)).tileStrokeBorder(shape),
        content = content,
    )
}

@Composable
private fun Modifier.tileStrokeBorder(shape: Shape): Modifier =
    if (LocalTileStrokeEnabled.current) {
        border(1.dp, LocalTileStrokeColor.current, shape)
    } else {
        this
    }

@Composable
private fun tileBorderStrokeOrNull(): androidx.compose.foundation.BorderStroke? =
    if (LocalTileStrokeEnabled.current) {
        androidx.compose.foundation.BorderStroke(1.dp, LocalTileStrokeColor.current)
    } else {
        null
    }

@Composable
fun rememberDrawablePainter(drawable: android.graphics.drawable.Drawable): Painter = remember(drawable) {
    BitmapPainter(drawable.toBitmap().asImageBitmap())
}

@Composable
fun rememberAppIconPainter(app: LauncherAppInfo, iconPackPackage: String?): Painter {
    val context = LocalContext.current
    val appKey = app.componentKey()
    val themedCacheKey = iconPackPackage?.let { themedIconCacheKey(it, appKey) }
    val cachedThemedIcon = themedCacheKey?.let { key -> synchronized(iconCacheLock) { themedIconCache[key] } }
    val themedIcon by produceState<Drawable?>(initialValue = cachedThemedIcon, appKey, iconPackPackage) {
        value = if (iconPackPackage == null || themedCacheKey == null) {
            null
        } else if (synchronized(iconCacheLock) { themedIconCache.containsKey(themedCacheKey) }) {
            synchronized(iconCacheLock) { themedIconCache[themedCacheKey] }
        } else {
            withContext(Dispatchers.IO) { loadThemedIcon(context, iconPackPackage, app) }
        }
    }
    val activeIcon = themedIcon ?: app.icon
    val bitmapKey = if (themedIcon != null && themedCacheKey != null) {
        "themed|$themedCacheKey"
    } else {
        "default|$appKey|${app.icon.hashCode()}"
    }
    val bitmap = remember(bitmapKey, activeIcon) {
        synchronized(iconCacheLock) {
            appIconBitmapCache.getOrPut(bitmapKey) { activeIcon.toBitmap().asImageBitmap() }
        }
    }
    return remember(bitmap) { BitmapPainter(bitmap) }
}

@Composable
fun rememberUriPainter(uriString: String): Painter? {
    val context = LocalContext.current
    val painter by produceState<Painter?>(initialValue = null, uriString) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriString))?.use { stream ->
                    BitmapPainter(BitmapFactory.decodeStream(stream).asImageBitmap())
                }
            }.getOrNull()
        }
    }
    return painter
}

private fun LauncherAppInfo.componentKey(): String = componentName.flattenToString()
private fun gridHeightForApps(count: Int): Dp {
    val rows = (count + 3) / 4
    return if (rows == 0) 0.dp else (rows * 105 + (rows - 1) * 14).dp
}
private fun folderGridHeight(count: Int): Dp = ((count + 1) / 2 * 215).dp

private fun loadIconPacks(context: Context): List<IconPackInfo> {
    iconPackListCache?.let { return it }
    val packageManager = context.packageManager
    val defaultPack = IconPackInfo(null, "Default", null)
    @Suppress("DEPRECATION")
    val installed = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
    val packs = installed.mapNotNull { appInfo ->
        val packageName = appInfo.packageName
        if (packageName == context.packageName || !hasIconPackFilter(context, packageName)) {
            return@mapNotNull null
        }
        IconPackInfo(
            packageName = packageName,
            label = packageManager.getApplicationLabel(appInfo).toString(),
            icon = null,
        )
    }.sortedBy { it.label.lowercase(Locale.getDefault()) }
    return (listOf(defaultPack) + packs).also { iconPackListCache = it }
}

private fun hasIconPackFilter(context: Context, packageName: String): Boolean {
    val assets = runCatching { context.packageManager.getResourcesForApplication(packageName).assets }.getOrNull() ?: return false
    return listOf("appfilter.xml", "drawable.xml").any { assetName ->
        runCatching { assets.open(assetName).use { true } }.getOrDefault(false)
    }
}

private fun themedIconCacheKey(iconPackPackage: String, appKey: String): String = "$iconPackPackage|$appKey"

private fun loadThemedIcon(context: Context, iconPackPackage: String, app: LauncherAppInfo): Drawable? {
    val cacheKey = themedIconCacheKey(iconPackPackage, app.componentKey())
    synchronized(iconCacheLock) {
        themedIconCache[cacheKey]?.let { return it }
        if (themedIconCache.containsKey(cacheKey)) return null
    }

    val resources = runCatching { context.packageManager.getResourcesForApplication(iconPackPackage) }.getOrNull() ?: return null
    val filter = iconPackFilterCache.getOrPut(iconPackPackage) { parseIconPackFilter(context, iconPackPackage) }
    val drawableName = filter[app.componentName.flattenToString()]
        ?: filter[app.componentName.packageName]
        ?: return null.also { synchronized(iconCacheLock) { themedIconCache[cacheKey] = null } }
    val drawableId = resources.getIdentifier(drawableName, "drawable", iconPackPackage)
        .takeIf { it != 0 }
        ?: resources.getIdentifier(drawableName, "mipmap", iconPackPackage).takeIf { it != 0 }
        ?: return null.also { synchronized(iconCacheLock) { themedIconCache[cacheKey] = null } }
    return runCatching { resources.getDrawable(drawableId, null) }
        .getOrNull()
        .also { synchronized(iconCacheLock) { themedIconCache[cacheKey] = it } }
}

private fun parseIconPackFilter(context: Context, packageName: String): Map<String, String> {
    val resources = runCatching { context.packageManager.getResourcesForApplication(packageName) }.getOrNull() ?: return emptyMap()
    val result = mutableMapOf<String, String>()
    listOf("appfilter.xml", "drawable.xml").forEach { assetName ->
        runCatching {
            resources.assets.open(assetName).use { stream ->
                val parser = Xml.newPullParser().apply { setInput(stream, null) }
                var event = parser.eventType
                while (event != XmlPullParser.END_DOCUMENT) {
                    if (event == XmlPullParser.START_TAG && parser.name == "item") {
                        val component = parser.getAttributeValue(null, "component")
                        val drawable = parser.getAttributeValue(null, "drawable")
                        if (!component.isNullOrBlank() && !drawable.isNullOrBlank()) {
                            normalizeIconComponent(component)?.let { result[it] = drawable }
                        }
                    }
                    event = parser.next()
                }
            }
        }
    }
    return result
}

private fun normalizeIconComponent(component: String): String? {
    val raw = component.removePrefix("ComponentInfo{").removeSuffix("}")
    val name = android.content.ComponentName.unflattenFromString(raw) ?: return raw.takeIf { "/" !in it && it.isNotBlank() }
    return name.flattenToString()
}

private fun loadWidgetGroups(context: Context): List<WidgetAppGroup> {
    val packageManager = context.packageManager
    val providers = AppWidgetManager.getInstance(context).installedProviders
    return providers
        .groupBy { it.provider.packageName }
        .mapNotNull { (packageName, packageProviders) ->
            val appInfo = runCatching { packageManager.getApplicationInfo(packageName, 0) }.getOrNull()
            val label = appInfo?.let { packageManager.getApplicationLabel(it).toString() }
                ?: packageProviders.firstOrNull()?.loadLabel(packageManager)?.toString()
                ?: packageName
            val icon = runCatching { packageManager.getApplicationIcon(packageName) }.getOrElse { packageManager.defaultActivityIcon }
            WidgetAppGroup(
                packageName = packageName,
                label = label,
                icon = icon,
                providers = packageProviders.sortedBy { it.loadLabel(packageManager).toString() },
            )
        }
        .sortedBy { it.label.lowercase(Locale.getDefault()) }
}

private fun defaultHomeApps(apps: List<LauncherAppInfo>): List<LauncherAppInfo> {
    val cameraPackages = listOf(
        "com.android.camera",
        "com.google.android.GoogleCamera",
        "com.sec.android.app.camera",
    )
    val phonePackages = listOf(
        "com.android.dialer",
        "com.google.android.dialer",
        "com.samsung.android.dialer",
    )
    val messagePackages = listOf(
        "com.android.mms",
        "com.google.android.apps.messaging",
        "com.samsung.android.messaging",
    )
    return listOf(cameraPackages, phonePackages, messagePackages)
        .mapNotNull { packages -> packages.firstNotNullOfOrNull { packageName -> apps.firstOrNull { it.packageName == packageName } } }
        .distinctBy { it.packageName }
}

private fun defaultHomeKeys(apps: List<LauncherAppInfo>): List<String> =
    listOf("WIDGET_CLOCK") +
        defaultHomeApps(apps).map { it.componentKey() } +
        listOf("WIDGET_WEATHER")

private fun launchApp(context: Context, app: LauncherAppInfo) {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        component = app.componentName
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
    }
    runCatching { context.startActivity(intent) }.onFailure {
        Toast.makeText(context, "Could not open ${app.label}", Toast.LENGTH_SHORT).show()
    }
}

private fun requestAppUninstall(context: Context, app: LauncherAppInfo) {
    val intent = Intent(Intent.ACTION_DELETE).apply {
        data = Uri.fromParts("package", app.packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    runCatching { context.startActivity(intent) }.onFailure {
        Toast.makeText(context, "Could not open uninstall", Toast.LENGTH_SHORT).show()
    }
}

private fun openDefaultLauncherSettings(context: Context) {
    val intent = Intent(Settings.ACTION_HOME_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }.onFailure {
        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

private fun openNotificationShade(context: Context): Boolean {
    if (LauncherAccessibilityService.openNotifications()) {
        return true
    }
    return runCatching {
        val statusBarService = context.getSystemService("statusbar") ?: return@runCatching false
        val expandMethod = statusBarService.javaClass.getMethod("expandNotificationsPanel")
        expandMethod.invoke(statusBarService)
        true
    }.getOrDefault(false)
}

private fun openSystemRecents(context: Context) {
    if (!LauncherAccessibilityService.openRecents()) {
        Toast.makeText(context, "Enable accessibility permission for the recent apps gesture", Toast.LENGTH_SHORT).show()
    }
}

private fun appWidgetKey(widgetId: Int): String = "$APP_WIDGET_KEY_PREFIX$widgetId"
private fun appWidgetIdFromKey(key: String): Int? =
    key.removePrefix(APP_WIDGET_KEY_PREFIX).takeIf { key.startsWith(APP_WIDGET_KEY_PREFIX) }?.toIntOrNull()

private fun deleteHomeWidget(context: Context, key: String) {
    val widgetId = appWidgetIdFromKey(key) ?: return
    runCatching { AppWidgetHost(context, APP_WIDGET_HOST_ID).deleteAppWidgetId(widgetId) }
}

private fun loadStringSet(context: Context, key: String): Set<String> = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getStringSet(key, emptySet()) ?: emptySet()
private fun saveStringSet(context: Context, key: String, value: Set<String>) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit { putStringSet(key, value) }
private fun loadHomeAppKeys(context: Context): List<String> {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    val ordered = prefs.getString("home_apps_order", null)
        ?.lines()
        ?.filter { it.isNotBlank() }
        ?.distinct()
        .orEmpty()
    return normalizeHomeAppKeys(ordered.ifEmpty { loadStringSet(context, "home_apps").toList() })
}

private data class DragDropData(val key: String, val offset: Float = 0f)
private fun saveHomeAppKeys(context: Context, value: List<String>) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
    val ordered = normalizeHomeAppKeys(value)
    putString("home_apps_order", ordered.joinToString("\n"))
    putStringSet("home_apps", ordered.toSet())
}

private fun normalizeHomeAppKeys(value: List<String>): List<String> {
    val ordered = value
        .filter { it.isNotBlank() && it != HOME_WIDGETS_KEY && it != HOME_SETTINGS_KEY }
        .distinct()
        .toMutableList()
    if ("WIDGET_CLOCK" !in ordered) ordered.add(0, "WIDGET_CLOCK")
    if ("WIDGET_WEATHER" !in ordered) ordered.add("WIDGET_WEATHER")
    return ordered
}
private fun loadWeatherLocation(context: Context): String = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("weather_location", DEFAULT_LOCATION) ?: DEFAULT_LOCATION
private fun saveWeatherLocation(context: Context, location: String) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit { putString("weather_location", location) }

private fun wallpaperOptions(): List<WallpaperOption> = listOf(
    WallpaperOption("light_blue", "Світло-блакитні", drawable = R.drawable.wallpaper_light_blue),
    WallpaperOption("dark_blue", "Темно-сині", drawable = R.drawable.wallpaper_dark_blue),
    WallpaperOption("warm_orange", "Теплі", drawable = R.drawable.wallpaper_warm_orange),
    WallpaperOption("dark_green", "Темно-зелені", drawable = R.drawable.wallpaper_dark_green),
)

private fun wallpaperDrawable(key: String): Int = wallpaperOptions().firstOrNull { it.key == key }?.drawable ?: R.drawable.wallpaper_warm_orange
private fun customWallpaperKey(uri: String): String = "custom:$uri"
private fun customWallpaperUri(key: String): String? = key.removePrefix("custom:").takeIf { key.startsWith("custom:") && it.isNotBlank() }
private fun adaptiveTileStroke(wallpaperKey: String): Color =
    if (wallpaperKey == "light_blue") {
        Color.Black.copy(alpha = 0.62f)
    } else {
        Color.White.copy(alpha = 0.78f)
    }
private fun Color.sameColorAs(other: Color): Boolean = toArgb() == other.toArgb()
private fun Color.channelRed(): Int = (toArgb() ushr 16) and 0xFF
private fun Color.channelGreen(): Int = (toArgb() ushr 8) and 0xFF
private fun Color.channelBlue(): Int = toArgb() and 0xFF

private fun loadLauncherSettings(context: Context): LauncherSettings {
    val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    return LauncherSettings(
        showClock = prefs.getBoolean("show_clock", true),
        showWeather = prefs.getBoolean("show_weather", true),
        showAppCount = prefs.getBoolean("show_app_count", true),
        alignHomeRight = prefs.getBoolean("align_home_right", false),
        languageMode = runCatching { LanguageMode.valueOf(prefs.getString("language_mode", LanguageMode.System.name) ?: LanguageMode.System.name) }.getOrDefault(LanguageMode.System),
        tileAlpha = prefs.getFloat("tile_alpha", 0.76f),
        tileColor = Color(prefs.getInt("tile_color", DefaultTileColor.toArgb())),
        customTileColor = Color(prefs.getInt("custom_tile_color", Color(0xFF4632A5).toArgb())),
        wallpaperKey = prefs.getString("wallpaper_key", "warm_orange") ?: "warm_orange",
        customWallpaperUris = prefs.getString("custom_wallpaper_uris", "").orEmpty().lines().filter { it.isNotBlank() },
        iconPackPackage = prefs.getString("icon_pack_package", null),
        homeIconScale = prefs.getFloat("home_icon_scale", 1.0f).coerceIn(HOME_ICON_SCALE_MIN, HOME_ICON_SCALE_MAX),
        clockWidth = prefs.getFloat("clock_width", 304f),
        clockHeight = prefs.getFloat("clock_height", 182f),
        weatherWidth = prefs.getFloat("weather_width", 456f),
        weatherHeight = prefs.getFloat("weather_height", 326f),
        appWidgetSizes = loadAppWidgetSizes(prefs.getString("app_widget_sizes_json", "{}") ?: "{}"),
        customFolders = runCatching {
            val json = prefs.getString("custom_folders_json", "{}") ?: "{}"
            val obj = JSONObject(json)
            val map = mutableMapOf<String, List<String>>()
            obj.keys().forEach { name ->
                val arr = obj.getJSONArray(name)
                val keys = List(arr.length()) { i -> arr.getString(i) }
                map[name] = keys
            }
            map
        }.getOrDefault(emptyMap()),
        folderOrder = prefs.getString("folder_order", "").orEmpty().lines().filter { it.isNotBlank() },
        folderIconScale = prefs.getFloat("folder_icon_scale", 1.0f),
    )
}

private fun saveLauncherSettings(context: Context, settings: LauncherSettings) {
    context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
        putBoolean("show_clock", settings.showClock)
        putBoolean("show_weather", settings.showWeather)
        putBoolean("show_app_count", settings.showAppCount)
        putBoolean("align_home_right", settings.alignHomeRight)
        putString("language_mode", settings.languageMode.name)
        putFloat("tile_alpha", settings.tileAlpha)
        putInt("tile_color", settings.tileColor.toArgb())
        putInt("custom_tile_color", settings.customTileColor.toArgb())
        putString("wallpaper_key", settings.wallpaperKey)
        putString("custom_wallpaper_uris", settings.customWallpaperUris.joinToString("\n"))
        if (settings.iconPackPackage == null) {
            remove("icon_pack_package")
        } else {
            putString("icon_pack_package", settings.iconPackPackage)
        }
        putFloat("home_icon_scale", settings.homeIconScale.coerceIn(HOME_ICON_SCALE_MIN, HOME_ICON_SCALE_MAX))
        putFloat("clock_width", settings.clockWidth)
        putFloat("clock_height", settings.clockHeight)
        putFloat("weather_width", settings.weatherWidth)
        putFloat("weather_height", settings.weatherHeight)
        putString("app_widget_sizes_json", saveAppWidgetSizes(settings.appWidgetSizes))
        putString("custom_folders_json", JSONObject(settings.customFolders).toString())
        putString("folder_order", settings.folderOrder.joinToString("\n"))
        putFloat("folder_icon_scale", settings.folderIconScale)
    }
}

private fun loadAppWidgetSizes(jsonText: String): Map<String, WidgetSize> = runCatching {
    val json = JSONObject(jsonText)
    buildMap {
        json.keys().forEach { key ->
            val sizeJson = json.getJSONObject(key)
            put(
                key,
                WidgetSize(
                    width = sizeJson.optDouble("width", 280.0).toFloat(),
                    height = sizeJson.optDouble("height", 140.0).toFloat(),
                ),
            )
        }
    }
}.getOrDefault(emptyMap())

private fun saveAppWidgetSizes(sizes: Map<String, WidgetSize>): String {
    val json = JSONObject()
    sizes.forEach { (key, size) ->
        json.put(
            key,
            JSONObject()
                .put("width", size.width.toDouble())
                .put("height", size.height.toDouble()),
        )
    }
    return json.toString()
}

private suspend fun getWeather(location: String): WeatherReport = withContext(Dispatchers.IO) {
    val now = System.currentTimeMillis()
    weatherCache?.takeIf { it.location == location && now - it.timestamp < 30 * 60 * 1000L }?.report
        ?: fetchWeather(location).also { weatherCache = WeatherCacheEntry(location, now, it) }
}

private fun fetchWeather(location: String): WeatherReport = runCatching {
    val encoded = URLEncoder.encode(location, "UTF-8")
    val geo = readJson("https://geocoding-api.open-meteo.com/v1/search?name=$encoded&count=1&language=uk&format=json")
    val first = geo.getJSONArray("results").getJSONObject(0)
    val latitude = first.getDouble("latitude")
    val longitude = first.getDouble("longitude")
    val display = listOf(first.optString("name"), first.optString("admin1"), first.optString("country")).filter { it.isNotBlank() }.joinToString(", ")
    val forecast = readJson("https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current=temperature_2m,weather_code&daily=weather_code,temperature_2m_max,temperature_2m_min&timezone=auto&forecast_days=7")
    val current = forecast.getJSONObject("current")
    val daily = forecast.getJSONObject("daily")
    val times = daily.getJSONArray("time")
    val maxTemps = daily.getJSONArray("temperature_2m_max")
    val minTemps = daily.getJSONArray("temperature_2m_min")
    WeatherReport(
        temperature = current.getDouble("temperature_2m").toInt(),
        condition = weatherCondition(current.getInt("weather_code")),
        location = display.ifBlank { location },
        forecast = List(min(7, times.length())) { index -> ForecastDay(dayLabel(index), maxTemps.getDouble(index).toInt(), minTemps.getDouble(index).toInt()) },
    )
}.getOrElse {
    WeatherReport(20, "Хмарно", location, listOf("Вт", "Ср", "Чт", "Пт", "Сб", "Нд", "Пн").mapIndexed { index, day -> ForecastDay(day, 18 + index, 12 + index) })
}

private fun readJson(url: String): JSONObject {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.connectTimeout = 7000
    connection.readTimeout = 7000
    connection.requestMethod = "GET"
    return connection.inputStream.bufferedReader().use { JSONObject(it.readText()) }
}

private fun weatherCondition(code: Int): String = when (code) {
    0 -> "Ясно"
    1, 2 -> "Мінлива хмарність"
    3 -> "Хмарно"
    45, 48 -> "Туман"
    51, 53, 55, 56, 57 -> "Мряка"
    61, 63, 65, 66, 67, 80, 81, 82 -> "Дощ"
    71, 73, 75, 77, 85, 86 -> "Сніг"
    95, 96, 99 -> "Гроза"
    else -> "Хмарно"
}

private fun dayLabel(index: Int): String = listOf("Сь", "Зв", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд").getOrElse(index) { "" }



private fun localizedFillColorName(name: String, copy: Copy): String = when (name) {
    "Графітовий сірий" -> copy.text("Графітовий сірий", "Graphite gray", "Графитовый серый")
    "Холодний попіл" -> copy.text("Холодний попіл", "Cool ash", "Холодный пепел")
    "Світлий туман" -> copy.text("Світлий туман", "Light mist", "Светлый туман")
    "Сталевий" -> copy.text("Сталевий", "Steel", "Стальной")
    "Сланцевий" -> copy.text("Сланцевий", "Slate", "Сланцевый")
    else -> name
}
private fun localizedAppLabel(app: LauncherAppInfo, copy: Copy): String = when (app.packageName) {
    "com.android.camera", "com.google.android.GoogleCamera" -> copy.text("Камера", "Camera", "Камера")
    "com.android.contacts", "com.google.android.contacts" -> copy.text("Контакти", "Contacts", "Контакты")
    "com.android.dialer", "com.google.android.dialer" -> copy.text("Телефон", "Phone", "Телефон")
    "com.android.mms", "com.google.android.apps.messaging" -> copy.text("Повідомлення", "Messages", "Сообщения")
    "com.android.calendar", "com.google.android.calendar" -> copy.text("Календар", "Calendar", "Календарь")
    "com.android.calculator2", "com.google.android.calculator" -> copy.text("Калькулятор", "Calculator", "Калькулятор")
    "com.android.clock", "com.google.android.deskclock" -> copy.text("Годинник", "Clock", "Часы")
    "com.android.settings" -> copy.text("Налаштування", "Settings", "Настройки")
    "com.google.android.apps.photos" -> copy.text("Фото", "Photos", "Фото")
    "com.google.android.gm" -> copy.text("Пошта", "Gmail", "Почта")
    "com.google.android.apps.maps" -> copy.text("Карти", "Maps", "Карты")
    "com.google.android.apps.docs" -> copy.text("Документи", "Docs", "Документы")
    "com.google.android.apps.sheets" -> copy.text("Таблиці", "Sheets", "Таблицы")
    "com.google.android.apps.slides" -> copy.text("Презентації", "Slides", "Презентации")
    "com.google.android.keep" -> copy.text("Нотатки", "Keep Notes", "Заметки")
    "com.google.android.apps.translate" -> copy.text("Перекладач", "Translate", "Переводчик")
    "com.google.android.apps.walletnfcrel" -> copy.text("Гаманець", "Wallet", "Кошелек")
    "com.android.vending" -> copy.text("Play Маркет", "Play Store", "Play Маркет")
    "com.google.android.apps.nbu.files" -> copy.text("Файли", "Files", "Файлы")
    "com.google.android.apps.authenticator2" -> copy.text("Автентифікатор", "Authenticator", "Аутентификатор")
    else -> app.label
}
private fun localizedFolderTitle(title: String, copy: Copy): String = when (title) {
    "Нові додатки" -> copy.text("Нові додатки", "New apps", "Новые приложения")
    "Google сервіси" -> copy.text("Google сервіси", "Google services", "Сервисы Google")
    "Соцмережі" -> copy.text("Соцмережі", "Social", "Соцсети")
    "Ігри" -> copy.text("Ігри", "Games", "Игры")
    "Фінанси" -> copy.text("Фінанси", "Finance", "Финансы")
    "Шопінг" -> copy.text("Шопінг", "Shopping", "Покупки")
    "Інструменти" -> copy.text("Інструменти", "Tools", "Инструменты")
    "Відео та Музика" -> copy.text("Відео та Музика", "Video and Music", "Видео и Музыка")
    "Подорожі" -> copy.text("Подорожі", "Travel", "Путешествия")
    "Головне" -> copy.text("Головне", "Main", "Главное")
    "Усі додатки" -> copy.text("Усі додатки", "All apps", "Все приложения")
    else -> title
}

private fun localizedWeatherLocation(location: String, copy: Copy): String {
    val resolved = if (copy.mode == LanguageMode.System) LanguageMode.Ukrainian else copy.mode
    if (resolved == LanguageMode.Ukrainian) return location
    val replacements = when (resolved) {
        LanguageMode.English -> listOf(
            "Бровари" to "Brovary",
            "Київська область" to "Kyiv Oblast",
            "Україна" to "Ukraine",
            "Київ" to "Kyiv",
        )
        LanguageMode.Russian -> listOf(
            "Бровари" to "Бровары",
            "Київська область" to "Киевская область",
            "Україна" to "Украина",
            "Київ" to "Киев",
        )
        else -> emptyList()
    }
    return replacements.fold(location) { current, (source, target) -> current.replace(source, target) }
}
private fun localizedWeatherCondition(condition: String, copy: Copy): String = when (condition) {
    "Ясно" -> copy.text("Ясно", "Clear", "Ясно")
    "Мінлива хмарність" -> copy.text("Мінлива хмарність", "Partly cloudy", "Переменная облачность")
    "Хмарно" -> copy.text("Хмарно", "Cloudy", "Облачно")
    "Туман" -> copy.text("Туман", "Fog", "Туман")
    "Мряка" -> copy.text("Мряка", "Drizzle", "Морось")
    "Дощ" -> copy.text("Дощ", "Rain", "Дождь")
    "Сніг" -> copy.text("Сніг", "Snow", "Снег")
    "Гроза" -> copy.text("Гроза", "Thunderstorm", "Гроза")
    else -> condition
}

private fun localizedDayLabel(day: String, copy: Copy): String = when (day) {
    "Сь" -> copy.text("Сь", "Td", "Сг")
    "Зв" -> copy.text("Зв", "Tm", "Зв")
    "Пн" -> copy.text("Пн", "Mon", "Пн")
    "Вт" -> copy.text("Вт", "Tue", "Вт")
    "Ср" -> copy.text("Ср", "Wed", "Ср")
    "Чт" -> copy.text("Чт", "Thu", "Чт")
    "Пт" -> copy.text("Пт", "Fri", "Пт")
    "Сб" -> copy.text("Сб", "Sat", "Сб")
    "Нд" -> copy.text("Нд", "Sun", "Вс")
    else -> day
}
private fun currentTime(): String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
private fun currentDate(): String = SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date())

private fun translations(mode: LanguageMode): Copy {
    val resolved = if (mode == LanguageMode.System) LanguageMode.Ukrainian else mode
    return when (resolved) {
        LanguageMode.Russian -> Copy("Приложения", "Назад", "Настройки", "Поиск приложений", "Добавлено на главный экран", "Показывать часы", "Показывать погоду", "Прозрачность", "Язык", "Локация погоды", "Сохранить", "Отмена", "Загрузка погоды", { "Установлено: $it" }, { languageNameRu(it) }, LanguageMode.Russian)
        LanguageMode.English -> Copy("Apps", "Back", "Settings", "Search apps", "Added to home screen", "Show clock", "Show weather", "Transparency", "Language", "Weather location", "Save", "Cancel", "Loading weather", { "Installed: $it" }, { languageNameEn(it) }, LanguageMode.English)
        LanguageMode.Ukrainian, LanguageMode.System -> Copy("Додатки", "Назад", "Налаштування", "Пошук додатків", "Додано на головний екран", "Показувати годинник", "Показувати погоду", "Прозорість", "Мова", "Локація погоди", "Зберегти", "Скасувати", "Завантаження погоди", { "Встановлено: $it" }, { languageNameUk(it) }, LanguageMode.Ukrainian)
    }
}

private fun languageNameUk(mode: LanguageMode): String = when (mode) {
    LanguageMode.System -> "Системна"
    LanguageMode.Ukrainian -> "Українська"
    LanguageMode.Russian -> "Російська"
    LanguageMode.English -> "Англійська"
}

private fun languageNameRu(mode: LanguageMode): String = when (mode) {
    LanguageMode.System -> "Системная"
    LanguageMode.Ukrainian -> "Украинская"
    LanguageMode.Russian -> "Русская"
    LanguageMode.English -> "Английская"
}

private fun languageNameEn(mode: LanguageMode): String = when (mode) {
    LanguageMode.System -> "System"
    LanguageMode.Ukrainian -> "Ukrainian"
    LanguageMode.Russian -> "Russian"
    LanguageMode.English -> "English"
}

private fun isDefaultLauncher(context: Context): Boolean {
    val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
    val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    return resolveInfo?.activityInfo?.packageName == context.packageName
}

private fun isNotificationAccessGranted(context: Context): Boolean {
    return NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
}

@Composable
fun HiddenAppsDialog(
    copy: Copy,
    apps: List<LauncherAppInfo>,
    iconPackPackage: String?,
    onDismiss: () -> Unit,
    onUnhideApp: (LauncherAppInfo) -> Unit,
) {
    CompositionLocalProvider(LocalTileAlpha provides OPEN_FOLDER_ALPHA) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .noPressClickable(onClick = onDismiss),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .fillMaxHeight(0.85f)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .noPressClickable(enabled = false) {},
                shape = RoundedCornerShape(32.dp),
                color = LocalTileColor.current.copy(alpha = OPEN_FOLDER_ALPHA),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 13.dp)
                ) {
                    Text(
                        copy.text("Приховані додатки", "Hidden apps", "Скрытые приложения"),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        copy.text("Довге натискання поверне додаток у список", "Long-press to return an app to the list", "Долгое нажатие вернет приложение в список"),
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        lineHeight = 15.sp
                    )
                    Spacer(Modifier.height(18.dp))
                    
                    if (apps.isEmpty()) {
                        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(copy.text("Немає прихованих додатків", "No hidden apps", "Нет скрытых приложений"), color = Color.White.copy(alpha = 0.4f), fontSize = 14.sp)
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 60.dp),
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(9.dp),
                            verticalArrangement = Arrangement.spacedBy(15.dp),
                            contentPadding = PaddingValues(bottom = 6.dp)
                        ) {
                            items(apps, key = { it.componentKey() }, contentType = { "hidden_app" }) { app ->
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .noPressCombinedClickable(
                                            onClick = { },
                                            onLongClick = { onUnhideApp(app) }
                                        ),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        rememberAppIconPainter(app, iconPackPackage),
                                        null,
                                        tint = Color.Unspecified,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(Modifier.height(6.dp))
                                    Text(
                                        localizedAppLabel(app, copy),
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.12f)),
                        border = tileBorderStrokeOrNull(),
                    ) {
                        Text(copy.back, fontSize = 15.sp, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}
