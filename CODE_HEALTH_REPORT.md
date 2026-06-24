# CODE HEALTH REPORT — PokeClaw

## Overall Score: 5.2/10

PokeClaw has strong product ambition and a useful Android-agent foundation, but the codebase is currently closer to advanced prototype quality than production quality.

## Subsystem Scores

| Subsystem | Score | Reason |
|---|---:|---|
| Architecture | 5/10 | Strong concept, weak boundaries |
| Maintainability | 4/10 | God classes and hidden dependencies |
| Scalability | 5/10 | Runtime will become hard to extend without extraction |
| Security | 4/10 | Powerful Android permissions need stricter enforcement |
| Testability | 3/10 | Global state and Android service coupling block tests |
| Android Compatibility | 6/10 | Modern SDK target, but permission handling needs hardening |
| Local AI Support | 6/10 | Good direction, lifecycle/session concerns |
| Cloud AI Support | 6/10 | Provider abstraction exists, key handling needs work |
| UX | 6/10 | Useful UI patterns, needs state cleanup |
| Reliability | 4/10 | Ad-hoc concurrency and long agent loops are risk points |

## Critical Code Smells

### 1. DefaultAgentService is too large

It appears to handle prompt construction, LLM loop execution, tool execution, budget control, retry logic, token monitoring, stuck detection, and context compression.

This should be split into:

- AgentLoopRunner
- PromptBuilder
- ToolExecutionCoordinator
- ContextCompressor
- TokenUsageTracker
- AgentRunPolicy

### 2. AppViewModel has runtime responsibilities

A ViewModel should not be the main application runtime container. It should expose UI state and dispatch UI events. Runtime creation and system wiring belong in an application component or dependency container.

### 3. TaskOrchestrator mixes layers

TaskOrchestrator should coordinate tasks, not directly own channel output, UI overlay updates, foreground-service state, and agent execution policy.

### 4. Global state blocks testing

Patterns such as application-level singleton access and singleton registries make it difficult to run isolated unit tests.

## Testing Gaps

High-value test targets:

1. Agent loop behavior
2. Tool argument validation
3. Permission enforcement
4. Sensitive-screen handling
5. LLM response parsing
6. Channel message ingestion
7. Task cancellation
8. Session handoff between chat and task modes

## Build/CI Health

The repo needs:

- GitHub Actions CI
- Debug build check
- Unit tests
- Android lint
- Dependency/security scan
- Release build verification

## Recommendation

Do not rewrite everything. Add a thin foundation layer first, then extract pieces one at a time while keeping behavior stable.