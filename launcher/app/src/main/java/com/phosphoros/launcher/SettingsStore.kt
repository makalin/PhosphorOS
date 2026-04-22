package com.phosphoros.launcher

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "phosphoros_settings")

data class LauncherSettings(
    val prompt: String = "phosphoros@android:~$",
    val fontSizeSp: Int = 16,
    val phosphorHex: String = "#00FF9C",
    val accentHex: String = "#FFC857",
)

class SettingsStore(private val context: Context) {
    private object Keys {
        val PROMPT = stringPreferencesKey("prompt")
        val FONT_SIZE = intPreferencesKey("font_size_sp")
        val PHOSPHOR = stringPreferencesKey("phosphor_hex")
        val ACCENT = stringPreferencesKey("accent_hex")
    }

    val settings: Flow<LauncherSettings> = context.dataStore.data.map { p ->
        LauncherSettings(
            prompt = p[Keys.PROMPT] ?: LauncherSettings().prompt,
            fontSizeSp = p[Keys.FONT_SIZE] ?: LauncherSettings().fontSizeSp,
            phosphorHex = p[Keys.PHOSPHOR] ?: LauncherSettings().phosphorHex,
            accentHex = p[Keys.ACCENT] ?: LauncherSettings().accentHex,
        )
    }

    suspend fun update(block: (LauncherSettings) -> LauncherSettings) {
        context.dataStore.edit { prefs ->
            val current = prefs.toSettings()
            val next = block(current)
            prefs[Keys.PROMPT] = next.prompt
            prefs[Keys.FONT_SIZE] = next.fontSizeSp
            prefs[Keys.PHOSPHOR] = next.phosphorHex
            prefs[Keys.ACCENT] = next.accentHex
        }
    }

    private fun Preferences.toSettings(): LauncherSettings {
        return LauncherSettings(
            prompt = this[Keys.PROMPT] ?: LauncherSettings().prompt,
            fontSizeSp = this[Keys.FONT_SIZE] ?: LauncherSettings().fontSizeSp,
            phosphorHex = this[Keys.PHOSPHOR] ?: LauncherSettings().phosphorHex,
            accentHex = this[Keys.ACCENT] ?: LauncherSettings().accentHex,
        )
    }
}

