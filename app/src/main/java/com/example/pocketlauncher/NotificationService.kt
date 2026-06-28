package com.example.pocketlauncher

import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationService : NotificationListenerService() {

    companion object {
        private val _activeNotificationPackages = MutableStateFlow<Set<String>>(emptySet())
        val activeNotificationPackages: StateFlow<Set<String>> = _activeNotificationPackages

        fun isEnabled(context: Context): Boolean {
            val componentName = ComponentName(context, NotificationService::class.java)
            val enabledListeners = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            return enabledListeners?.contains(componentName.flattenToString()) == true
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        updateActiveNotifications()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        updateActiveNotifications()
    }

    override fun onListenerConnected() {
        updateActiveNotifications()
    }

    override fun onListenerDisconnected() {
        _activeNotificationPackages.value = emptySet()
    }

    private fun updateActiveNotifications() {
        try {
            val active = activeNotifications
                ?.filter { !it.isOngoing && it.packageName != packageName }
                ?.map { it.packageName }
                ?.toSet() ?: emptySet()
            _activeNotificationPackages.value = active
        } catch (e: Exception) {
            _activeNotificationPackages.value = emptySet()
        }
    }
}
