# PhosphorOS

**Retro Terminal Interface for Android (Titan 2 Optimized)**

PhosphorOS transforms your Android device into a **minimal terminal-style interface** inspired by vintage CRT systems, cyberdecks, and command-line environments.

Designed especially for **keyboard devices like Titan 2**.

---

## ✨ What is this?

PhosphorOS is a **hybrid project**:

### 1. Theme Kit (usable now)

A launcher-based setup using:

* Nova Launcher
* KWGT widgets
* Custom icons & wallpapers

### 2. Custom Launcher (in progress)

A standalone Android launcher:

* terminal-based UI
* command-driven navigation
* one APK install

---

## ⚡ Two Ways to Use

### 🟢 Option A — Quick Setup (No Code)

Use Nova + KWGT

✔ works now
✔ no build required
❗ manual setup (5–10 min)

---

### 🟣 Option B — PhosphorOS Launcher (MVP)

Install single APK

✔ one-click install
✔ native terminal UI
✔ no KWGT/Nova needed

🚧 active development (`/launcher/`)

---

## 📦 Project Structure

```bash
PhosphorOS/
├── README.md
├── screenshots/
│
├── wallpapers/        # CRT / grid backgrounds
├── icons/             # monochrome terminal icons
├── kwgt/              # widget configs & formulas
├── nova/              # launcher settings
│
├── launcher/          # 🚧 Android launcher (APK project)
│   ├── app/
│   ├── ui/
│   ├── core/
│   ├── assets/
│   └── README.md
│
└── docs/
    ├── install.md
    └── design.md
```

---

## 🚀 Installation (Theme Kit)

### 1. Install apps

* Nova Launcher
* KWGT (Kustom Widget)

---

### 2. Apply wallpaper

From:

```bash
/wallpapers/
```

See also: `wallpapers/README.md`

Recommended:

* `phosphor_green.png`
* `titan_grid.png`

---

### 3. Nova settings

* Grid: `4x5`
* Labels: ON
* Dock: OFF
* Dark mode: ON

---

### 4. KWGT Widgets

Snippets live in:

```bash
/kwgt/presets/
```

#### Prompt

```text
makalin@titan2:~$
$if(df(ss)%2=0,█, )
```

#### System

```text
BAT: $bi(level)%
NET: $if(nc(wifi)=connected,WIFI,$if(nc(cell)=connected,LTE,OFFLINE))
TIME: $df(HH:mm)
DATE: $df(EEE dd MMM)
```

#### Launcher

```text
> web
> mail
> dir
> snd
> sys
```

Assign tap actions to apps.

---

### 5. Icons

Use `/icons/` or replicate style:

See also: `icons/README.md`

| App      | Terminal Label |
| -------- | -------------- |
| Chrome   | web            |
| Gmail    | mail           |
| Files    | dir            |
| Spotify  | snd            |
| Settings | sys            |

---

## 🎨 Design System

* Background: `#000000`
* Text: `#00FF9C`
* Accent: `#FFC857`
* Font:

  * VT323 → retro
  * JetBrains Mono → clean

---

## 🧠 Why not a single theme?

Android does not support deep theming like this without:

* root
* custom ROM
* or custom launcher

So:

> Theme Kit = flexible workaround
> Launcher = real solution

---

# 🧪 PhosphorOS Launcher (Core Idea)

Location:

```bash
/launcher/
```

This will become the **main product**.

---

## 🔥 Features (Current MVP)

- Terminal-style home screen + real command prompt
- Keyboard-first UX (Titan 2 friendly)
  - DPAD ↑/↓ command history
- App discovery + launching
  - `apps [filter]`
  - `open <name>` (fuzzy matching)
- Favorites (persisted)
  - `fav`, `fav add <name>`, `fav rm <name>`
- Settings (persisted)
  - `settings`, `settings off`
  - `set prompt …`, `set font …`, `set phosphor …`, `set accent …`
- System info widgets
  - header shows BAT/NET/TIME (+ notification count)
  - `status`
- Notifications capture (optional)
  - `notifs`, `notifs on`, `notifs clear`
  - requires enabling notification access in Android settings
- Media volume control
  - `volume <0-100>`

### Notes / limitations (Android OS)

- Wi‑Fi/Bluetooth toggles are restricted for normal apps on modern Android.
- Brightness is OS-controlled; the launcher can open display settings via `brightness`.

## 💡 Command System (examples)

```bash
help
status
apps
apps mail
open chrome
fav add gmail
fav
set font 18
notifs on
notifs
volume 50
clear
```

---

## 🧱 Architecture (Launcher)

```bash
launcher/
├── app/           # Android entry
├── core/          # command engine
├── ui/            # terminal renderer
├── widgets/       # system modules
├── input/         # keyboard handling
└── assets/
```

---

## 🧪 CI (APK builds)

GitHub Actions builds a **debug APK artifact** on pushes/PRs that touch `launcher/`.

---

## 🧠 UX Philosophy

* zero clutter
* text-first interface
* keyboard-first navigation
* no icons required (optional)

---

## 📱 Target Devices

Primary:

* Unihertz Titan 2

Secondary:

* any Android device

---

## 🔮 Roadmap

### Phase 1

* ✅ theme kit
* ✅ KWGT presets

### Phase 2

* ✅ launcher prototype
* ✅ command parser + app launch
* ✅ basic UI + history/favorites/settings

### Phase 3

* APK release
* full terminal UX
* plugin system

---

## 🧑‍💻 Author

Mehmet Turgay Akalın
[https://github.com/makalin](https://github.com/makalin)

---

## 🧾 License

MIT
