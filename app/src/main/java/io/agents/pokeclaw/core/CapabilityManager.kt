package io.agents.pokeclaw.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * High-level capability model for runtime decisions.
 *
 * Capabilities are derived from concrete permission/service state. Runtime
 * components should eventually ask this manager whether something can run,
 * instead of manually checking permissions and service singletons.
 */
class CapabilityManager(
    private val permissionStateRepository: PermissionStateRepository,
) {

    enum class Capability {
        INTERACTIVE_TASK,
        CHAT,
        MONITOR,
        NOTIFICATION_READ,
        OVERLAY,
        STORAGE,
    }

    data class CapabilitySnapshot(
        val interactiveTask: Boolean = false,
        val chat: Boolean = true,
        val monitor: Boolean = false,
        val notificationRead: Boolean = false,
        val overlay: Boolean = false,
        val storage: Boolean = false,
    ) {
        fun isCapable(capability: Capability): Boolean = when (capability) {
            Capability.INTERACTIVE_TASK -> interactiveTask
            Capability.CHAT -> chat
            Capability.MONITOR -> monitor
            Capability.NOTIFICATION_READ -> notificationRead
            Capability.OVERLAY -> overlay
            Capability.STORAGE -> storage
        }
    }

    private val mutableSnapshot = MutableStateFlow(CapabilitySnapshot())
    val snapshot: StateFlow<CapabilitySnapshot> = mutableSnapshot.asStateFlow()

    fun refresh() {
        permissionStateRepository.refresh()
        val permissionState = permissionStateRepository.snapshot()
        mutableSnapshot.value = CapabilitySnapshot(
            interactiveTask = permissionState.canRunInteractiveTask,
            chat = true,
            monitor = permissionState.canRunMonitor,
            notificationRead = permissionState.canReadNotifications,
            overlay = permissionState.canShowOverlay,
            storage = permissionState.storageAccessGranted,
        )
    }

    fun isCapable(capability: Capability): Boolean =
        mutableSnapshot.value.isCapable(capability)
}
