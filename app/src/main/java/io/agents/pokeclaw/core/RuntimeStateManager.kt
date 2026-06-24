package io.agents.pokeclaw.core

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Centralized runtime state holder.
 *
 * This is a foundation class for replacing scattered global runtime state with
 * a small reactive model. It is intentionally not wired into existing runtime
 * paths yet, so behavior stays unchanged during Phase 0.
 */
class RuntimeStateManager {

    enum class AgentState {
        IDLE,
        INITIALIZING,
        READY,
        RUNNING,
        STOPPING,
        ERROR,
    }

    enum class ServiceState {
        DISABLED,
        CONNECTING,
        READY,
        DEGRADED,
    }

    data class RuntimeState(
        val agentState: AgentState = AgentState.IDLE,
        val accessibilityState: ServiceState = ServiceState.DISABLED,
        val notificationListenerState: ServiceState = ServiceState.DISABLED,
        val foregroundServiceRunning: Boolean = false,
        val networkConnected: Boolean = true,
        val activeTaskId: String = "",
        val activeTaskChannel: String = "",
    ) {
        val canStartTask: Boolean
            get() = agentState == AgentState.READY &&
                accessibilityState == ServiceState.READY &&
                activeTaskId.isEmpty()

        val isTaskRunning: Boolean
            get() = agentState == AgentState.RUNNING || agentState == AgentState.STOPPING
    }

    private val mutableState = MutableStateFlow(RuntimeState())
    val state: StateFlow<RuntimeState> = mutableState.asStateFlow()

    fun snapshot(): RuntimeState = mutableState.value

    fun setAgentState(agentState: AgentState) {
        mutableState.value = mutableState.value.copy(agentState = agentState)
    }

    fun setAccessibilityState(serviceState: ServiceState) {
        mutableState.value = mutableState.value.copy(accessibilityState = serviceState)
    }

    fun setNotificationListenerState(serviceState: ServiceState) {
        mutableState.value = mutableState.value.copy(notificationListenerState = serviceState)
    }

    fun setForegroundServiceRunning(running: Boolean) {
        mutableState.value = mutableState.value.copy(foregroundServiceRunning = running)
    }

    fun setNetworkConnected(connected: Boolean) {
        mutableState.value = mutableState.value.copy(networkConnected = connected)
    }

    fun setActiveTask(taskId: String, channel: String) {
        mutableState.value = mutableState.value.copy(
            activeTaskId = taskId,
            activeTaskChannel = channel,
        )
    }

    fun clearActiveTask() {
        mutableState.value = mutableState.value.copy(
            activeTaskId = "",
            activeTaskChannel = "",
        )
    }
}
