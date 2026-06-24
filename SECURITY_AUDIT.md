# SECURITY AUDIT — PokeClaw

## Summary

PokeClaw controls Android UI automation through Accessibility, notifications, overlays, network APIs, and potentially local/cloud AI models. That makes this app high-power and high-risk by design.

The security model must be explicit, auditable, and restrictive.

## Severity Overview

| Severity | Count | Theme |
|---|---:|---|
| Critical | 2 | Accessibility control and token/key handling |
| High | 4 | Overlay, storage, network, exported entry points |
| Medium | 5 | WebView, logging, background startup, package visibility |
| Low | 3 | Documentation/config hardening |

## Critical Issues

### CRIT-001 — AccessibilityService Can Control Other Apps

**Risk:** Accessibility can read screen content, click, type, scroll, and potentially expose sensitive information.

**Required controls:**

- Per-tool capability checks
- Sensitive field detection
- Password/payment/banking guardrails
- Explicit user consent for dangerous actions
- Audit logging for every tool call
- No autonomous execution on unknown sensitive screens

### CRIT-002 — API Key / Token Storage

**Risk:** OpenAI, Anthropic, Telegram, Discord, WeChat or other provider credentials may be stored in normal key-value storage.

**Required controls:**

- Android Keystore-backed encrypted storage
- Token redaction in logs
- No key export through backup/logcat
- Migration path from old storage

## High Issues

### HIGH-001 — Overlay Permission

Overlay can be abused for clickjacking or misleading UI.

**Fix:** Only show overlays while task state is visible and user-understandable. Never overlay credential prompts.

### HIGH-002 — Notification Access

Notification content may include OTPs, private messages, auth codes, banking alerts.

**Fix:** Add notification filtering, redaction, and allowlist/denylist policy.

### HIGH-003 — External Automation Entry Points

External intents must be validated.

**Fix:** Reject malformed input, require explicit allowed caller contract, and log provenance.

### HIGH-004 — Network/API Usage

LLM calls can leak screen content and private messages.

**Fix:** Add cloud-send warning, local/cloud mode separation, redaction layer, and rate limiting.

## Medium Issues

1. WebView should be hardened.
2. Logs must redact personal and token data.
3. Storage access should be minimized.
4. Package visibility should be limited.
5. Background startup should remain user-visible and explainable.

## Required Security Components

- `PermissionStateRepository`
- `CapabilityManager`
- `ToolPermissionGuard`
- `SensitiveScreenGuard`
- `SecretStore`
- `NetworkRedactor`
- `AuditLogWriter`

## Production Rule

No tool should execute merely because the LLM requested it. Every tool execution must pass deterministic local policy first.