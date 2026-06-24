package io.agents.pokeclaw.core

import io.agents.pokeclaw.utils.KVUtils

/**
 * Centralized application configuration.
 *
 * This class is intentionally small and backwards-compatible. It does not change
 * runtime behavior by itself. Existing code can migrate magic constants here
 * incrementally.
 */
data class AppConfig(
    val agentMaxIterations: Int = DEFAULT_AGENT_MAX_ITERATIONS,
    val agentTemperature: Double = DEFAULT_AGENT_TEMPERATURE,
    val screenSettleMs: Long = DEFAULT_SCREEN_SETTLE_MS,
    val maxApiRetries: Int = DEFAULT_MAX_API_RETRIES,
    val loopDetectWindowSize: Int = DEFAULT_LOOP_DETECT_WINDOW,
    val contextKeepRecentRounds: Int = DEFAULT_CONTEXT_KEEP_RECENT_ROUNDS,
    val taskWakeLockTimeoutMs: Long = DEFAULT_WAKE_LOCK_TIMEOUT_MS,
    val networkReconnectDelayMs: Long = DEFAULT_NETWORK_RECONNECT_DELAY_MS,
    val preWarmScreenEnabled: Boolean = true,
    val autoReturnToChatEnabled: Boolean = true,
) {
    companion object {
        const val DEFAULT_AGENT_MAX_ITERATIONS = 60
        const val DEFAULT_AGENT_TEMPERATURE = 0.1
        const val DEFAULT_SCREEN_SETTLE_MS = 500L
        const val DEFAULT_MAX_API_RETRIES = 3
        const val DEFAULT_LOOP_DETECT_WINDOW = 4
        const val DEFAULT_CONTEXT_KEEP_RECENT_ROUNDS = 3
        const val DEFAULT_WAKE_LOCK_TIMEOUT_MS = 600_000L
        const val DEFAULT_NETWORK_RECONNECT_DELAY_MS = 2_000L

        fun fromStorage(): AppConfig = AppConfig(
            agentMaxIterations = KVUtils.getInt("agent_max_iterations", DEFAULT_AGENT_MAX_ITERATIONS),
            agentTemperature = KVUtils.getFloat("agent_temperature", DEFAULT_AGENT_TEMPERATURE.toFloat()).toDouble(),
            screenSettleMs = KVUtils.getLong("screen_settle_ms", DEFAULT_SCREEN_SETTLE_MS),
            maxApiRetries = KVUtils.getInt("max_api_retries", DEFAULT_MAX_API_RETRIES),
            loopDetectWindowSize = KVUtils.getInt("loop_detect_window", DEFAULT_LOOP_DETECT_WINDOW),
            contextKeepRecentRounds = KVUtils.getInt("context_keep_recent", DEFAULT_CONTEXT_KEEP_RECENT_ROUNDS),
            taskWakeLockTimeoutMs = KVUtils.getLong("wake_lock_timeout_ms", DEFAULT_WAKE_LOCK_TIMEOUT_MS),
            networkReconnectDelayMs = KVUtils.getLong("network_reconnect_ms", DEFAULT_NETWORK_RECONNECT_DELAY_MS),
        )

        fun saveToStorage(config: AppConfig) {
            KVUtils.putInt("agent_max_iterations", config.agentMaxIterations)
            KVUtils.putFloat("agent_temperature", config.agentTemperature.toFloat())
            KVUtils.putLong("screen_settle_ms", config.screenSettleMs)
            KVUtils.putInt("max_api_retries", config.maxApiRetries)
            KVUtils.putInt("loop_detect_window", config.loopDetectWindowSize)
            KVUtils.putInt("context_keep_recent", config.contextKeepRecentRounds)
            KVUtils.putLong("wake_lock_timeout_ms", config.taskWakeLockTimeoutMs)
            KVUtils.putLong("network_reconnect_ms", config.networkReconnectDelayMs)
        }
    }
}
