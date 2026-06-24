# QA STRATEGY — PokeClaw

## Goal

Create a QA system that makes refactoring safe and prevents regressions in the Android AI Agent runtime.

## Testing Pyramid

### 1. Unit Tests — 70%

Target pure Kotlin/runtime components first.

Required tests:

- PromptBuilderTest
- ContextCompressorTest
- ToolExecutionCoordinatorTest
- AgentLoopRunnerTest
- RuntimeStateManagerTest
- SessionManagerTest
- PermissionStateRepositoryTest
- CapabilityManagerTest
- LlmClientFactoryTest
- TokenUsageTrackerTest

### 2. Integration Tests — 20%

Required tests:

- Agent loop with fake LLM
- Tool registry with fake tools
- Channel input to task router
- Task cancellation
- Session handoff chat -> task -> chat
- Notification intake to task creation

### 3. Android/UI Tests — 10%

Required tests:

- ComposeChatActivity smoke test
- SettingsActivity persistence test
- Permission screen flow
- ExternalAutomationActivity intent contract
- Accessibility service smoke test on emulator where possible

## CI Pipeline

Every PR should run:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
./gradlew lintDebug
```

Later phases should add:

```bash
./gradlew connectedDebugAndroidTest
./gradlew dependencyCheckAnalyze
./gradlew assembleRelease
```

## Device Compatibility Matrix

| Device Type | Android Version | Priority |
|---|---|---|
| Pixel emulator | Android 14/15 | P0 |
| Samsung One UI | Android 13/14/15 | P0 |
| Xiaomi/MIUI/HyperOS | Android 13/14/15 | P1 |
| OnePlus/OxygenOS | Android 13/14/15 | P1 |
| Tablet | Android 13+ | P2 |

## Regression Checklist

- App launches cleanly
- Accessibility service detection works
- Overlay permission state works
- Notification listener state works
- Chat opens and sends a message
- Cloud LLM config can be saved
- Local LLM config does not crash app
- Agent can start a task
- Agent can cancel a task
- Tool execution returns structured success/error
- Foreground service starts/stops correctly
- No secret appears in logs

## Coverage Targets

| Area | Target |
|---|---:|
| Agent core | 80% |
| Tool system | 85% |
| LLM factory/client formatting | 75% |
| State managers | 90% |
| Channel parsers | 70% |
| UI ViewModels | 80% |
| Android Activities | 40–60% |

## QA Rule

No major runtime refactor should be merged without tests around the extracted component.