package com.example.pocketlauncher

import android.accessibilityservice.AccessibilityService
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent

class LauncherAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        activeService = this
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit

    override fun onInterrupt() = Unit

    override fun onUnbind(intent: Intent?): Boolean {
        if (activeService === this) {
            activeService = null
        }
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        if (activeService === this) {
            activeService = null
        }
        super.onDestroy()
    }

    companion object {
        @Volatile
        private var activeService: LauncherAccessibilityService? = null

        fun openRecents(): Boolean {
            return activeService?.performGlobalAction(GLOBAL_ACTION_RECENTS) == true
        }

        fun openNotifications(): Boolean {
            return activeService?.performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS) == true
        }

        fun isEnabled(context: Context): Boolean {
            val componentName = ComponentName(context, LauncherAccessibilityService::class.java)
            val enabledServices = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
            )

            return (enabledServices
                ?.split(':')
                ?.any { service -> service.equals(componentName.flattenToString(), ignoreCase = true) }
                == true
            )
        }
    }
}
