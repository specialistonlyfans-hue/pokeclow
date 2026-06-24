# ARCHITECTURE REPORT — PokeClaw

## 1. Overview

PokeClaw is an Android AI Agent platform built around Android Accessibility, channel-based task intake, LLM-driven agent execution, local/cloud model support, floating overlay feedback, and notification monitoring.

The repository already contains a serious foundation for an Android automation agent, but it is not production-ready yet. The main weakness is not missing features. The main weakness is architectural concentration: too much runtime behavior is concentrated in a small number of large classes.

## 2. Major Components

### Activities

| Component | Purpose | Current Risk |
|---|---|---|
| SplashActivity | Entry point / permission gating | Low |
| ComposeChatActivity | Main chat UI | Medium: controller-style state, not a clean ViewModel boundary |
| SettingsActivity | Settings screen | Medium |
| LlmConfigActivity | LLM/provider configuration | Medium |
| ChannelConfigActivity | Channel setup | Medium |
| ThemeActivity | Theme configuration | Low |
| WebActivity | WebView/OAuth helper | Medium security review needed |
| GuideActivity | Onboarding/help | Low |
| ExternalAutomationActivity | External task entry | High: must validate input contracts strictly |
| ClipboardReaderActivity | Clipboard bridge/helper | Medium privacy risk |

### ViewModels and State

| Component | Role | Risk |
|---|---|---|
| AppViewModel | Application-level runtime coordinator | Critical: God ViewModel |
| SettingsViewModel | Settings state | Low/Medium |
| ChatSessionController | Chat/session logic | Medium: should become ChatViewModel |
| TaskSessionStore | Task lock/session state | Medium |
| AppCapabilityCoordinator | Capability state | Medium: should be unified with permission state |

### Services

| Component | Type | Role | Risk |
|---|---|---|---|
| ClawAccessibilityService | AccessibilityService | Screen reading, clicks, input, UI automation | Critical |
| ClawNotificationListener | NotificationListenerService | Notification intake | High |
| ForegroundService | Foreground service | Keep-alive/task state | Medium |
| KeepAliveJobService | JobService | Scheduled heartbeat | Medium |

### Agent Runtime

| Component | Role | Risk |
|---|---|---|
| AgentService | Runtime interface | Good abstraction |
| DefaultAgentService | Main implementation | Critical: oversized, too many responsibilities |
| AgentServiceFactory | Runtime construction | Medium |
| ToolRegistry | Tool discovery/execution registry | Medium: singleton-like coupling |
| BaseTool and tools | Tool execution layer | Medium: needs test coverage and permission gates |

### LLM Integrations

| Component | Role | Risk |
|---|---|---|
| LlmClient | Common LLM interface | Good |
| OpenAiLlmClient | OpenAI cloud integration | Medium: key handling/security |
| AnthropicLlmClient | Anthropic cloud integration | Medium: key handling/security |
| LocalLlmClient | On-device inference | Medium/High: lifecycle/session contention |
| LocalModelManager | Local model management | Medium |
| LlmClientFactory | Provider selection | Medium |

## 3. Tool System

The tool system appears to contain screen inspection, tapping, text input, scrolling, app opening, clipboard, notification reading, installed apps, calling, message sending, and knowledge-base tools.

This is powerful but dangerous. Every tool must have:

- Explicit capability requirement
- Runtime permission gate
- Sensitive-screen guard
- Structured input validation
- Auditable result format
- Unit tests

## 4. Dependency Graph

```text
ClawApplication
  -> AppViewModel
      -> TaskOrchestrator
          -> AgentService / DefaultAgentService
              -> LlmClient
              -> ToolRegistry
              -> Agent guards / budget / stuck detection
          -> PipelineRouter
          -> TaskSessionStore
      -> ChannelManager / ChannelSetup
      -> FloatingCircleManager
      -> ForegroundService / KeepAliveJobService
      -> ConfigServerManager
  -> AppCapabilityCoordinator
  -> PlaybookManager
```

## 5. Main Architecture Problems

### Critical

1. `DefaultAgentService` is a God class.
2. `AppViewModel` is acting as an application runtime container.
3. `TaskOrchestrator` mixes orchestration, UI feedback, channel output, and service control.
4. Global/singleton state makes testing and lifecycle correctness difficult.
5. Accessibility-service access is too central and too powerful.
6. No clear dependency-injection boundary.

### High

1. Local LLM and chat/task sessions need one central lifecycle manager.
2. Permission checks are scattered.
3. Runtime state is not centralized as a reactive model.
4. CI/testing is not sufficient for a production Android agent.

### Medium

1. Hardcoded configuration values.
2. Mixed error-handling style.
3. Channel implementations are uneven.
4. Security documentation and enforcement need to be stronger.

## 6. Target Architecture

```text
AppComponent
  -> AppConfig
  -> PermissionStateRepository
  -> CapabilityManager
  -> RuntimeStateManager
  -> SessionManager
  -> AgentRuntime
       -> AgentLoopRunner
       -> PromptBuilder
       -> ToolExecutionCoordinator
       -> ContextCompressor
       -> TokenUsageTracker
  -> ToolSystem
       -> ToolRegistry
       -> ToolPermissionGuard
  -> Channels
       -> Telegram / Discord / WeChat / Local
  -> UI
       -> ChatViewModel
       -> SettingsViewModel
```

## 7. Immediate Recommendation

Do not add new agent features yet. First add documentation, CI, state repositories, permission repository, capability manager, runtime state manager, and session manager. Then refactor the agent runtime incrementally.