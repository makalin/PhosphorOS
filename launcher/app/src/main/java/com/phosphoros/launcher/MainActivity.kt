package com.phosphoros.launcher

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.KeyboardActions
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val bg = Color.Black

            val ctx = LocalContext.current
            val settingsStore = remember { SettingsStore(ctx) }
            val favoritesStore = remember { FavoritesStore(ctx) }
            val appIndex = remember { AppIndex(ctx) }

            val settings by settingsStore.settings.collectAsStateWithLifecycle(initialValue = LauncherSettings())
            val phosphor = remember(settings.phosphorHex) { Color(parseColor(settings.phosphorHex)) }
            val accent = remember(settings.accentHex) { Color(parseColor(settings.accentHex)) }

            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = bg,
                    surface = bg,
                    onBackground = phosphor,
                    onSurface = phosphor,
                    primary = phosphor,
                ),
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = bg) {
                    val processor = remember { CommandProcessor(ctx, appIndex, favoritesStore, settingsStore) }
                    val log = remember { mutableStateListOf("${settings.prompt}  (type: help)") }
                    var input by remember { mutableStateOf(TextFieldValue("")) }
                    val scrollState = rememberScrollState()

                    // Command history
                    val history = remember { mutableStateListOf<String>() }
                    var historyIdx by remember { mutableStateOf(-1) }

                    // Settings UI toggle
                    var showSettings by remember { mutableStateOf(false) }

                    // Suggestions
                    val baseCommands = remember {
                        listOf(
                            "help", "clear", "status", "apps", "apps ", "open ",
                            "fav", "fav add ", "fav rm ", "volume ", "brightness", "settings",
                            "set prompt ", "set font ", "set phosphor ", "set accent ",
                        )
                    }
                    val favorites by favoritesStore.favorites.collectAsStateWithLifecycle(initialValue = emptyList())
                    val apps = remember { appIndex.list() }
                    val notifs by NotificationsRepo.items.collectAsStateWithLifecycle()

                    var header by remember { mutableStateOf("") }
                    LaunchedEffect(notifs.size) {
                        while (true) {
                            val snap = SystemInfo.snapshot(ctx)
                            val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                            header = "BAT:${snap.batteryPercent?.let { "$it%" } ?: "?"}  NET:${snap.network}  TIME:$now  N:${notifs.size}"
                            delay(30_000)
                        }
                    }

                    fun submitCommand() {
                        val raw = input.text
                        val trimmed = raw.trim()
                        if (trimmed.isEmpty()) return

                        log.add("> $trimmed")
                        history.add(trimmed)
                        historyIdx = history.size

                        val result = processor.run(trimmed)
                        if (result.clearScreen) {
                            log.clear()
                            log.add("${settings.prompt}  (cleared)")
                        }
                        result.lines.forEach { line -> log.add(line) }
                        if (result.openSettings) showSettings = true
                        if (result.closeSettings) showSettings = false

                        input = TextFieldValue("", selection = TextRange(0))
                    }

                    LaunchedEffect(log.size) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bg)
                            .verticalScroll(scrollState)
                            .padding(PaddingValues(16.dp)),
                    ) {
                        // Header widgets
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = header,
                                color = accent,
                                style = TextStyle(fontSize = (settings.fontSizeSp - 2).coerceAtLeast(12).sp),
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = log.joinToString("\n"),
                            color = phosphor,
                            style = TextStyle(fontSize = settings.fontSizeSp.sp),
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Suggestions (very lightweight)
                        val current = input.text
                        val suggestions = remember(current, favorites, apps) {
                            val q = current.trim().lowercase()
                            if (q.isEmpty()) emptyList()
                            else {
                                val cand = buildList {
                                    addAll(baseCommands)
                                    addAll(favorites.map { "open ${it.label}" })
                                    addAll(apps.take(200).map { "open ${it.label}" })
                                }
                                cand.filter { it.lowercase().contains(q) }.distinct().take(5)
                            }
                        }
                        suggestions.forEach { s ->
                            Text(
                                text = s,
                                color = phosphor.copy(alpha = 0.55f),
                                style = TextStyle(fontSize = (settings.fontSizeSp - 2).coerceAtLeast(12).sp),
                            )
                        }
                        if (suggestions.isNotEmpty()) Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = input,
                            onValueChange = { input = it.copy(selection = TextRange(it.text.length)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusable(true),
                                .onPreviewKeyEvent { e ->
                                    if (e.nativeKeyEvent.action != KeyEvent.ACTION_DOWN) return@onPreviewKeyEvent false
                                    when (e.nativeKeyEvent.keyCode) {
                                        KeyEvent.KEYCODE_DPAD_UP -> {
                                            if (history.isNotEmpty()) {
                                                historyIdx = (historyIdx - 1).coerceAtLeast(0)
                                                val t = history.getOrNull(historyIdx).orEmpty()
                                                input = TextFieldValue(t, selection = TextRange(t.length))
                                            }
                                            true
                                        }
                                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                                            if (history.isNotEmpty()) {
                                                historyIdx = (historyIdx + 1).coerceAtMost(history.size)
                                                val t = if (historyIdx in history.indices) history[historyIdx] else ""
                                                input = TextFieldValue(t, selection = TextRange(t.length))
                                            }
                                            true
                                        }
                                        KeyEvent.KEYCODE_ENTER -> {
                                            submitCommand()
                                            true
                                        }
                                        else -> false
                                    }
                                },
                            textStyle = TextStyle(color = phosphor, fontSize = settings.fontSizeSp.sp),
                            placeholder = { Text("command…", color = phosphor.copy(alpha = 0.5f)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { submitCommand() }),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = phosphor.copy(alpha = 0.7f),
                                unfocusedBorderColor = phosphor.copy(alpha = 0.25f),
                                cursorColor = phosphor,
                            ),
                        )

                        if (showSettings) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("settings:", color = accent, style = TextStyle(fontSize = settings.fontSizeSp.sp))
                            Text(
                                "prompt: ${settings.prompt}",
                                color = phosphor,
                                style = TextStyle(fontSize = settings.fontSizeSp.sp),
                            )
                            Text(
                                "font: ${settings.fontSizeSp}sp   phosphor: ${settings.phosphorHex}   accent: ${settings.accentHex}",
                                color = phosphor,
                                style = TextStyle(fontSize = (settings.fontSizeSp - 2).coerceAtLeast(12).sp),
                            )
                            Text(
                                "use: set prompt <text> | set font <n> | set phosphor <#RRGGBB> | set accent <#RRGGBB> | settings off",
                                color = phosphor.copy(alpha = 0.7f),
                                style = TextStyle(fontSize = (settings.fontSizeSp - 2).coerceAtLeast(12).sp),
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun parseColor(hex: String): Int {
    val cleaned = hex.trim().removePrefix("#")
    val v = cleaned.toLongOrNull(16) ?: 0x00FF9C
    return (0xFF000000 or v).toInt()
}

