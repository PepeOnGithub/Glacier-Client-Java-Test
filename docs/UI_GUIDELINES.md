# Glacier Client — UI & Module Guidelines

A single source of truth for keeping the Glacier UI clean and consistent. Follow these when adding a
module, a setting, or any screen element.

## 1. Visual language

- **Font:** Inter Medium everywhere. Wrap user-facing strings with the screen's `inter(String)` helper
  (`Text.literal(s).setStyle(Style.EMPTY.withFont(glacierclient:inter_medium))`). Never draw raw strings
  for headings, labels, module names, or settings.
- **Corners (radius):**
  - Pills / primary buttons (Resume, Save & Quit, title buttons): `height / 2` (full capsule).
  - Circular icon buttons (header info/mail/screenshot, bottom-left, slide tab): `size / 2`.
  - Cards / tiles / panels: `8–12 px`.
  - Inline controls (mode button, string box, toggle pill): `7–8 px`.
- **Colors** (from `GlacierTheme`):
  - Panel/base fill: `0xE0–0xCC` alpha over `0x0B1220`. Hover: `0xEE1B2433`.
  - Accent: `GlacierTheme.ACCENT` (active state, values, highlights). Dim: `TEXT_DIM` (secondary).
  - Background nine-slice textures (`*_bg`) render at 0.65 alpha — don't stack more than one.
- **Outlines:** 1px `0x1AFFFFFF` for subtle edges; accent outline only for focus/active.
- **Spacing:** 8px panel padding; 10px gap between cards; 4px between setting rows.

## 2. Buttons

- Round shapes for icon-only actions; pills for text actions.
- Every button must have a **hover state** (lighten fill) and perform a **real action** — no dead buttons.
  If an action has no destination yet, route it somewhere sensible (e.g. the mod menu) rather than no-op.
- Verify the action's API against the **named** Minecraft jar (see §6) before wiring.

## 3. Module cards (mod menu grid)

- Icon (30px) centered, name (Inter, centered, trimmed) below, gear in the top-right if the module has
  settings. Accent bar/outline only when the module is enabled.
- Card background: rounded `modules_underlined_base_bg` (or themed `CardStyle`). One background only.
- Keep names short; trim with `trim(name, w - 12)`.

## 4. Settings (the part most often "half-baked")

Every setting type has a defined presentation — match it exactly:

| Setting        | Control                                                              |
|----------------|---------------------------------------------------------------------|
| Boolean        | Toggle pill (right-aligned), knob slides on/off                      |
| Number         | Slider with filled track + knob; **value shown right-aligned** in accent |
| Mode           | Rounded button showing current value → opens dropdown                |
| String         | Rounded text box; focus = accent outline + blinking caret           |
| Color          | Swatch (alpha checker behind) → opens color picker                  |

Rules:
- Label on the left in Inter `TEXT`; control on the right. Row height 26px.
- A setting must **do something** when changed. If a module exposes a getter but nothing reads it,
  it is *not done* — wire it (event listener or mixin) before calling the module complete.
- Don't add setting types a module can't honor (e.g. a Color on a module with no color output).

## 5. "Done" checklist for a module

1. Registered in `ModuleManager.registerModules()` (per-package `register(...)` line).
2. Appears with an icon (add an `IconTextures` alias if needed).
3. Every setting visibly changes behavior (verified in-game or by clear code path).
4. Enable/disable is reversible (restore any state changed in `onEnable`).
5. No leftover getters with no consumer.

## 6. Implementation notes (avoid guesswork)

- Confirm real yarn names/descriptors from the **named** merged jar, not the intermediary one:
  `~/.gradle/caches/fabric-loom/minecraftMaven/.../minecraft-merged-1.20.4-…yarn…-v2.jar`
  → `unzip` the `.class` and `javap -p -s` it.
- Mixins that target optional/uncertain members: use `require = 0` so a mapping miss fails gracefully.
- Build the jar with `./gradlew :build --offline -q`; never compile per-change — batch, then build.
