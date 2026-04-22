## PhosphorOS Launcher (WIP)

This folder is the **future standalone Android launcher** (single APK) described in the root `README.md`.

### Current status

- Folder scaffolding is in place (`app/`, `core/`, `ui/`, etc.)
- ✅ Now includes a **buildable Android project** in `launcher/app`

### Intended architecture

- `app/`: Android entry / launcher integration
- `core/`: command engine + routing
- `ui/`: terminal renderer
- `widgets/`: system modules
- `input/`: keyboard handling
- `assets/`: bundled fonts, icon set, wallpapers, etc.

### Next steps (when implementing)

- Implement command parsing (`open`, `status`, `clear`).
- Add real app launching via `PackageManager`.
- Add widgets (battery/network/time) and persistent settings.

### Build on another computer

This repo includes Gradle build scripts and wrapper configuration. To build without Android Studio:

1. Install **JDK 17**
2. Install the **Android SDK** (platform + build-tools)
3. From `launcher/`:

```bash
./gradlew assembleDebug
```

Note: The wrapper scripts are included, but the wrapper JAR is intentionally not committed here; if `./gradlew` complains, generate it once with:

```bash
gradle wrapper
```

Or open `launcher/` in Android Studio once (it will generate wrapper files automatically).
