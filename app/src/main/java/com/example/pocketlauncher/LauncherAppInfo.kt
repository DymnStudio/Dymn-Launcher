package com.example.pocketlauncher

import android.content.ComponentName
import android.graphics.drawable.Drawable

data class LauncherAppInfo(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable,
    val category: Int = -1,
    val isSystemApp: Boolean = false,
) {
    val componentName: ComponentName
        get() = ComponentName(packageName, activityName)
}
