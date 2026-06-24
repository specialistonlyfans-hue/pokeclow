# TECHNICAL DEBT REPORT — PokeClaw

## Summary

Technical debt level: **High**

Estimated effort to reach production-grade maintainability: **200–400 engineering hours**, depending on how much existing behavior must remain fully backward-compatible.

## Debt Categories

| Category | Severity | Estimated Effort |
|---|---|---:|
| Agent runtime concentration | Critical | 40–80h |
| Global state / singleton coupling | Critical | 30–60h |
| Permission/security hardening | Critical | 40–80h |
| Testing gaps | High | 80–120h |
| CI/CD gaps | High | 8–20h |
| UI state management | Medium | 20–40h |
| Config / magic constants | Medium | 8–16h |

## Critical Debt

### TD-001 — Oversized Agent Runtime

`DefaultAgentService` should not own prompt generation, loop execution, tool coordination, compression, retry policy, stuck detection, and reporting.

**Fix:** Extract focused classes without changing behavior.

### TD-002 — AppViewModel as Runtime Container

`AppViewModel` appears to act as a runtime object graph. This prevents clean lifecycle ownership and testing.

**Fix:** Add `AppComponent` or manual DI container, then shrink AppViewModel.

### TD-003 — Permission Logic Scattered

Accessibility, overlay, notification, storage, and background execution checks must be centralized.

**Fix:** Introduce `PermissionStateRepository` and `CapabilityManager`.

### TD-004 — Missing Session Ownership Model

Local LLM sessions, task sessions, and chat sessions need a clear single-owner model.

**Fix:** Introduce `SessionManager`.

## High Debt

### TD-005 — Weak Test Coverage

Critical runtime paths need test coverage before major refactor.

### TD-006 — No CI Gate

Every PR should run build, unit tests, and lint.

### TD-007 — API Key Storage Risk

Cloud provider keys should not live in plain unencrypted storage.

## Medium Debt

### TD-008 — Magic Numbers

Agent iteration count, timeouts, retry windows, token policies, and settle delays should be centralized.

### TD-009 — Mixed Error Handling

Use a consistent sealed result or error model.

### TD-010 — UI/Business Coupling

Chat and task state should be exposed as immutable StateFlow models.

## Debt Repayment Order

1. Documentation and CI
2. AppConfig
3. PermissionStateRepository
4. CapabilityManager
5. RuntimeStateManager
6. SessionManager
7. Extract PromptBuilder
8. Extract ToolExecutionCoordinator
9. Extract AgentLoopRunner
10. Add tests around extracted classes

## Non-Goals

Do not add new automation features until the foundation is stable.
Do not rewrite the whole app at once.
Do not break existing channel or agent behavior without a migration plan.