package com.phosphoros.launcher

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.favoritesDataStore by preferencesDataStore(name = "phosphoros_favorites")

data class Favorite(
    val label: String,
    val packageName: String,
    val activityName: String,
)

class FavoritesStore(private val context: Context) {
    private object Keys {
        val FAVORITES = stringSetPreferencesKey("favorites")
    }

    val favorites: Flow<List<Favorite>> = context.favoritesDataStore.data.map { prefs ->
        (prefs[Keys.FAVORITES] ?: emptySet())
            .mapNotNull { encoded -> decode(encoded) }
            .sortedBy { it.label.lowercase() }
    }

    suspend fun add(fav: Favorite) {
        context.favoritesDataStore.edit { prefs ->
            val set = (prefs[Keys.FAVORITES] ?: emptySet()).toMutableSet()
            set.add(encode(fav))
            prefs[Keys.FAVORITES] = set
        }
    }

    suspend fun removeByLabel(label: String) {
        val q = label.trim().lowercase()
        context.favoritesDataStore.edit { prefs ->
            val set = (prefs[Keys.FAVORITES] ?: emptySet())
                .filterNot { decode(it)?.label?.lowercase() == q }
                .toSet()
            prefs[Keys.FAVORITES] = set
        }
    }

    private fun encode(f: Favorite): String = "${f.label}||${f.packageName}||${f.activityName}"

    private fun decode(s: String): Favorite? {
        val parts = s.split("||")
        if (parts.size != 3) return null
        val (label, pkg, act) = parts
        if (label.isBlank() || pkg.isBlank() || act.isBlank()) return null
        return Favorite(label = label, packageName = pkg, activityName = act)
    }
}

