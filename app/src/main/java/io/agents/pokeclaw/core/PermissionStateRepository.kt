package io.agents.pokeclaw.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import androidx.core.content.ContextCompat
import io.agents.pokeclaw.service.ClawAccessibilityService
import io.agents.pokeclaw.service.ClawNotificationListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Single source of truth for permission and service-binding state.
 *
 * Existing UI/runtime code can migrate to this incrementally instead of
 * checking Android settings directly in many different places.
 */
class PermissionStateRepository(private val context: Context) {

    data class PermissionState(
        val accessibilityEnabledInSettings: Boolean = false,
        val accessibilityRunning: Boolean = false,
        val notificationAccessEnabledInSettings: Boolean = false,
        val notificationListenerConnected: Boolean = false,
        val postNotificationsGranted: Boolean = false,
        val overlayGranted: Boolean = false,
        val batteryOptimizationIgnored: Boolean = false,
        val storageAccessGranted: Boolean = false,
    ) {
        val canRunInteractiveTask: Boolean
            get() = accessibilityRunning

        val canReadNotifications: Boolean
            get() = notificationListenerConnected

        val canShowOverlay: Boolean
            get() = overlayGranted

        val canRunMonitor: Boolean
            get() = accessibilityRunning && notificationListenerConnected
    }

    private val mutableState = MutableStateFlow(PermissionState())
    val state: StateFlow<PermissionState> = mutableState.asStateFlow()

    fun snapshot(): PermissionState = mutableState.value

    fun refresh() {
        mutableState.value = PermissionState(
            accessibilityEnabledInSettings = ClawAccessibilityService.isEnabledInSettings(context),
            accessibilityRunning = ClawAccessibilityService.isRunning(),
            notificationAccessEnabledInSettings = ClawNotificationListener.isEnabledInSettings(context),
            notificationListenerConnected = ClawNotificationListener.isConnected(),
            postNotificationsGranted = isPostNotificationsGranted(),
            overlayGranted = Settings.canDrawOverlays(context),
            batteryOptimizationIgnored = isIgnoringBatteryOptimizations(),
            storageAccessGranted = hasStorageAccess(),
        )
    }

    private fun isPostNotificationsGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(context.packageName)
    }

    private fun hasStorageAccess(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
