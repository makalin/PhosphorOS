package com.phosphoros.launcher

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.provider.Settings
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class CommandResult(
    val lines: List<String>,
    val clearScreen: Boolean = false,
    val openSettings: Boolean = false,
    val closeSettings: Boolean = false,
)

class CommandProcessor(
    private val context: Context,
    private val appIndex: AppIndex,
    private val favoritesStore: FavoritesStore,
    private val settingsStore: SettingsStore,
) {
    private val pm = context.packageManager
    private val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val aliases: Map<String, String> = mapOf(
        "ls" to "apps",
        "cls" to "clear",
        "?" to "help",
    )

    fun run(raw: String): CommandResult {
        val input = raw.trim()
        if (input.isEmpty()) return CommandResult(lines = emptyList())

        val parts = input.split(Regex("\\s+"))
        val cmd0 = parts.first().lowercase()
        val cmd = aliases[cmd0] ?: cmd0
        val args = parts.drop(1)

        return when (cmd) {
            "help" -> CommandResult(
                lines = listOf(
                    "commands:",
                    "  help                 show this help",
                    "  clear                clear screen",
                    "  status               device info",
                    "  apps [filter]         list launchable apps (optional filter)",
                    "  open <name>           launch an app by name (fuzzy)",
                    "  fav                   list favorites",
                    "  fav add <name>        add favorite by app name",
                    "  fav rm <name>         remove favorite by label",
                    "  volume <0-100>        set media volume percent",
                    "  brightness            open display settings (OS limited)",
                    "  settings              open launcher settings UI",
                    "  set <k> <v>           set: prompt/font/phosphor/accent",
                    "  wifi/bt               show OS limitations",
                    "  notifs                list recent notifications (needs permission)",
                    "  notifs on             open notification access settings",
                    "  notifs clear          clear captured notifications",
                ),
            )

            "clear" -> CommandResult(lines = emptyList(), clearScreen = true)

            "status" -> {
                val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                val snap = SystemInfo.snapshot(context)
                CommandResult(
                    lines = listOf(
                        "time: $now",
                        "device: ${Build.MANUFACTURER} ${Build.MODEL}",
                        "android: ${Build.VERSION.RELEASE} (sdk ${Build.VERSION.SDK_INT})",
                        "bat: ${snap.batteryPercent?.let { "$it%" } ?: "?"}",
                        "net: ${snap.network}",
                    ),
                )
            }

            "apps" -> {
                val filter = args.joinToString(" ").trim().lowercase()
                val apps = appIndex.list()
                    .filter { a -> filter.isEmpty() || a.label.lowercase().contains(filter) }
                    .take(50)
                    .map { a -> "${a.label}  (${a.packageName})" }
                CommandResult(lines = if (apps.isEmpty()) listOf("no apps found") else apps)
            }

            "open" -> {
                val query = args.joinToString(" ").trim()
                if (query.isEmpty()) {
                    CommandResult(lines = listOf("usage: open <name>"))
                } else {
                    val allApps = appIndex.list()
                    val labels = allApps.map { it.label }
                    val matchedLabel = Fuzzy.bestMatch(query, labels)
                    val match = allApps.firstOrNull { it.label == matchedLabel }
                    if (match == null) {
                        CommandResult(lines = listOf("not found: $query"))
                    } else {
                        val launchIntent = Intent(Intent.ACTION_MAIN).apply {
                            addCategory(Intent.CATEGORY_LAUNCHER)
                            setClassName(match.packageName, match.activityName)
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(launchIntent)
                        CommandResult(lines = listOf("opening: ${match.label}"))
                    }
                }
            }

            "fav" -> {
                val sub = args.firstOrNull()?.lowercase()
                val rest = args.drop(1).joinToString(" ").trim()

                when (sub) {
                    null, "list" -> {
                        val favs = runBlocking { favoritesStore.favorites.firstValue() }
                        CommandResult(lines = if (favs.isEmpty()) listOf("no favorites") else favs.map { it.label })
                    }

                    "add" -> {
                        if (rest.isBlank()) return CommandResult(lines = listOf("usage: fav add <name>"))
                        val allApps = appIndex.list()
                        val matchedLabel = Fuzzy.bestMatch(rest, allApps.map { it.label })
                        val app = allApps.firstOrNull { it.label == matchedLabel }
                            ?: return CommandResult(lines = listOf("not found: $rest"))
                        runBlocking { favoritesStore.add(Favorite(app.label, app.packageName, app.activityName)) }
                        CommandResult(lines = listOf("favorite added: ${app.label}"))
                    }

                    "rm", "remove", "del" -> {
                        if (rest.isBlank()) return CommandResult(lines = listOf("usage: fav rm <name>"))
                        runBlocking { favoritesStore.removeByLabel(rest) }
                        CommandResult(lines = listOf("favorite removed: $rest"))
                    }

                    else -> CommandResult(lines = listOf("usage: fav [list|add|rm]"))
                }
            }

            "volume" -> {
                val v = args.firstOrNull()?.toIntOrNull()
                if (v == null || v !in 0..100) return CommandResult(lines = listOf("usage: volume <0-100>"))
                val max = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val set = ((v / 100f) * max).toInt().coerceIn(0, max)
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, set, 0)
                CommandResult(lines = listOf("volume: $v%"))
            }

            "brightness" -> {
                // Non-system apps can't reliably set global brightness on modern Android without special permission.
                val i = Intent(Settings.ACTION_DISPLAY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(i)
                CommandResult(lines = listOf("opened display settings (brightness is OS-controlled)"))
            }

            "wifi", "bt", "bluetooth" -> CommandResult(
                lines = listOf(
                    "not supported: Android restricts toggling Wi‑Fi/Bluetooth for normal apps on modern versions.",
                    "tip: use quick settings tiles, or make this app a privileged/system app to control radios.",
                ),
            )

            "notifs", "notifications" -> {
                val sub = args.firstOrNull()?.lowercase()
                when (sub) {
                    "on", "enable" -> {
                        val i = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(i)
                        CommandResult(lines = listOf("open settings: enable PhosphorOS notification access"))
                    }
                    "clear" -> {
                        NotificationsRepo.clear()
                        CommandResult(lines = listOf("notifications cleared"))
                    }
                    null, "list" -> {
                        val items = NotificationsRepo.items.value.take(10)
                        if (items.isEmpty()) {
                            CommandResult(lines = listOf("no notifications (or permission not granted)"))
                        } else {
                            CommandResult(
                                lines = items.map { n ->
                                    val head = listOf(n.title, n.text).firstOrNull { it.isNotBlank() }.orEmpty()
                                    "${n.from}: $head"
                                },
                            )
                        }
                    }
                    else -> CommandResult(lines = listOf("usage: notifs [list|on|clear]"))
                }
            }

            "settings" -> {
                if (args.firstOrNull()?.lowercase() == "off") {
                    CommandResult(lines = listOf("settings hidden"), closeSettings = true)
                } else {
                    CommandResult(lines = listOf("opening settings…"), openSettings = true)
                }
            }

            "set" -> {
                val key = args.firstOrNull()?.lowercase()
                val value = args.drop(1).joinToString(" ").trim()
                if (key == null || value.isBlank()) {
                    return CommandResult(lines = listOf("usage: set <prompt|font|phosphor|accent> <value>"))
                }
                when (key) {
                    "prompt" -> {
                        runBlocking { settingsStore.update { it.copy(prompt = value) } }
                        CommandResult(lines = listOf("prompt updated"), openSettings = true)
                    }
                    "font" -> {
                        val n = value.toIntOrNull()
                        if (n == null || n !in 12..28) return CommandResult(lines = listOf("font must be 12..28"))
                        runBlocking { settingsStore.update { it.copy(fontSizeSp = n) } }
                        CommandResult(lines = listOf("font updated: ${n}sp"), openSettings = true)
                    }
                    "phosphor" -> {
                        if (!value.matches(Regex("#[0-9a-fA-F]{6}"))) {
                            return CommandResult(lines = listOf("usage: set phosphor #RRGGBB"))
                        }
                        runBlocking { settingsStore.update { it.copy(phosphorHex = value.uppercase()) } }
                        CommandResult(lines = listOf("phosphor updated"), openSettings = true)
                    }
                    "accent" -> {
                        if (!value.matches(Regex("#[0-9a-fA-F]{6}"))) {
                            return CommandResult(lines = listOf("usage: set accent #RRGGBB"))
                        }
                        runBlocking { settingsStore.update { it.copy(accentHex = value.uppercase()) } }
                        CommandResult(lines = listOf("accent updated"), openSettings = true)
                    }
                    else -> CommandResult(lines = listOf("unknown setting: $key"))
                }
            }

            else -> CommandResult(lines = listOf("unknown command: $cmd (try: help)"))
        }
    }
}

