# ROADMAP — PokeClaw Production Transformation

## Objective

Turn PokeClaw into a stable Android AI Agent platform suitable for long-term development and deployment.

This roadmap intentionally prioritizes architecture, security, testing, and maintainability before new features.

## Phase 0 — Foundation

### Goal

Create the foundation that allows safe incremental refactoring.

### Work

- Add CI pipeline
- Add `AppConfig`
- Add `PermissionStateRepository`
- Add `CapabilityManager`
- Add `RuntimeStateManager`
- Add `SessionManager`
- Document architecture/security/testing gaps

### Required Files

- `.github/workflows/ci.yml`
- `app/src/main/java/io/agents/pokeclaw/core/AppConfig.kt`
- `app/src/main/java/io/agents/pokeclaw/core/PermissionStateRepository.kt`
- `app/src/main/java/io/agents/pokeclaw/core/CapabilityManager.kt`
- `app/src/main/java/io/agents/pokeclaw/core/RuntimeStateManager.kt`
- `app/src/main/java/io/agents/pokeclaw/core/SessionManager.kt`

### Expected Outcome

The repo has a clear base layer for configuration, permission state, capability checks, runtime state, and LLM session ownership.

## Phase 1 — Agent Runtime Extraction

### Goal

Break up `DefaultAgentService` without changing behavior.

### New Components

- `AgentLoopRunner`
- `PromptBuilder`
- `ToolExecutionCoordinator`
- `ContextCompressor`
- `TokenUsageTracker`
- `AgentRunPolicy`

### Risks

- Agent behavior regressions
- Tool execution order changes
- Context compression changes affecting LLM quality

### Expected Outcome

`DefaultAgentService` becomes a thin coordinator instead of a God class.

## Phase 2 — State and Permission Hardening

### Goal

Make all runtime capability decisions deterministic and testable.

### Work

- Wire `PermissionStateRepository` into UI and runtime
- Add `ToolPermissionGuard`
- Add `SensitiveScreenGuard`
- Add `AuditLogWriter`
- Replace scattered permission checks

### Expected Outcome

Every high-risk tool call passes local policy before execution.

## Phase 3 — UI and ViewModel Cleanup

### Goal

Move chat and runtime UI state to clean ViewModel/StateFlow boundaries.

### Work

- Add `ChatViewModel`
- Add `ChatState`
- Add `ChatEvent`
- Deprecate `ChatSessionController`
- Move hardcoded user-facing strings to resources

### Expected Outcome

UI becomes predictable, testable, and lifecycle-safe.

## Phase 4 — Production Hardening

### Goal

Prepare for real deployment.

### Work

- Encrypt API keys
- Add rate limiting
- Harden WebView
- Harden network security config
- Add release CI
- Add dependency/security scanning
- Add regression tests

### Expected Outcome

Production checklist is clear, security risk is reduced, and builds are guarded by CI.

## Branch Strategy

- `main`: stable base
- `feature/phase-0-foundation`: documentation + foundational classes
- `feature/phase-1-agent-runtime`: agent extraction
- `feature/phase-2-security-state`: permission/security state
- `feature/phase-3-ui-state`: UI state refactor
- `feature/phase-4-production-hardening`: release/security hardening

## Rule

No large rewrite. Small commits. Builds must keep passing.