package com.example.pocketlauncher

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    private val packageManager = application.packageManager

    private val _apps = MutableStateFlow<List<LauncherAppInfo>>(emptyList())
    val apps: StateFlow<List<LauncherAppInfo>> = _apps.asStateFlow()

    private val _recentAppKeys = MutableStateFlow<List<String>>(emptyList())
    val recentApps: StateFlow<List<LauncherAppInfo>> = combine(_apps, _recentAppKeys) { allApps, keys ->
        keys.mapNotNull { key -> allApps.find { it.componentName.flattenToString() == key } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _homeRequests = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val homeRequests: SharedFlow<Unit> = _homeRequests.asSharedFlow()

    private val prefs = application.getSharedPreferences("launcher_prefs", Context.MODE_PRIVATE)

    val notificationPackages: StateFlow<Set<String>> = NotificationService.activeNotificationPackages

    init {
        refreshApps()
        loadRecentApps()
    }

    private fun loadRecentApps() {
        val keys = prefs.getString("recent_apps", "")?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
        _recentAppKeys.value = keys
    }

    fun addToRecent(app: LauncherAppInfo) {
        val key = app.componentName.flattenToString()
        val current = _recentAppKeys.value.toMutableList()
        current.remove(key)
        current.add(0, key)
        val next = current.take(8)
        _recentAppKeys.value = next
        prefs.edit().putString("recent_apps", next.joinToString("\n")).apply()
    }

    fun setQuery(value: String) {
        if (_query.value == value) {
            return
        }
        _query.value = value
    }

    fun requestHome() {
        _homeRequests.tryEmit(Unit)
    }

    fun refreshApps() {
        viewModelScope.launch(Dispatchers.IO) {
            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }

            val resolvedApps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                packageManager.queryIntentActivities(
                    launcherIntent,
                    PackageManager.ResolveInfoFlags.of(0),
                )
            } else {
                @Suppress("DEPRECATION")
                packageManager.queryIntentActivities(launcherIntent, 0)
            }

            val apps = resolvedApps
                .mapNotNull { info ->
                    val activityInfo = info.activityInfo ?: return@mapNotNull null
                    val packageName = activityInfo.packageName
                    val fallbackLabel = info.loadLabel(packageManager).toString()
                    val applicationInfo = activityInfo.applicationInfo
                    val category = applicationInfo.category
                    LauncherAppInfo(
                        label = ukrainianAppLabel(packageName, fallbackLabel),
                        packageName = packageName,
                        activityName = activityInfo.name,
                        icon = info.loadIcon(packageManager),
                        category = category,
                        isSystemApp = applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0 ||
                            applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0,
                    )
                }
                .distinctBy { it.componentName.flattenToString() }
                .sortedBy { it.label.lowercase() }

            _apps.value = apps
        }
    }
}

private fun ukrainianAppLabel(
    packageName: String,
    fallbackLabel: String,
): String {
    return UKRAINIAN_APP_LABELS[packageName] ?: fallbackLabel
}

private val UKRAINIAN_APP_LABELS = mapOf(
    "com.android.camera" to "Камера",
    "com.google.android.GoogleCamera" to "Камера",
    "com.android.contacts" to "Контакти",
    "com.google.android.contacts" to "Контакти",
    "com.android.dialer" to "Телефон",
    "com.google.android.dialer" to "Телефон",
    "com.android.mms" to "Повідомлення",
    "com.google.android.apps.messaging" to "Повідомлення",
    "com.android.calendar" to "Календар",
    "com.google.android.calendar" to "Календар",
    "com.android.calculator2" to "Калькулятор",
    "com.google.android.calculator" to "Калькулятор",
    "com.android.clock" to "Годинник",
    "com.google.android.deskclock" to "Годинник",
    "com.android.settings" to "Налаштування",
    "com.android.chrome" to "Chrome",
    "com.google.android.apps.photos" to "Фото",
    "com.google.android.gm" to "Пошта",
    "com.google.android.apps.maps" to "Карти",
    "com.google.android.youtube" to "YouTube",
    "com.google.android.apps.youtube.music" to "YouTube Music",
    "com.google.android.apps.docs" to "Документи",
    "com.google.android.apps.sheets" to "Таблиці",
    "com.google.android.apps.slides" to "Презентації",
    "com.google.android.keep" to "Нотатки",
    "com.google.android.apps.translate" to "Перекладач",
    "com.google.android.apps.walletnfcrel" to "Гаманець",
    "com.android.vending" to "Play Маркет",
    "com.google.android.apps.nbu.files" to "Файли",
    "com.google.android.apps.authenticator2" to "Автентифікатор",
)
