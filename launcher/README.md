## PhosphorOS Launcher (WIP)

This folder is the **future standalone Android launcher** (single APK) described in the root `README.md`.

### Current status

- Folder scaffolding is in place (`app/`, `core/`, `ui/`, etc.)
- ✅ Now includes a **buildable Android project** in `launcher/app`
  - Minimal terminal UI + command prompt
  - Commands: `help`, `clear`, `status`, `apps`, `open <name>`, `fav`, `set`, `volume`, `brightness`, `settings`
  - History navigation: DPAD ↑/↓

### Intended architecture

- `app/`: Android entry / launcher integration
- `core/`: command engine + routing
- `ui/`: terminal renderer
- `widgets/`: system modules
- `input/`: keyboard handling
- `assets/`: bundled fonts, icon set, wallpapers, etc.

### Next steps (when implementing)

- Better autocomplete + inline hints
- Notification listener module (requires user permission)
- Optional plugin system

### Commands (current)

Examples:

```bash
help
status
apps
apps mail
open chrome
fav add gmail
fav
set font 18
set phosphor #00FF9C
volume 50
clear
```

### Build on another computer

This repo includes Gradle build scripts and wrapper configuration. To build without Android Studio:

1. Install **JDK 17**
2. Install the **Android SDK** (platform + build-tools)
3. From `launcher/`:

```bash
./gradlew assembleDebug
```

The Gradle wrapper jars are committed under `launcher/gradle/wrapper/`, so `./gradlew` should work in CI and on other machines.
