package io.agents.pokeclaw.core

import io.agents.pokeclaw.agent.llm.LlmClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Manages ownership of a single LLM session across chat and task modes.
 *
 * This is especially important for local on-device engines where simultaneous
 * sessions can be expensive or unsupported.
 */
class SessionManager {

    enum class SessionOwner {
        NONE,
        CHAT,
        TASK,
    }

    data class SessionState(
        val owner: SessionOwner = SessionOwner.NONE,
        val sessionId: String = "",
        val modelReady: Boolean = false,
    )

    private val mutableState = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = mutableState.asStateFlow()

    private var currentClientInternal: LlmClient? = null
    val currentClient: LlmClient? get() = currentClientInternal

    fun snapshot(): SessionState = mutableState.value

    fun acquire(owner: SessionOwner, client: LlmClient? = null): Boolean {
        val current = mutableState.value
        if (current.owner != SessionOwner.NONE && current.owner != owner) {
            return false
        }

        if (client != null) {
            currentClientInternal = client
        }

        mutableState.value = SessionState(
            owner = owner,
            sessionId = UUID.randomUUID().toString(),
            modelReady = currentClientInternal != null,
        )
        return true
    }

    fun release(owner: SessionOwner? = null) {
        val current = mutableState.value
        if (owner != null && current.owner != owner) {
            return
        }

        try {
            currentClientInternal?.close()
        } catch (_: Exception) {
            // Release must be best-effort and never crash lifecycle cleanup.
        }

        currentClientInternal = null
        mutableState.value = SessionState()
    }

    fun isAvailableFor(owner: SessionOwner): Boolean {
        val current = mutableState.value
        return current.owner == SessionOwner.NONE || current.owner == owner
    }
}
